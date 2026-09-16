package br.com.prothera.teste_Iniflex.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.prothera.teste_Iniflex.entity.Person;
import br.com.prothera.teste_Iniflex.repository.PersonRepository;

@Service
public class CreatePersonUseCase {
    // PRINCIPIOS SOLID
    // SINGLE RESPONSABILITY PRINCIPLE

    @Autowired
    private PersonRepository personRepository;

    public Person execute(Person person) {

        if (person.getName() == null || person.getDate_of_birth() == null ) {
            throw new IllegalArgumentException("Preencher todos os campos");
        }

       person =  personRepository.save(person);
       return person;
    }

}
