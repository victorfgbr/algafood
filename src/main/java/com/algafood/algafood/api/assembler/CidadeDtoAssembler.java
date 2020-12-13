package com.algafood.algafood.api.assembler;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algafood.algafood.api.model.CidadeDto;
import com.algafood.algafood.domain.model.Cidade;

@Component
public class CidadeDtoAssembler {

	@Autowired
	private ModelMapper modelMapper;
	
	public CidadeDto toModel(Cidade cidade) {
		return modelMapper.map(cidade, CidadeDto.class);
	}
	
	public List<CidadeDto> toCollectionModel(List<Cidade> cidades) {
		return cidades.stream()
				.map(cidade -> toModel(cidade))
				.collect(Collectors.toList());
	}
	
}