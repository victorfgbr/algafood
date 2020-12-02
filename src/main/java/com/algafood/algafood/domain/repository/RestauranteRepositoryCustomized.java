package com.algafood.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import com.algafood.algafood.domain.model.Restaurante;

public interface RestauranteRepositoryCustomized {

	List<Restaurante> meuFind(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal);

	
	List<Restaurante> findComFreteGratis (String nome);
}