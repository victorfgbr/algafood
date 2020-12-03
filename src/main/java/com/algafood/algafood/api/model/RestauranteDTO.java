package com.algafood.algafood.api.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RestauranteDTO {

	private Long id;
	
	private String nome;
	
	private BigDecimal taxaFrete;
	
	private CozinhaDTO cozinha;
	
}
