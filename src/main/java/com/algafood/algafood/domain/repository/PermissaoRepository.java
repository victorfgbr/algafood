package com.algafood.algafood.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algafood.algafood.domain.model.Permissao;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
	
}
