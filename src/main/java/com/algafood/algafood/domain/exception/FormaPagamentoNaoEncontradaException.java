package com.algafood.algafood.domain.exception;

public class FormaPagamentoNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	private static final String MSG_FORMA_PAGAMENTO_NAO_ENCONTRADA = 
			"A forma de pagamento código %d não está cadastrada no sistema.";
	
	public FormaPagamentoNaoEncontradaException (String mensagem) {
		super(mensagem);
	}

	public FormaPagamentoNaoEncontradaException (Long id) {
		this(String.format(MSG_FORMA_PAGAMENTO_NAO_ENCONTRADA, id));
	}

}
