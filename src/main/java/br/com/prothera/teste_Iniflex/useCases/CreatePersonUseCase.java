package br.com.prothera.teste_Iniflex.useCases;

import org.springframework.stereotype.Service;

import br.com.prothera.teste_Iniflex.entity.Person;

@Service
public class CreatePersonUseCase {
    // PRINCIPIOS SOLID
    // SINGLE RESPONSABILITY PRINCIPLE

    public void execute(Person person) {
        System.out.println(person);
    }

}
