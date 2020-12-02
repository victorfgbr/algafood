package com.algafood.algafood.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algafood.algafood.domain.model.FormaPagamento;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {
	
}
