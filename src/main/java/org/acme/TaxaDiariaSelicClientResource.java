package org.acme;

import org.acme.dto.TaxaDiariaDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1")
public class TaxaDiariaSelicClientResource {

	private ObjectMapper objectMapper = new ObjectMapper();

	@ConfigProperty(name = "url-taxa-selic")
	private String baseUrl;

	@POST
	@Path("/taxa-diaria-selic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String hello(String requestBody, @QueryParam(value = "parametrosOrdenacao") String parametrosOrdenacao,
			@QueryParam(value = "page") String page, @QueryParam(value = "pageSize") String pageSize)
			throws JsonMappingException, JsonProcessingException {

		// Crie uma instância do cliente JAX-RS
		Client client = ClientBuilder.newClient();
		// Crie um alvo Web
		WebTarget target = client.target(baseUrl);

		// Converte o requestBody que é String em um objeto DTO
		TaxaDiariaDTO taxaDiariaDTO = objectMapper.readValue(requestBody, TaxaDiariaDTO.class);

		// Faça a chamada à API externa usando o método GET
		Invocation.Builder builder = target.queryParam("parametrosOrdenacao", parametrosOrdenacao)
				.queryParam("page", page).queryParam("pageSize", pageSize).request(MediaType.APPLICATION_JSON);
		Response resposta = builder.post(Entity.entity(taxaDiariaDTO, MediaType.APPLICATION_JSON));
		String jsonResposta = resposta.readEntity(String.class);
		// Fechar o cliente quando não for mais necessário
		client.close();
		// Processar a resposta
		Log.info("Resposta da API externa: " + jsonResposta);

		return jsonResposta;
	}

}
