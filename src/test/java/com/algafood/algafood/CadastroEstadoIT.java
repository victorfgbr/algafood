package com.algafood.algafood;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;

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

import com.algafood.algafood.domain.model.Cidade;
import com.algafood.algafood.domain.model.Estado;
import com.algafood.algafood.domain.repository.CidadeRepository;
import com.algafood.algafood.domain.repository.EstadoRepository;
import com.algafood.algafood.util.DatabaseCleaner;
import com.algafood.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroEstadoIT {
	
	private static final int ID_ESTADO_INEXISTENTE = 100;

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	Estado bahia;
    Estado sergipe;
    Integer qtdEstados;
	
	@Autowired
	private DatabaseCleaner dataBaseCleaner;
	
	@LocalServerPort
	private int port;
	
	@Before
	public void setUp () {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/estados";
		
		System.out.println("Cleaning tables...");
		dataBaseCleaner.clearTables();
		
		System.out.println("Making new data...");
		prepararMassaDeDados();
	}
	
	private void prepararMassaDeDados () {
	    bahia = new Estado();
	    bahia.setNome("Bahia");
	    estadoRepository.save(bahia);	
	    
	    sergipe = new Estado();
	    sergipe.setNome("Sergipe");
		estadoRepository.save(sergipe);
		
	    qtdEstados = (int) estadoRepository.count();
	}	

	@Test
	public void listarEstados() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("", Matchers.hasSize(qtdEstados))
			.body("nome", hasItems("Bahia", "Sergipe")); 
	}
	
	@Test
	public void detalharEstadoBahia() {
		given()
			.pathParam("id", bahia.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", equalTo(bahia.getId().intValue()))
			.body("nome", equalTo(bahia.getNome())); 
	}

	@Test
	public void detalharEstadoInexistente() {
		given()
			.pathParam("id", ID_ESTADO_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value()); 
	}

	@Test
	public void cadastrarEstadoGoias() {
		String jsonGoias = ResourceUtils.getContentFromResource(
				"/json/correto/estado-goias.json");
		
		Integer idGoias = qtdEstados + 1;
		String nomeGoias = "Goiás";
		// POST
		given()
			.body(jsonGoias)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("id", equalTo(idGoias))
			.body("nome", equalTo(nomeGoias));
		
		// GET
		given()
			.pathParam("id", idGoias)
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", equalTo(idGoias))
			.body("nome", equalTo(nomeGoias));
	}

	@Test
	public void cadastrarEstadoSemNome () {
		String jsonEstadoSemNome = ResourceUtils.getContentFromResource(
				"/json/incorreto/estado-sem-nome.json");
		given()
			.body(jsonEstadoSemNome)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void alterarEstadoBahia () {
		
		String jsonBahiaAlterado = ResourceUtils.getContentFromResource(
				"/json/correto/estado-bahia-alterado.json");
		
		// PUT
		given()
			.pathParam("id", bahia.getId())
			.body(jsonBahiaAlterado)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value());
		
		// GET
		given()
			.pathParam("id", bahia.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", equalTo(bahia.getId().intValue()))
			.body("nome", equalTo("Bhia")); 
	}
	
	@Test
	public void alterarEstadoInexistente () {
		
		String jsonBahiaAlterado = ResourceUtils.getContentFromResource(
				"/json/correto/estado-bahia-alterado.json");
		
		given()
			.pathParam("id", ID_ESTADO_INEXISTENTE)
			.body(jsonBahiaAlterado)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{id}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void excluirEstadoEmUso () {
		
		Cidade salvador = new Cidade();
		salvador.setNome("Salvador");
		salvador.setEstado(bahia);
		cidadeRepository.save(salvador);
		
		given()
			.pathParam("id", bahia.getId())
		.when()
			.delete("/{id}")
		.then()
			.statusCode(HttpStatus.CONFLICT.value());	
	}
	
	@Test
	public void excluirEstadoBahia () {
		given()
			.pathParam("id", bahia.getId())
		.when()
			.delete("/{id}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());	
	}
	
	@Test
	public void excluirEstadoInexistente () {
		given()
			.pathParam("id", ID_ESTADO_INEXISTENTE)
		.when()
			.delete("/{id}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());	
	}
}