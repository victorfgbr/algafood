package com.algafood.algafood.domain.exception;

public class FormaPagamentoEmUsoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	private static final String MSG_FORMA_PAGAMENTO_EM_USO = 
			"A forma de pagamento com o código %d está em uso.";
	
	public FormaPagamentoEmUsoException (String mensagem) {
		super(mensagem);
	}

	public FormaPagamentoEmUsoException (Long id) {
		this(String.format(MSG_FORMA_PAGAMENTO_EM_USO, id));
	}

}
