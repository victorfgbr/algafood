package com.algafood.algafood.domain.exception;

public class CozinhaNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	private static final String MSG_COZINHA_NAO_ENCONTRADA = 
			"Não existe um cadastro de cozinha com o código %d";
	
	public CozinhaNaoEncontradaException (String mensagem) {
		super(mensagem);
	}

	public CozinhaNaoEncontradaException (Long id) {
		this(String.format(MSG_COZINHA_NAO_ENCONTRADA, id));
	}

}
