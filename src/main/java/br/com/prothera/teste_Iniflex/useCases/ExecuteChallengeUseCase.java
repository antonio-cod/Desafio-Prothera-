package br.com.prothera.teste_Iniflex.useCases;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.prothera.teste_Iniflex.entity.Employee;
import br.com.prothera.teste_Iniflex.repository.EmployeeRepository;

@Service
public class ExecuteChallengeUseCase {
    public record Snapshot(UUID id, String name, LocalDate birth, BigDecimal salary, String function) {
        public static Snapshot of(Employee employee) {
            return new Snapshot(employee.getId(), employee.getName(), employee.getDateOfBirth(),
                employee.getSalary(), employee.getJobFunction());
        }
    }

    public record Result(boolean initialized, List<Snapshot> inserted,
                         List<Snapshot> beforeIncrease, Snapshot removed) {
        public Result {
            inserted = List.copyOf(inserted);
            beforeIncrease = List.copyOf(beforeIncrease);
        }
    }

    private final EmployeeRepository repository;
    private final InitializeEmployeesUseCase initialize;
    private final UpdateEmployeesUseCase update;

    public ExecuteChallengeUseCase(EmployeeRepository repository, InitializeEmployeesUseCase initialize,
                                   UpdateEmployeesUseCase update) {
        this.repository = repository;
        this.initialize = initialize;
        this.update = update;
    }

    @Transactional
    public Result execute() {
        if (repository.count() > 0) {
            var original = initialize.originalEmployees().stream().map(Snapshot::of).toList();
            var withoutJoao = original.stream().filter(e -> !e.name().equals("João")).toList();
            return new Result(false, original, withoutJoao, null);
        }
        var employees = new ArrayList<>(initialize.execute());
        var inserted = employees.stream().map(Snapshot::of).toList();
        var joao = employees.stream().filter(e -> e.getName().equals("João")).findFirst().orElseThrow();
        update.remove(joao.getId());
        employees.remove(joao);
        var before = employees.stream().map(Snapshot::of).toList();
        update.increaseSalaries();
        return new Result(true, inserted, before, Snapshot.of(joao));
    }
}
