package com.algafood.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algafood.algafood.domain.exception.FormaPagamentoEmUsoException;
import com.algafood.algafood.domain.exception.FormaPagamentoNaoEncontradaException;
import com.algafood.algafood.domain.model.FormaPagamento;
import com.algafood.algafood.domain.repository.FormaPagamentoRepository;

@Service
public class CadastroFormaPagamentoService {

	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@Transactional
	public FormaPagamento salvar(FormaPagamento formaPagamento) {
		return formaPagamentoRepository.save(formaPagamento);
	}
	
	@Transactional
	public void remover(Long id) {
		try {
			formaPagamentoRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new FormaPagamentoNaoEncontradaException(id);

		} catch (DataIntegrityViolationException e) {
			throw new FormaPagamentoEmUsoException(id);
		}
	}
	
	public FormaPagamento buscarOuFalhar(Long id) {
		return formaPagamentoRepository.findById(id)
				.orElseThrow(() -> new FormaPagamentoNaoEncontradaException(id));
	}
}
