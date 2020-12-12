package com.algafood.algafood.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algafood.algafood.api.assembler.RestauranteInputDisassembler;
import com.algafood.algafood.api.assembler.RestauranteModelAssembler;
import com.algafood.algafood.api.model.RestauranteDTO;
import com.algafood.algafood.api.model.input.CozinhaIdInput;
import com.algafood.algafood.api.model.input.RestauranteInput;
import com.algafood.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.algafood.algafood.domain.exception.NegocioException;
import com.algafood.algafood.domain.model.Restaurante;
import com.algafood.algafood.domain.repository.RestauranteRepository;
import com.algafood.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	private RestauranteModelAssembler restauranteModelAssembler;
	
	@Autowired
	private RestauranteInputDisassembler restauranteInputDisassembler;
	
	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroRestauranteService cadastroRestaurante;

	@GetMapping
	public List<RestauranteDTO> listar() {
		return restauranteModelAssembler.toCollectionModel(restauranteRepository.findAll());

	}

	@GetMapping("/{restauranteId}")
	public RestauranteDTO buscar(@PathVariable Long restauranteId) {
		Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(restauranteId);
		
		return restauranteModelAssembler.toModel(restaurante);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteDTO adicionar(@RequestBody @Valid RestauranteInput restauranteInput) {
		try {
			Restaurante restaurante = restauranteInputDisassembler.toDomainObject(restauranteInput);
			return restauranteModelAssembler.toModel(cadastroRestaurante.salvar(restaurante));
		}
		catch (CozinhaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@PutMapping("/{restauranteId}")
	public RestauranteDTO atualizar (@PathVariable Long restauranteId,
			@RequestBody @Valid RestauranteInput restauranteInput) {
		
		try {
			Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
			
			restauranteInputDisassembler.copyToDomainObject(restauranteInput, restauranteAtual);
			
			return restauranteModelAssembler.toModel(cadastroRestaurante.salvar(restauranteAtual));
		}
		catch (CozinhaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@DeleteMapping("/{restauranteId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long restauranteId) {
		cadastroRestaurante.remover(restauranteId);
	}
	
	@PatchMapping("/{restauranteId}")
	public RestauranteDTO atualizarParcial (@PathVariable Long restauranteId,
			@RequestBody Map<String, Object> campos,
			HttpServletRequest request) {
		
		Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
		
		merge (campos, restauranteAtual, request);
		
		RestauranteInput restauranteAtualDto = new RestauranteInput();
		restauranteAtualDto.setNome(restauranteAtual.getNome());
		restauranteAtualDto.setTaxaFrete(restauranteAtual.getTaxaFrete());
		
		CozinhaIdInput cozinhaInputDto = new CozinhaIdInput();
		cozinhaInputDto.setId(restauranteAtual.getId());
		restauranteAtualDto.setCozinha(cozinhaInputDto);
		
		return atualizar(restauranteId, restauranteAtualDto);
	}
	
	@PutMapping("/{restauranteId}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativar (@PathVariable Long restauranteId) {
		cadastroRestaurante.ativar(restauranteId);
	}
	
	@DeleteMapping("/{restauranteId}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void desativar (@PathVariable Long restauranteId) {
		cadastroRestaurante.desativar(restauranteId);
	}
	
	private void merge (Map<String, Object> camposOrigem, Restaurante restauranteDestino, 
			HttpServletRequest request) {
		
		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
			
			Restaurante restauranteOrigem = objectMapper.convertValue(camposOrigem, Restaurante.class);
			
			camposOrigem.forEach((nomeCampo, valorCampo) -> {
				Field field = ReflectionUtils.findField(Restaurante.class, nomeCampo);
				field.setAccessible(true);
				
				Object valorOrigem = ReflectionUtils.getField(field, restauranteOrigem);
				ReflectionUtils.setField(field, restauranteDestino, valorOrigem);
			});			
		} catch (IllegalArgumentException e) {
			throw new HttpMessageNotReadableException(e.getMessage(), e, serverHttpRequest);
		}
	}
}
