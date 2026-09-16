package br.com.prothera.teste_Iniflex.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee extends Person {
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal salary;

    @Column(name = "job_function", nullable = false)
    private String jobFunction;

    @Column(name = "insertion_order", nullable = false, unique = true)
    private int insertionOrder;

    protected Employee() {}

    public Employee(String name, LocalDate dateOfBirth, BigDecimal salary,
                    String jobFunction, int insertionOrder) {
        super(name, dateOfBirth);
        this.salary = salary;
        this.jobFunction = jobFunction;
        this.insertionOrder = insertionOrder;
    }

    public BigDecimal getSalary() { return salary; }
    public String getJobFunction() { return jobFunction; }
    public int getInsertionOrder() { return insertionOrder; }

    public void increaseSalary(BigDecimal rate) {
        salary = salary.multiply(BigDecimal.ONE.add(rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
