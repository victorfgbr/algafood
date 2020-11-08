package com.algafood.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algafood.algafood.domain.exception.EntidadeEmUsoException;
import com.algafood.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algafood.algafood.domain.model.Cozinha;
import com.algafood.algafood.domain.repository.CozinhaRepository;

@Service
public class CadastroCozinhaService {

	@Autowired
	private CozinhaRepository cozinhaRepository;

	public Cozinha salvar(Cozinha cozinha) {
		return cozinhaRepository.salvar(cozinha);
	}

	public void remover(Long id) {
		try { 
			cozinhaRepository.remover(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de cozinha com o código %d", id));

		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Cozinha de código %d não pode ser removida, pois está em uso", id));
		}

	}

}
