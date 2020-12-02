package com.algafood.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algafood.algafood.domain.exception.CidadeNaoEncontradaException;
import com.algafood.algafood.domain.exception.EntidadeEmUsoException;
import com.algafood.algafood.domain.model.Cidade;
import com.algafood.algafood.domain.model.Estado;
import com.algafood.algafood.domain.repository.CidadeRepository;

@Service
public class CadastroCidadeService {

	private static final String MSG_CIDADE_EM_USO = 
			"Cidade de código %d não pode ser removida, pois está em uso";

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private CadastroEstadoService cadastroEstadoService;

	@Transactional
	public Cidade salvar(Cidade cidade) {

		Long estadoId = cidade.getEstado().getId();
		
		Estado estado = cadastroEstadoService.buscarOuFalhar(estadoId);
		cidade.setEstado(estado);
		 
		return cidadeRepository.save(cidade);
	}
	
	@Transactional
	public void remover(Long id) {
		try {
			cidadeRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new CidadeNaoEncontradaException(id);

		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_CIDADE_EM_USO, id));
		}
	}
	
	public Cidade buscarOuFalhar(Long id) {
		return cidadeRepository.findById(id)
				.orElseThrow(() -> new CidadeNaoEncontradaException(id));
	}
}
