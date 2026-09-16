package br.com.prothera.teste_Iniflex;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping ("/javadev")
@RestController
public class PrimeiroController {

    @GetMapping ("/helloworld")
    public String helloWorld(){
        return "Ola Mundo";
    }

}
