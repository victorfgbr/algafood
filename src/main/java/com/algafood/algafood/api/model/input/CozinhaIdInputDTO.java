package com.algafood.algafood.api.model.input;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CozinhaIdInputDTO {
	
	@NotNull
	private Long id;
}
