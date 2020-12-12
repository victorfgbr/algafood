package com.algafood.algafood.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algafood.algafood.api.model.input.EstadoInput;
import com.algafood.algafood.domain.model.Estado;

@Component
public class EstadoInputDisassembler {

	@Autowired
	ModelMapper modelMapper;
	
	public Estado toDomainModel (EstadoInput estadoInput) {
		return modelMapper.map(estadoInput, Estado.class);
	}
	
	public void copy (EstadoInput estadoInput, Estado estado) {
		modelMapper.map(estadoInput, estado);
	}
	
}
