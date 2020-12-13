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

import com.algafood.algafood.api.assembler.CidadeDtoAssembler;
import com.algafood.algafood.api.assembler.CidadeInputDisassembler;
import com.algafood.algafood.api.model.CidadeDto;
import com.algafood.algafood.api.model.input.CidadeInput;
import com.algafood.algafood.domain.model.Cidade;
import com.algafood.algafood.domain.repository.CidadeRepository;
import com.algafood.algafood.domain.service.CadastroCidadeService;

@RestController
@RequestMapping(value = "/cidades")
public class CidadeController {

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private CadastroCidadeService cadastroCidade;

	@Autowired
	private CidadeInputDisassembler cidadeInputDisassembler; 
	
	@Autowired
	private CidadeDtoAssembler cidadeDtoAssembler;
	
	@GetMapping
	public List<CidadeDto> listar() {
		return cidadeDtoAssembler.toCollectionModel(
					cidadeRepository.findAll());
	}

	@GetMapping("/{cidadeId}")
	public CidadeDto buscar(@PathVariable Long cidadeId) {
		return cidadeDtoAssembler.toModel(
					cadastroCidade.buscarOuFalhar(cidadeId));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeDto adicionar(@RequestBody @Valid CidadeInput cidadeInput) {
		
		Cidade cidade = cidadeInputDisassembler.toDomainObject(cidadeInput);
		
		return cidadeDtoAssembler.toModel(
					cadastroCidade.salvar(cidade));
	}

	@PutMapping("/{cidadeId}")
	public CidadeDto atualizar(@PathVariable Long cidadeId, @RequestBody @Valid CidadeInput cidadeInput) {

			Cidade cidade = cadastroCidade.buscarOuFalhar(cidadeId);
			cidadeInputDisassembler.copyToDomainObject(cidadeInput, cidade);

			return cidadeDtoAssembler.toModel(
						cadastroCidade.salvar(cidade));
	}

	@DeleteMapping("/{cidadeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cidadeId) {
		cadastroCidade.remover(cidadeId);
	}
}
 