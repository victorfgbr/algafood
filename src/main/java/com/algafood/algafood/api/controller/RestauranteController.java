package com.algafood.algafood.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algafood.algafood.domain.model.Restaurante;
import com.algafood.algafood.domain.repository.RestauranteRepository;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@GetMapping
	public List<Restaurante> listar() {
		return restauranteRepository.listar();
	}

	@GetMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> buscar(@PathVariable Long restauranteId) {
		Restaurante cozinha = restauranteRepository.buscar(restauranteId);

		if (cozinha != null) {
			return ResponseEntity.ok(cozinha);
		}

		return ResponseEntity.notFound().build();
	}

}
