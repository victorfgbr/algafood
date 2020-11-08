package com.algafood.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algafood.algafood.domain.exception.EntidadeEmUsoException;
import com.algafood.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algafood.algafood.domain.model.Cidade;
import com.algafood.algafood.domain.model.Estado;
import com.algafood.algafood.domain.repository.CidadeRepository;
import com.algafood.algafood.domain.repository.EstadoRepository;

@Service
public class CadastroCidadeService {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	public Cidade salvar(Cidade cidade) {
		
		Long estadoId = cidade.getEstado().getId();

		Estado estado = estadoRepository.buscar(estadoId);
		if (estado == null) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe cadastro de estado com o código %d", estadoId));
		}

		return cidadeRepository.salvar(cidade);
	}

	public void remover(Long id) {
		try {
			cidadeRepository.remover(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de cidade com o código %d", id));

		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Cidade de código %d não pode ser removida, pois está em uso", id));
		}

	}

}
