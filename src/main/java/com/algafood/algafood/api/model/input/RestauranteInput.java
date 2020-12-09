package com.algafood.algafood.api.model.input;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;

@Data
public class RestauranteInput {

	@NotBlank
	private String nome;
	
	@PositiveOrZero
	@NotNull
	private BigDecimal taxaFrete;
	
	@Valid
	@NotNull
	private CozinhaIdInput cozinha;
}
