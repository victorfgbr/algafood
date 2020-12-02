package com.algafood.algafood.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algafood.algafood.domain.model.Cozinha;

public interface CozinhaRepository extends JpaRepository<Cozinha, Long>{
	
	List<Cozinha> findByNomeContaining(String nome);
}
