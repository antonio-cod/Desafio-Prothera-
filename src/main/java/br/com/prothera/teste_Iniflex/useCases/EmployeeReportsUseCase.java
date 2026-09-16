package br.com.prothera.teste_Iniflex.useCases;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import br.com.prothera.teste_Iniflex.entity.Employee;

@Service
public class EmployeeReportsUseCase {
    private static final BigDecimal MINIMUM_WAGE = new BigDecimal("1212.00");

    public Map<String, List<Employee>> groupByFunction(List<Employee> employees) {
        return employees.stream().collect(Collectors.groupingBy(
            Employee::getJobFunction, LinkedHashMap::new, Collectors.toList()));
    }

    public List<Employee> birthdays(List<Employee> employees) {
        return employees.stream().filter(employee -> {
            int month = employee.getDateOfBirth().getMonthValue();
            return month == 10 || month == 12;
        }).toList();
    }

    public Optional<Employee> oldest(List<Employee> employees) {
        return employees.stream().min(Comparator.comparing(Employee::getDateOfBirth));
    }

    public int age(Employee employee, LocalDate referenceDate) {
        return Period.between(employee.getDateOfBirth(), referenceDate).getYears();
    }

    public List<Employee> alphabetical(List<Employee> employees) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("pt-BR"));
        return employees.stream().sorted(Comparator.comparing(Employee::getName, collator)).toList();
    }

    public BigDecimal totalSalary(List<Employee> employees) {
        return employees.stream().map(Employee::getSalary).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public BigDecimal minimumWages(Employee employee) {
        return employee.getSalary().divide(MINIMUM_WAGE, 2, RoundingMode.HALF_UP);
    }
}
