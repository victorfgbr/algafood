package com.algafood.algafood.api.assembler;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algafood.algafood.api.model.CozinhaDto;
import com.algafood.algafood.domain.model.Cozinha;

@Component
public class CozinhaDtoAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public CozinhaDto toModel(Cozinha cozinha) {
		return modelMapper.map(cozinha, CozinhaDto.class);
	}
	
	public List<CozinhaDto> toCollectionModel(List<Cozinha> cozinhas) {
		return cozinhas.stream()
				.map(cozinha -> toModel(cozinha))
				.collect(Collectors.toList());
	}
	
}