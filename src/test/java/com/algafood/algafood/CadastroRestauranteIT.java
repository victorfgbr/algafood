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
public class CadastroRestauranteIT {

    private static final int RESTAURANTE_ID_INEXISTENTE = 100;

    @LocalServerPort
    private int port;
    
    @Autowired
    private DatabaseCleaner databaseCleaner;
    
    @Autowired
    private CozinhaRepository cozinhaRepository;
    
    @Autowired
    private RestauranteRepository restauranteRepository;
    
    private Restaurante restauranteDiAndrezza;
    private Restaurante restauranteMocoto;
    private Cozinha cozinhaItaliana;
    private long qtdRestaurantes;
    
	@Before
	public void setUp () {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/restaurantes";
        


		System.out.println("Cleaning tables...");
		databaseCleaner.clearTables();
		
		System.out.println("Making new data...");
		prepararMassaDeDados();
	}
	
	private void prepararMassaDeDados () {
        cozinhaItaliana = new Cozinha();
        cozinhaItaliana.setNome("Italiana");
        cozinhaRepository.save(cozinhaItaliana);

        Cozinha cozinhaNordetisna = new Cozinha();
        cozinhaNordetisna.setNome("Nordetisna");
        cozinhaRepository.save(cozinhaNordetisna);
        
        restauranteDiAndrezza = new Restaurante();
        restauranteDiAndrezza.setNome("Di Andrezza");
        restauranteDiAndrezza.setTaxaFrete(new BigDecimal(7.50));
        restauranteDiAndrezza.setCozinha(cozinhaItaliana);
        restauranteRepository.save(restauranteDiAndrezza);
        
        restauranteMocoto = new Restaurante();
        restauranteMocoto.setNome("Mocotó");
        restauranteMocoto.setTaxaFrete(new BigDecimal(12.99));
        restauranteMocoto.setCozinha(cozinhaNordetisna);
        restauranteRepository.save(restauranteMocoto);
        
        qtdRestaurantes = restauranteRepository.count();
	}

	///////////////////////////////////////////////////////////

    @Test
	public void listarRestaurantes () {
        given()
	        .accept(ContentType.JSON)
	    .when()
	        .get()
	    .then()
	    	.body("", Matchers.hasSize((int) qtdRestaurantes))
	    	.body("nome", hasItems("Di Andrezza", "Mocotó"))
	        .statusCode(HttpStatus.OK.value());
    }

    @Test
	public void detalharRestauranteSucesso () {
        given()
	        .accept(ContentType.JSON)
			.pathParam("id", restauranteDiAndrezza.getId())
	    .when()
	        .get("/{id}")
	    .then()
	    	.body("id", equalTo(restauranteDiAndrezza.getId().intValue()))
	    	.body("nome", equalTo(restauranteDiAndrezza.getNome()))
	    	.body("taxaFrete", equalTo(restauranteDiAndrezza.getTaxaFrete().floatValue()))
	    	.body("cozinha.id", equalTo(restauranteDiAndrezza.getCozinha().getId().intValue()))
	    	.body("cozinha.nome", equalTo(restauranteDiAndrezza.getCozinha().getNome()))
	    	.body("ativo", equalTo(restauranteDiAndrezza.getAtivo()))
	    	.statusCode(HttpStatus.OK.value());
    }

    @Test
	public void detalharRestauranteInexistente () {
        given()
	        .accept(ContentType.JSON)
			.pathParam("id", RESTAURANTE_ID_INEXISTENTE)
	    .when()
	        .get("/{id}")
	    .then()
	    	.statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
	public void cadastrarRestauranteSucesso () {
		String jsonRestaurante = ResourceUtils.getContentFromResource(
				"/json/correto/restaurante-cantina.json");
		
		Long idCantina = qtdRestaurantes + 1;

		given()
			.body(jsonRestaurante)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
	    	.body("id", equalTo(idCantina.intValue()))
	    	.body("nome", equalTo("Cantina"))
	    	.body("taxaFrete", equalTo(10.99))
	    	.body("cozinha.id", equalTo(cozinhaItaliana.getId().intValue()))
	    	.body("cozinha.nome", equalTo(cozinhaItaliana.getNome()))
	    	.body("ativo", equalTo(true))
			.statusCode(HttpStatus.CREATED.value());
    }

    @Test
	public void cadastrarRestauranteTaxaFreteNegativa () {
		String jsonRestaurante = ResourceUtils.getContentFromResource(
				"/json/correto/restaurante-frete-negativo.json");

		given()
			.body(jsonRestaurante)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.body ("type", equalTo("https://algafood.com.br/dados-invalidos"))
			.statusCode(HttpStatus.BAD_REQUEST.value());
    }
        
    @Test
	public void cadastrarRestauranteComCozinhaInexistente () {
		String jsonRestaurante = ResourceUtils.getContentFromResource(
				"/json/correto/restaurante-cozinha-inexistente.json");

		given()
			.body(jsonRestaurante)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.body ("type", equalTo("https://algafood.com.br/erro-negocio"))
			.statusCode(HttpStatus.BAD_REQUEST.value());
    }
	    
	@Test
	public void alterarRestauranteCozinhaInexistente () {
		String jsonRestaurante = ResourceUtils.getContentFromResource(
				"/json/correto/restaurante-cozinha-inexistente.json");

		given()
			.body(jsonRestaurante)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.pathParam("id", restauranteDiAndrezza.getId())
		.when()
			.put("/{id}")
		.then()
			.body ("type", equalTo("https://algafood.com.br/erro-negocio"))
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}
  
//	@Test
//	public void alterarRestauranteSucesso() {
//		// TODO
//	}
    
//	@Test
//	public void ativarRestauranteExistente () {
//		// TODO
//	}
	
//	@Test
//	public void ativarRestauranteInexistente () {
//		// TODO
//	}
	
//	@Test
//	public void desativarRestauranteExistente () {
//		// TODO
//	}
	
//	@Test
//	public void desativarRestauranteInexistente () {
//		// TODO
//	}
	
//	@Test
//	public void excluirRestauranteSucesso () {
//		// TODO
//	}
	
//	@Test
//	public void excluirRestauranteInexistente () {
//		// TODO
//	}
	
}
