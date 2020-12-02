package com.algafood.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import com.algafood.algafood.domain.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long>, RestauranteRepositoryCustomized,
		JpaSpecificationExecutor<Restaurante> {

	List<Restaurante> findByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal);

	List<Restaurante> consultaPorNome(String nome, @Param("id") Long cozinhaId);

	List<Restaurante> meuFind(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal);
}
