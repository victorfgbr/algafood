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
	private static final int COZINHA_EM_USO = 1;
	private static final int COZINHA_SEM_USO = 2;

	private Cozinha cozinhaAmericana;
	private int quantidadeCozinhasCadastradas;
	private String jsonCorretoCozinhaChinesa;
	
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
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarCozinhas() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value());
	}

	@Test
	public void deveConterCozinhas_QuandoConsultarCozinhas() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.body("", Matchers.hasSize(quantidadeCozinhasCadastradas))
			.body("nome", hasItems("Tailandesa", "Americana")); 
	}
	
	@Test
	public void deveRetornarStatus201_quantoCadastrarCozinha () {
		
		jsonCorretoCozinhaChinesa = ResourceUtils.getContentFromResource(
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
	public void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		given()
			.pathParam("cozinhaId", cozinhaAmericana.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo(cozinhaAmericana.getNome()));
	}
	
	@Test
	public void deveRetornarNotFound_QuandoConsultarCozinhaInexistente() {
		given()
			.pathParam("cozinhaId", COZINHA_ID_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	public void deveRetornarStatus201_QuandoExcluirCozinhaNaoUtilizada() {
		given()
			.pathParam("cozinhaId", COZINHA_SEM_USO)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());	
	}
	
	public void deveRetornarStatus409_QuandoExcluirCozinhaEmUso() {
		given()
			.pathParam("cozinhaId", COZINHA_EM_USO)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.CONFLICT.value());
	}
	
	public void deveRetornarStatus404_QuandoExcluirCozinhaInexistente() {
		given()
			.pathParam("cozinhaId", COZINHA_ID_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	private void prepararMassaDeDados () {
		
	    Cozinha cozinhaTailandesa = new Cozinha();
	    cozinhaTailandesa.setNome("Tailandesa");
	    cozinhaRepository.save(cozinhaTailandesa);

	    cozinhaAmericana = new Cozinha();
	    cozinhaAmericana.setNome("Americana");
	    cozinhaRepository.save(cozinhaAmericana);
	    
	    quantidadeCozinhasCadastradas = (int) cozinhaRepository.count();
		
        Restaurante restaurante = new Restaurante();
        restaurante.setNome("Thai Thai Delivery");
        restaurante.setTaxaFrete(new BigDecimal(10));
        restaurante.setCozinha(cozinhaTailandesa);
        restauranteRepository.save(restaurante);
	    
	}
	
}