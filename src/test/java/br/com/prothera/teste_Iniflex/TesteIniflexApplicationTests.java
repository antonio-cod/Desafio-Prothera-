package br.com.prothera.teste_Iniflex;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import br.com.prothera.teste_Iniflex.entity.Employee;
import br.com.prothera.teste_Iniflex.presentation.EmployeePrinter;
import br.com.prothera.teste_Iniflex.repository.EmployeeRepository;
import br.com.prothera.teste_Iniflex.useCases.*;
import static org.assertj.core.api.Assertions.*;

@org.junit.jupiter.api.extension.ExtendWith(org.springframework.boot.test.system.OutputCaptureExtension.class)
class TesteIniflexApplicationTests {
    @Test
    void executesChallengeAndRecreatesOriginalDataOnRestart() {
        for (int startup = 0; startup < 2; startup++) {
            try (var context = new SpringApplicationBuilder(TesteIniflexApplication.class)
                    .web(WebApplicationType.NONE)
                    .run("--spring.datasource.url=jdbc:h2:mem:challenge-test;DB_CLOSE_DELAY=-1",
                         "--spring.jpa.hibernate.ddl-auto=create-drop",
                         "--logging.level.root=WARN", "--spring.main.banner-mode=off")) {
                var repository = context.getBean(EmployeeRepository.class);
                var reports = context.getBean(EmployeeReportsUseCase.class);
                var printer = context.getBean(EmployeePrinter.class);
                var employees = repository.findAllByOrderByInsertionOrderAsc();
                assertThat(employees).extracting(Employee::getName).containsExactly(
                    "Maria", "Caio", "Miguel", "Alice", "Heitor", "Arthur", "Laura", "Heloísa", "Helena");
                assertThat(employees).extracting(e -> e.getSalary().toPlainString()).containsExactly(
                    "2210.38", "10819.75", "21031.87", "2458.15", "1740.99", "4479.02",
                    "3319.20", "1767.54", "3079.92");
                assertThat(reports.totalSalary(employees)).isEqualByComparingTo("50906.82");
                assertThat(employees).extracting(e -> reports.minimumWages(e).toPlainString())
                    .containsExactly("1.82", "8.93", "17.35", "2.03", "1.44", "3.70", "2.74", "1.46", "2.54");
                var groups = reports.groupByFunction(employees);
                assertThat(groups).hasSize(7);
                assertThat(groups.get("Operador")).extracting(Employee::getName).containsExactly("Maria", "Heitor");
                assertThat(groups.get("Gerente")).extracting(Employee::getName).containsExactly("Laura", "Helena");
                assertThat(groups.values().stream().mapToInt(List::size).sum()).isEqualTo(9);
                assertThat(reports.birthdays(employees)).extracting(Employee::getName).containsExactly("Maria", "Miguel");
                var oldest = reports.oldest(employees).orElseThrow();
                assertThat(oldest.getName()).isEqualTo("Caio");
                assertThat(reports.age(oldest, LocalDate.of(2026, 5, 1))).isEqualTo(64);
                assertThat(reports.age(oldest, LocalDate.of(2026, 5, 2))).isEqualTo(65);
                assertThat(reports.alphabetical(employees)).extracting(Employee::getName).containsExactly(
                    "Alice", "Arthur", "Caio", "Heitor", "Helena", "Heloísa", "Laura", "Maria", "Miguel");
                assertThat(employees.get(0).getName()).isEqualTo("Maria");
                assertThat(printer.format(employees.get(0))).isEqualTo("Maria | 18/10/2000 | 2.210,38 | Operador");
                assertThatThrownBy(() -> context.getBean(InitializeEmployeesUseCase.class).execute())
                    .isInstanceOf(IllegalStateException.class);

                // Inspeciona a carga original, antes da remoção e do aumento.
                repository.deleteAllInBatch();
                var original = context.getBean(InitializeEmployeesUseCase.class).execute();
                assertThat(original).extracting(Employee::getInsertionOrder).containsExactly(1,2,3,4,5,6,7,8,9,10);
                assertThat(original).extracting(printer::format).containsExactly(
                    "Maria | 18/10/2000 | 2.009,44 | Operador",
                    "João | 12/05/1990 | 2.284,38 | Operador",
                    "Caio | 02/05/1961 | 9.836,14 | Coordenador",
                    "Miguel | 14/10/1988 | 19.119,88 | Diretor",
                    "Alice | 05/01/1995 | 2.234,68 | Recepcionista",
                    "Heitor | 19/11/1999 | 1.582,72 | Operador",
                    "Arthur | 31/03/1993 | 4.071,84 | Contador",
                    "Laura | 08/07/1994 | 3.017,45 | Gerente",
                    "Heloísa | 24/05/2003 | 1.606,85 | Eletricista",
                    "Helena | 02/09/1996 | 2.799,93 | Gerente");
                var joao = original.get(1);
                context.getBean(UpdateEmployeesUseCase.class).remove(joao.getId());
                assertThat(repository.existsById(joao.getId())).isFalse();
                assertThat(repository.count()).isEqualTo(9);
                assertThat(reports.totalSalary(repository.findAllByOrderByInsertionOrderAsc()))
                    .isEqualByComparingTo("46278.93");
            }
        }
    }

    @Test
    void fileDatabasePreservesEmployeesAndIdsAcrossRestarts(@org.junit.jupiter.api.io.TempDir java.nio.file.Path directory,
            org.springframework.boot.test.system.CapturedOutput output) {
        String url = "jdbc:h2:file:" + directory.resolve("iniflex").toAbsolutePath();
        List<java.util.UUID> originalIds = null;
        int outputOffset = output.getOut().length();
        for (int startup = 0; startup < 2; startup++) {
            try (var context = new SpringApplicationBuilder(TesteIniflexApplication.class)
                    .web(WebApplicationType.NONE)
                    .run("--spring.datasource.url=" + url, "--spring.jpa.hibernate.ddl-auto=update",
                         "--logging.level.root=WARN", "--spring.main.banner-mode=off")) {
                var repository = context.getBean(EmployeeRepository.class);
                var employees = repository.findAllByOrderByInsertionOrderAsc();
                assertThat(employees).hasSize(9);
                assertThat(employees).extracting(Employee::getName).doesNotContain("João");
                assertThat(context.getBean(EmployeeReportsUseCase.class).totalSalary(employees))
                    .isEqualByComparingTo("50906.82");
                String console = output.getOut().substring(outputOffset);
                outputOffset = output.getOut().length();
                int previous = -1;
                for (String item : List.of("3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.8", "3.9", "3.10", "3.11", "3.12")) {
                    int position = console.indexOf(item + " – ");
                    assertThat(position).as("Título %s em ordem", item).isGreaterThan(previous);
                    previous = position;
                }
                assertThat(console).contains("URL ativa: " + url, directory.resolve("iniflex.mv.db").toString(),
                    "Tabela: employee", "Dezembro (12)", "Nenhum aniversariante neste mês.", "50.906,82",
                    "3.6 – Imprimir os funcionários, agrupados por função.",
                    "3.10 – Imprimir a lista de funcionários por ordem alfabética.");
                if (startup == 0) {
                    assertThat(console).contains("10 funcionários inseridos", "9 na lista e 9 no banco",
                        "Maria | 18/10/2000 | 2.009,44 | Operador", "Maria | 2.009,44 | 2.210,38");
                } else {
                    assertThat(console).contains("Inserção não repetida", "Remoção não repetida. João está ausente",
                        "Salários atuais persistidos:", "Aumento não reaplicado")
                        .doesNotContain("Salários antes do aumento:", "Aumento aplicado e persistido.");
                }
                String insertion = console.substring(console.indexOf("3.1 –"), console.indexOf("3.2 –"));
                String removal = console.substring(console.indexOf("3.2 –"), console.indexOf("3.3 –"));
                assertThat(insertion).contains("Maria | 18/10/2000 | 2.009,44 | Operador",
                    "João | 12/05/1990 | 2.284,38 | Operador",
                    "Caio | 02/05/1961 | 9.836,14 | Coordenador");
                assertThat(insertion.indexOf("Maria |" )).isLessThan(insertion.indexOf("João |"));
                assertThat(insertion.indexOf("João |" )).isLessThan(insertion.indexOf("Caio |"));
                assertThat(insertion.lines().filter(line -> line.matches(".*[0-9]{2}/[0-9]{2}/[0-9]{4}.*")).count()).isEqualTo(10);
                assertThat(removal).doesNotContain("João | 12/05/1990")
                    .contains("Maria | 18/10/2000 | 2.009,44 | Operador");
                assertThat(removal.lines().filter(line -> line.matches(".*[0-9]{2}/[0-9]{2}/[0-9]{4}.*")).count()).isEqualTo(9);
                var ids = employees.stream().map(Employee::getId).toList();
                if (startup == 0) {
                    originalIds = ids;
                } else {
                    assertThat(ids).containsExactlyElementsOf(originalIds);
                }
            }
            assertThat(directory.resolve("iniflex.mv.db")).exists();
        }
    }

    @Test
    void rollsBackInitialActionsAndPreservesUnrelatedExistingData() {
        try (var context = new SpringApplicationBuilder(TesteIniflexApplication.class)
                .web(WebApplicationType.NONE)
                .run("--spring.datasource.url=jdbc:h2:mem:rollback-test",
                     "--spring.jpa.hibernate.ddl-auto=create-drop", "--logging.level.root=WARN")) {
            var repository = context.getBean(EmployeeRepository.class);
            repository.deleteAllInBatch();
            var transaction = new org.springframework.transaction.support.TransactionTemplate(
                context.getBean(org.springframework.transaction.PlatformTransactionManager.class));
            assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
                context.getBean(ExecuteChallengeUseCase.class).execute();
                repository.flush();
                throw new IllegalStateException("Falha simulada antes do commit");
            })).isInstanceOf(IllegalStateException.class);
            assertThat(repository.count()).isZero();

            var existing = repository.save(new Employee("João", LocalDate.of(1990, 5, 12),
                new java.math.BigDecimal("1500.00"), "Operador", 2));
            var execution = context.getBean(ExecuteChallengeUseCase.class).execute();
            assertThat(execution.initialized()).isFalse();
            var unchanged = repository.findById(existing.getId()).orElseThrow();
            assertThat(unchanged.getSalary()).isEqualByComparingTo("1500.00");
            assertThat(repository.count()).isEqualTo(1);
        }
    }

    @Test
    void reportsHandleDecemberBirthdaysAndEmptyLists() {
        var reports = new EmployeeReportsUseCase();
        var december = new Employee("Dezembro", LocalDate.of(2000, 12, 31),
            new java.math.BigDecimal("1212.00"), "Operador", 1);
        assertThat(reports.birthdays(List.of(december))).containsExactly(december);
        assertThat(reports.minimumWages(december)).isEqualByComparingTo("1.00");
        assertThat(reports.oldest(List.of())).isEmpty();
        assertThat(reports.groupByFunction(List.of())).isEmpty();
        assertThat(reports.totalSalary(List.of())).isEqualByComparingTo("0.00");
    }
}
