package com.algafood.algafood.api.assembler;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algafood.algafood.api.model.FormaPagamentoDto;
import com.algafood.algafood.domain.model.FormaPagamento;

@Component
public class FormaPagamentoDtoAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public FormaPagamentoDto toModel(FormaPagamento formaPagamento) {
		return modelMapper.map(formaPagamento, FormaPagamentoDto.class);
	}
	
	public List<FormaPagamentoDto> toCollectionModel(List<FormaPagamento> formasPagamento) {
		return formasPagamento.stream()
				.map(forma -> toModel(forma))
				.collect(Collectors.toList());
	}
	
}