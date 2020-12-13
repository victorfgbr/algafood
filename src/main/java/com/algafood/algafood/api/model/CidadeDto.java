package com.algafood.algafood.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CidadeDto {
	
	private Long id;
	private String nome;
	private EstadoDto estado;
	
}
