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

import com.algafood.algafood.api.assembler.FormaPagamentoDtoAssembler;
import com.algafood.algafood.api.assembler.FormaPagamentoInputDisassembler;
import com.algafood.algafood.api.model.FormaPagamentoDto;
import com.algafood.algafood.api.model.input.FormaPagamentoInput;
import com.algafood.algafood.domain.model.FormaPagamento;
import com.algafood.algafood.domain.repository.FormaPagamentoRepository;
import com.algafood.algafood.domain.service.CadastroFormaPagamentoService;

@RestController
@RequestMapping("/formas-pagamento")
public class FormaPagamentoController {
	
	@Autowired
	private FormaPagamentoDtoAssembler formaPagamentoDtoAssembler;
	
	@Autowired
	private FormaPagamentoInputDisassembler formaPagamentoInputDisassembler;
	
	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@Autowired
	private CadastroFormaPagamentoService cadastroFormaPagamento;
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping
	public List<FormaPagamentoDto> listar () {
		return formaPagamentoDtoAssembler.toCollectionModel(
					formaPagamentoRepository.findAll());
	}
	
	@GetMapping("/{idFormaPagamento}")
	public FormaPagamentoDto detalhar (@PathVariable Long idFormaPagamento) {
		return formaPagamentoDtoAssembler.toModel(
					cadastroFormaPagamento.buscarOuFalhar(idFormaPagamento));
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FormaPagamentoDto salvar (@Valid @RequestBody FormaPagamentoInput formaPagamentoInput) {
		FormaPagamento formaPagamento = formaPagamentoInputDisassembler.toDomainObject(formaPagamentoInput);

		return formaPagamentoDtoAssembler.toModel(
					formaPagamentoRepository.save(formaPagamento));
	}
	
	@PutMapping("/{idFormaPagamento}")
	public FormaPagamentoDto editar (@PathVariable Long idFormaPagamento,
			@Valid @RequestBody FormaPagamentoInput formaPagamentoInput) { 
		
		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarOuFalhar(idFormaPagamento);
		
		formaPagamentoInputDisassembler.copyToDomainObject(formaPagamentoInput, formaPagamento);

		return formaPagamentoDtoAssembler.toModel(
					cadastroFormaPagamento.salvar(formaPagamento));
	}
	
	@DeleteMapping("/{idFormaPagamento}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir (@PathVariable Long idFormaPagamento) { 
		cadastroFormaPagamento.remover(idFormaPagamento);
	}

}
