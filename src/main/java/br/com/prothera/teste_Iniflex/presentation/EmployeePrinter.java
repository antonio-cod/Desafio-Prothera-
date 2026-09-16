package br.com.prothera.teste_Iniflex.presentation;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;
import br.com.prothera.teste_Iniflex.useCases.ExecuteChallengeUseCase.Result;
import br.com.prothera.teste_Iniflex.useCases.ExecuteChallengeUseCase.Snapshot;
import br.com.prothera.teste_Iniflex.useCases.EmployeeReportsUseCase;
import java.util.Locale;
import org.springframework.stereotype.Component;
import br.com.prothera.teste_Iniflex.entity.Employee;

@Component
public class EmployeePrinter {
    private final EmployeeReportsUseCase reports;

    public EmployeePrinter(EmployeeReportsUseCase reports) {
        this.reports = reports;
    }

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String number(BigDecimal value) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(value);
    }

    public String format(Employee employee) {
        return "%s | %s | %s | %s".formatted(employee.getName(),
            employee.getDateOfBirth().format(DATE), number(employee.getSalary()), employee.getJobFunction());
    }

    public void title(String title) {
        System.out.println("\n" + title);
    }

    public void employees(List<Employee> employees) {
        System.out.println("Nome | Data nascimento | Salário | Função");
        if (employees.isEmpty()) System.out.println("Nenhum funcionário encontrado.");
        employees.forEach(employee -> System.out.println(format(employee)));
    }

    public void database(String url, String path) {
        title("Banco de dados utilizado");
        System.out.println("URL ativa: " + url);
        System.out.println("Arquivo: " + (path == null ? "banco em memória (sem arquivo)" : path + ".mv.db"));
        System.out.println("Tabela: employee");
    }

    private void snapshots(List<Snapshot> employees) {
        System.out.println("Nome | Data nascimento | Salário | Função");
        employees.forEach(e -> System.out.printf("%s | %s | %s | %s%n",
            e.name(), e.birth().format(DATE), number(e.salary()), e.function()));
    }

    public void challenge(Result result, List<Employee> employees, LocalDate referenceDate) {
        title("3.1 – Inserir todos os funcionários, na mesma ordem e informações da tabela acima.");
        if (result.initialized()) {
            System.out.println("10 funcionários inseridos; carga inicial concluída e persistida.");
            snapshots(result.inserted());
        } else {
            System.out.println("Inserção não repetida: registros existentes preservados. Referência da carga original da imagem:");
            snapshots(result.inserted());
        }

        title("3.2 – Remover o funcionário “João” da lista.");
        if (result.initialized()) {
            System.out.printf("João removido da lista e do banco. Restantes: %d na lista e %d no banco.%n",
                result.beforeIncrease().size(), employees.size());
            snapshots(result.beforeIncrease());
        } else {
            System.out.println("Remoção não repetida. João está "
                + (employees.stream().anyMatch(e -> e.getName().equals("João")) ? "presente" : "ausente")
                + " no banco.");
            System.out.println("Referência da etapa 3.2: lista original após remover João, antes do aumento:");
            snapshots(result.beforeIncrease());
        }

        title("3.3 – Imprimir todos os funcionários com todas suas informações, sendo que:");
        System.out.println("• informação de data deve ser exibido no formato dd/mm/aaaa;");
        System.out.println("• informação de valor numérico deve ser exibida no formatado com separador de milhar como ponto e decimal como vírgula.");
        if (result.initialized()) {
            System.out.println("Salários antes do aumento:");
            snapshots(result.beforeIncrease());
        } else {
            System.out.println("Salários atuais persistidos:");
            employees(employees);
        }

        title("3.4 – Os funcionários receberam 10% de aumento de salário, atualizar a lista de funcionários com novo valor.");
        if (result.initialized()) {
            System.out.println("Aumento aplicado e persistido. Nome | Salário anterior | Salário atualizado");
            for (var before : result.beforeIncrease()) {
                var after = employees.stream().filter(e -> e.getId().equals(before.id())).findFirst().orElseThrow();
                System.out.printf("%s | %s | %s%n", before.name(), number(before.salary()), number(after.getSalary()));
            }
        } else {
            System.out.println("Aumento não reaplicado. Salários atuais preservados:");
            employees(employees);
        }

        title("3.5 – Agrupar os funcionários por função em um MAP, sendo a chave a “função” e o valor a “lista de funcionários”.");
        var groups = reports.groupByFunction(employees);
        System.out.println("Função | Quantidade de funcionários");
        groups.forEach((function, group) -> System.out.printf("%s | %d%n", function, group.size()));

        title("3.6 – Imprimir os funcionários, agrupados por função.");
        groups.forEach((function, group) -> { title(function); employees(group); });

        title("3.8 – Imprimir os funcionários que fazem aniversário no mês 10 e 12.");
        var birthdays = reports.birthdays(employees);
        for (int month : new int[]{10, 12}) {
            title(month == 10 ? "Outubro (10)" : "Dezembro (12)");
            var matching = birthdays.stream().filter(e -> e.getDateOfBirth().getMonthValue() == month).toList();
            if (matching.isEmpty()) System.out.println("Nenhum aniversariante neste mês.");
            else employees(matching);
        }

        title("3.9 – Imprimir o funcionário com a maior idade, exibir os atributos: nome e idade.");
        System.out.println("Nome | Idade");
        reports.oldest(employees).ifPresentOrElse(e -> System.out.printf("%s | %d anos%n",
            e.getName(), reports.age(e, referenceDate)), () -> System.out.println("Nenhum funcionário encontrado."));

        title("3.10 – Imprimir a lista de funcionários por ordem alfabética.");
        employees(reports.alphabetical(employees));

        title("3.11 – Imprimir o total dos salários dos funcionários.");
        System.out.println(number(reports.totalSalary(employees)));

        title("3.12 – Imprimir quantos salários mínimos ganha cada funcionário, considerando que o salário mínimo é R$1212.00.");
        System.out.println("Salário mínimo: R$ 1.212,00. Nome | Quantidade de salários mínimos");
        employees.forEach(e -> System.out.printf("%s | %s%n", e.getName(), number(reports.minimumWages(e))));
    }
}
