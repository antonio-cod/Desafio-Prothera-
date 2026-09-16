package br.com.prothera.teste_Iniflex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.prothera.teste_Iniflex.entity.Person;
import br.com.prothera.teste_Iniflex.useCases.CreatePersonUseCase;

@RequestMapping ("/person")
@RestController
public class PersonController {

    @Autowired
     CreatePersonUseCase createPersonUseCase;

    @PostMapping ("create")
    public void create(@RequestBody Person person) {

        createPersonUseCase.execute(person);

    }

}
