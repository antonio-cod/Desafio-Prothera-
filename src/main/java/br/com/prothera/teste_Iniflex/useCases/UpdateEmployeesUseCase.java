package br.com.prothera.teste_Iniflex.useCases;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.prothera.teste_Iniflex.entity.Employee;
import br.com.prothera.teste_Iniflex.repository.EmployeeRepository;

@Service
public class UpdateEmployeesUseCase {
    private final EmployeeRepository repository;

    public UpdateEmployeesUseCase(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void remove(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public List<Employee> increaseSalaries() {
        List<Employee> employees = repository.findAllByOrderByInsertionOrderAsc();
        employees.forEach(employee -> employee.increaseSalary(new BigDecimal("0.10")));
        return employees; // O JPA persiste as alterações ao concluir a transação.
    }
}
