package br.com.prothera.teste_Iniflex;

import java.time.LocalDate;
import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import br.com.prothera.teste_Iniflex.repository.EmployeeRepository;
import br.com.prothera.teste_Iniflex.presentation.EmployeePrinter;
import br.com.prothera.teste_Iniflex.useCases.ExecuteChallengeUseCase;

@Component
public class Principal implements CommandLineRunner {
    private final ExecuteChallengeUseCase executeChallenge;
    private final EmployeeRepository repository;
    private final EmployeePrinter printer;
    private final DataSource dataSource;

    public Principal(ExecuteChallengeUseCase executeChallenge, EmployeeRepository repository,
                     EmployeePrinter printer, DataSource dataSource) {
        this.executeChallenge = executeChallenge;
        this.repository = repository;
        this.printer = printer;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement();
             var result = statement.executeQuery("SELECT DATABASE_PATH()")) {
            result.next();
            printer.database(connection.getMetaData().getURL(), result.getString(1));
        }
        // O proxy transacional conclui o commit antes da consulta e da apresentação.
        var execution = executeChallenge.execute();
        var persisted = repository.findAllByOrderByInsertionOrderAsc();
        printer.challenge(execution, persisted, LocalDate.now());
    }
}
