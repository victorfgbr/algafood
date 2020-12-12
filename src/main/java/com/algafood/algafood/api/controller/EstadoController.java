package com.algafood.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algafood.algafood.api.assembler.EstadoDtoAssembler;
import com.algafood.algafood.api.assembler.EstadoInputDisassembler;
import com.algafood.algafood.api.model.EstadoDto;
import com.algafood.algafood.api.model.input.EstadoInput;
import com.algafood.algafood.domain.model.Estado;
import com.algafood.algafood.domain.repository.EstadoRepository;
import com.algafood.algafood.domain.service.CadastroEstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {

	@Autowired
	private EstadoRepository estadoRepository;

	@Autowired
	private CadastroEstadoService cadastroEstado;
	
	@Autowired
	private EstadoDtoAssembler estadoDtoAssembler;
	
	@Autowired
	private EstadoInputDisassembler estadoInputDisassembler;
	
	
	
	
	
	@GetMapping
	public List<EstadoDto> listar() { 
		return estadoDtoAssembler.toCollectionModel(
				estadoRepository.findAll());
	}

	@GetMapping("/{estadoId}")
	public EstadoDto buscar(@PathVariable Long estadoId) {
		return estadoDtoAssembler.toModel(
				cadastroEstado.buscarOuFalhar(estadoId)); 
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoDto adicionar(@RequestBody @Valid EstadoInput estadoInput) {
		
		Estado estado = estadoInputDisassembler.toDomainModel(estadoInput);
		
		return estadoDtoAssembler.toModel(
				cadastroEstado.salvar(estado));
	}
	
	@PutMapping("/{estadoId}")
	public EstadoDto atualizar (@PathVariable Long estadoId,
			@RequestBody @Valid EstadoInput estadoInput) {
		
		Estado estado = cadastroEstado.buscarOuFalhar(estadoId);
		
		estadoInputDisassembler.copy(estadoInput, estado);
		
		return estadoDtoAssembler.toModel(
				cadastroEstado.salvar(estado));
	}

	@DeleteMapping("/{estadoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long estadoId) {
		cadastroEstado.remover(estadoId);
	}

	
}
