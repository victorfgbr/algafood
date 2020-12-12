package com.algafood.algafood;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.algafood.algafood.domain.model.Cozinha;
import com.algafood.algafood.domain.model.Restaurante;
import com.algafood.algafood.domain.repository.CozinhaRepository;
import com.algafood.algafood.domain.repository.RestauranteRepository;
import com.algafood.algafood.util.DatabaseCleaner;
import com.algafood.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroCozinhaIT {
	private static final int COZINHA_ID_INEXISTENTE = 100;

	private Cozinha americana;
	private Cozinha tailandesa;
	private int qtdCozinhas;	
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
    
    @Autowired
    private RestauranteRepository restauranteRepository;
    
	@Autowired
	private DatabaseCleaner dataBaseCleaner;
	
	@LocalServerPort
	private int port;
	
	@Before
	public void setUp () {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";

		System.out.println("Cleaning tables...");
		dataBaseCleaner.clearTables();
		
		System.out.println("Making new data...");
		prepararMassaDeDados();
	}
	
	private void prepararMassaDeDados () {
		
	    tailandesa = new Cozinha();
	    tailandesa.setNome("Tailandesa");
	    cozinhaRepository.save(tailandesa);

	    americana = new Cozinha();
	    americana.setNome("Americana");
	    cozinhaRepository.save(americana);
	    
	    qtdCozinhas = (int) cozinhaRepository.count();
	}
	
	@Test
	public void listarCozinhas() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.body("", Matchers.hasSize(qtdCozinhas))
			.body("nome", hasItems("Tailandesa", "Americana"))
			.statusCode(HttpStatus.OK.value());
	}
	
	@Test
	public void detalharCozinhaAmericana () {
		given()
			.pathParam("id", americana.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", equalTo(americana.getId().intValue()))
			.body("nome", equalTo(americana.getNome()));
	}
	
	@Test
	public void detalharCozinhaInexistente () {
		given()
			.pathParam("id", COZINHA_ID_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void cadastrarCozinhaChinesa () {
		String jsonCorretoCozinhaChinesa = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-chinesa.json");

		given()
			.body(jsonCorretoCozinhaChinesa)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value());
	}
	
	@Test
	public void cadastrarCozinhaSemNome () {
		String jsonCozinhaSemNome = ResourceUtils.getContentFromResource(
				"/json/incorreto/cozinha-sem-nome.json");
		given()
			.body(jsonCozinhaSemNome)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	public void alterarCozinhaAmericana() {

		String jsonAmericanaAlterado = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-americana-alterada.json");
		
		// PUT
		given()
			.pathParam("id", americana.getId())
			.body(jsonAmericanaAlterado)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value());
		
		// GET
		given()
			.pathParam("id", americana.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", equalTo(americana.getId().intValue()))
			.body("nome", equalTo(americana.getNome() + "_")); 
	}
	
	@Test
	public void alterarCozinhaInexistente() {
		// TODO
	}
	
	@Test
	public void excluirCozinhaSucesso() {
		given()
			.pathParam("cozinhaId", americana.getId())
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());	
	}
	
	@Test
	public void excluirCozinhaInexistente() {
		given()
			.pathParam("cozinhaId", COZINHA_ID_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());	
	}
	
	@Test
	public void excluirCozinhaEmUso() {
		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Pizza Hut");
		restaurante.setCozinha(americana);
		restaurante.setTaxaFrete(new BigDecimal(8.99));
		restauranteRepository.save(restaurante);
		
		given()
			.pathParam("cozinhaId", americana.getId())
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.CONFLICT.value());
	}
		
}