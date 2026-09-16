package br.com.prothera.teste_Iniflex.useCases;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.prothera.teste_Iniflex.entity.Employee;
import br.com.prothera.teste_Iniflex.repository.EmployeeRepository;

@Service
public class InitializeEmployeesUseCase {
    private final EmployeeRepository repository;

    public InitializeEmployeesUseCase(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<Employee> execute() {
        if (repository.count() != 0) {
            throw new IllegalStateException("A execução do desafio requer um banco vazio.");
        }
        repository.saveAll(originalEmployees());
        return repository.findAllByOrderByInsertionOrderAsc();
    }

    // Novos objetos a cada chamada: a referência do enunciado nunca é alterada pelo aumento.
    public List<Employee> originalEmployees() {
        return List.of(
            employee("Maria", "2000-10-18", "2009.44", "Operador", 1),
            employee("João", "1990-05-12", "2284.38", "Operador", 2),
            employee("Caio", "1961-05-02", "9836.14", "Coordenador", 3),
            employee("Miguel", "1988-10-14", "19119.88", "Diretor", 4),
            employee("Alice", "1995-01-05", "2234.68", "Recepcionista", 5),
            employee("Heitor", "1999-11-19", "1582.72", "Operador", 6),
            employee("Arthur", "1993-03-31", "4071.84", "Contador", 7),
            employee("Laura", "1994-07-08", "3017.45", "Gerente", 8),
            employee("Heloísa", "2003-05-24", "1606.85", "Eletricista", 9),
            employee("Helena", "1996-09-02", "2799.93", "Gerente", 10)
        );
    }

    private Employee employee(String name, String birth, String salary, String function, int order) {
        return new Employee(name, LocalDate.parse(birth), new BigDecimal(salary), function, order);
    }
}
