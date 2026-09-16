package br.com.prothera.teste_Iniflex.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.prothera.teste_Iniflex.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findAllByOrderByInsertionOrderAsc();
}
