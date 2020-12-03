package com.algafood.algafood.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
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

import com.algafood.algafood.api.model.CozinhaDTO;
import com.algafood.algafood.api.model.RestauranteDTO;
import com.algafood.algafood.api.model.input.CozinhaIdInputDTO;
import com.algafood.algafood.api.model.input.RestauranteInputDTO;
import com.algafood.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.algafood.algafood.domain.exception.NegocioException;
import com.algafood.algafood.domain.model.Cozinha;
import com.algafood.algafood.domain.model.Restaurante;
import com.algafood.algafood.domain.repository.RestauranteRepository;
import com.algafood.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroRestauranteService cadastroRestaurante;

	@GetMapping
	public List<RestauranteDTO> listar() {
		List<Restaurante> restaurantes = restauranteRepository.findAll();
		
		return toListDto(restaurantes); 
	}

	@GetMapping("/{restauranteId}")
	public RestauranteDTO buscar(@PathVariable Long restauranteId) {
		Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(restauranteId);
		return toDto(restaurante); 
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteDTO adicionar(@RequestBody @Valid RestauranteInputDTO restauranteInputDto) {
		try {
			Restaurante restaurante = toModel(restauranteInputDto);
			return toDto(cadastroRestaurante.salvar(restaurante));
		}
		catch (CozinhaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@PutMapping("/{restauranteId}")
	public RestauranteDTO atualizar (@PathVariable Long restauranteId,
			@RequestBody @Valid RestauranteInputDTO restauranteInputDto) {
		
		try {
			Restaurante restaurante = toModel(restauranteInputDto);
			Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
			
			BeanUtils.copyProperties(restaurante, restauranteAtual, "id", "formasPagamento", "endereco", "dataCadastro");
			
			return toDto(cadastroRestaurante.salvar(restaurante));
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
		
		RestauranteInputDTO restauranteAtualDto = new RestauranteInputDTO();
		restauranteAtualDto.setNome(restauranteAtual.getNome());
		restauranteAtualDto.setTaxaFrete(restauranteAtual.getTaxaFrete());
		
		CozinhaIdInputDTO cozinhaInputDto = new CozinhaIdInputDTO();
		cozinhaInputDto.setId(restauranteAtual.getId());
		restauranteAtualDto.setCozinha(cozinhaInputDto);
		
		return atualizar(restauranteId, restauranteAtualDto);
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
	
	private List<RestauranteDTO> toListDto (List<Restaurante> restaurantes) {
		return restaurantes.stream().map((restaurante) -> toDto(restaurante)).collect(Collectors.toList());
	}
	
	private RestauranteDTO toDto (Restaurante restaurante) {
		
		RestauranteDTO restauranteDto = new RestauranteDTO();
		CozinhaDTO cozinhaDto = new  CozinhaDTO();
		
		cozinhaDto.setId(restaurante.getCozinha().getId());
		cozinhaDto.setNome(restaurante.getCozinha().getNome());
		
		restauranteDto.setId(restaurante.getId());
		restauranteDto.setNome(restaurante.getNome());
		restauranteDto.setTaxaFrete(restaurante.getTaxaFrete());
		
		restauranteDto.setCozinha(cozinhaDto);
		
		return restauranteDto;
	}
	
	private Restaurante toModel (RestauranteInputDTO restauranteInput) {
		Restaurante restaurante = new Restaurante();
		restaurante.setNome(restauranteInput.getNome());
		restaurante.setTaxaFrete(restauranteInput.getTaxaFrete());
		
		Cozinha cozinha = new Cozinha();
		cozinha.setId(restauranteInput.getCozinha().getId());
		restaurante.setCozinha(cozinha);
		
		return restaurante;
	}
	
}
