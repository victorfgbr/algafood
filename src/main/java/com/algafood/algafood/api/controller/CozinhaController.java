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

import com.algafood.algafood.api.assembler.CozinhaDtoAssembler;
import com.algafood.algafood.api.assembler.CozinhaInputDisassembler;
import com.algafood.algafood.api.model.CozinhaDto;
import com.algafood.algafood.api.model.input.CozinhaInput;
import com.algafood.algafood.domain.model.Cozinha;
import com.algafood.algafood.domain.repository.CozinhaRepository;
import com.algafood.algafood.domain.service.CadastroCozinhaService;

@RestController
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;

	@Autowired
	private CadastroCozinhaService cadastroCozinha;
	
	@Autowired
	private CozinhaDtoAssembler cozinhaDtoAssembler;
	
	@Autowired
	private CozinhaInputDisassembler cozinhaInputDisassembler;
	
	
	@GetMapping
	public List<CozinhaDto> listar() {
		return cozinhaDtoAssembler.toCollectionModel(cozinhaRepository.findAll());
	}

	@GetMapping("/{cozinhaId}")
	public CozinhaDto buscar(@PathVariable Long cozinhaId) {
		return cozinhaDtoAssembler.toModel(cadastroCozinha.buscarOuFalhar(cozinhaId));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaDto adicionar(@RequestBody @Valid CozinhaInput cozinhaInput) {
		Cozinha cozinha = cozinhaInputDisassembler.toDomainObject(cozinhaInput);
		cozinha = cadastroCozinha.salvar(cozinha);
		return cozinhaDtoAssembler.toModel(cozinha);
	}

	@PutMapping("/{cozinhaId}")
	public CozinhaDto atualizar(@PathVariable Long cozinhaId, 
			@RequestBody @Valid CozinhaInput cozinhaInput) {
		
		Cozinha cozinhaAtual = cadastroCozinha.buscarOuFalhar(cozinhaId);
		cozinhaInputDisassembler.copyToDomainObject(cozinhaInput, cozinhaAtual);
		return cozinhaDtoAssembler.toModel(cadastroCozinha.salvar(cozinhaAtual));
	}

	@DeleteMapping("/{cozinhaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cozinhaId) {
		cadastroCozinha.remover(cozinhaId);
	}

}
