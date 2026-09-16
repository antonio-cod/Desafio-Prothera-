package br.com.prothera.teste_Iniflex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.prothera.teste_Iniflex.custom_messages.ErrorMensage;
import br.com.prothera.teste_Iniflex.entity.Person;
import br.com.prothera.teste_Iniflex.useCases.CreatePersonUseCase;

@RequestMapping ("/person")
@RestController
public class PersonController {

    @Autowired
     CreatePersonUseCase createPersonUseCase;

    @PostMapping ("create")
    public ResponseEntity<?> create(@RequestBody Person person) {

        try {
            var result = createPersonUseCase.execute(person);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            var errorMessage = new ErrorMensage(e.getMessage(), "INVALID_PARAMS");
            return ResponseEntity.status(400).body(errorMessage);
        }


    }

}
