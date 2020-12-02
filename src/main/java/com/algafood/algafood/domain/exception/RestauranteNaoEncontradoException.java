package com.algafood.algafood.domain.exception;

public class RestauranteNaoEncontradoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	private static final String MSG_RESTAURANTE_NAO_ENCONTRADO = 
			"Não existe um cadastro de restaurante com o código %d";

	
	public RestauranteNaoEncontradoException (String mensagem) {
		super(mensagem);
	}

	public RestauranteNaoEncontradoException (Long id) {
		this(String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, id));
	}

}
