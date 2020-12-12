package com.algafood.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algafood.algafood.domain.exception.RestauranteNaoEncontradoException;
import com.algafood.algafood.domain.model.Cozinha;
import com.algafood.algafood.domain.model.Restaurante;
import com.algafood.algafood.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;
	
	@Transactional
	public Restaurante salvar(Restaurante restaurante) {

		Long cozinhaId = restaurante.getCozinha().getId();

		Cozinha cozinha = cadastroCozinhaService.buscarOuFalhar(cozinhaId);

		restaurante.setCozinha(cozinha);

		return restauranteRepository.save(restaurante);
	}

	@Transactional
	public void remover(Long id) {
		try {
			restauranteRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new RestauranteNaoEncontradoException(id);

		}
	}
	
	public Restaurante buscarOuFalhar(Long id) {
		return restauranteRepository.findById(id)
				.orElseThrow(() -> new RestauranteNaoEncontradoException(id));
	}
	
	@Transactional
	public void ativar (Long restauranteId) {
		Restaurante restaurante = buscarOuFalhar(restauranteId);
		restaurante.ativar();
	}
	
	@Transactional
	public void desativar (Long restauranteId) {
		Restaurante restaurante = buscarOuFalhar(restauranteId);
		restaurante.desativar();
	}
}
