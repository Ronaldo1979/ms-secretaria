package br.com.cognitivebrasil.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cognitivebrasil.enumerator.Pasta;
import br.com.cognitivebrasil.model.Secretaria;

public interface SecretariaRepository extends JpaRepository<Secretaria, Long>{

	Secretaria findByPasta(Pasta pasta);
}
