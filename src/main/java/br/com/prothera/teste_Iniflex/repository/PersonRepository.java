package br.com.prothera.teste_Iniflex.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.prothera.teste_Iniflex.entity.Person;

public interface PersonRepository extends JpaRepository<Person, UUID> {

}
