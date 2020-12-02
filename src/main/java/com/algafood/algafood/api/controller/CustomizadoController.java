package com.algafood.algafood.api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.algafood.algafood.domain.model.Cozinha;
import com.algafood.algafood.domain.model.Restaurante;
import com.algafood.algafood.domain.repository.CozinhaRepository;
import com.algafood.algafood.domain.repository.RestauranteRepository;

@RestController
@RequestMapping(value = "/teste")
public class CustomizadoController {

	@Autowired
	CozinhaRepository cozinhaRepository;

	@Autowired
	private RestauranteRepository restauranteRepository;

	@GetMapping("/cozinha/por-nome")
	public List<Cozinha> listarPorNome(@RequestParam String nome) {
		return cozinhaRepository.findByNomeContaining(nome);
	}

	@GetMapping("/restaurante/por-taxa")
	public List<Restaurante> listarPorTaxa(BigDecimal taxaInicial, BigDecimal taxaFinal) {
		return restauranteRepository.findByTaxaFreteBetween(taxaInicial, taxaFinal);
	}

	@GetMapping("/restaurante/filtro")
	public List<Restaurante> filtrarRestaurantes(String nome, Long cozinha) {
		return restauranteRepository.consultaPorNome(nome, cozinha);
	}

	@GetMapping("/restaurante/filtro-2")
	public List<Restaurante> filtrarRestaurantes(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal) {
		return restauranteRepository.meuFind(nome, taxaFreteInicial, taxaFreteFinal);
	}

	@GetMapping("/restaurante/frete-gratis")
	public List<Restaurante> restaurantesComFreteGratis(String nome) {

		return restauranteRepository.findComFreteGratis(nome);
	}

}
