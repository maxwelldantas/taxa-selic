package org.acme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.TaxaDiariaDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/v1")
public class TaxaDiariaSelicClientResource {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ConfigProperty(name = "url-taxa-selic")
    private String baseUrl;

    private static String consultaTaxaSelic(Invocation.Builder builder, TaxaDiariaDTO taxaDiariaDTO) {
        try (Response resposta = builder.post(Entity.entity(taxaDiariaDTO, MediaType.APPLICATION_JSON))) {
            String jsonResposta = resposta.readEntity(String.class);
            // Processar a resposta
            Log.info("Resposta da API externa: " + jsonResposta);
            return jsonResposta;
        } catch (ResponseProcessingException e) {
            Log.info(e.getMessage());
        }
        return null;
    }

    @POST
    @Path("/taxa-diaria-selic")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String taxaDiariaSelic(String requestBody, @QueryParam(value = "parametrosOrdenacao") String parametrosOrdenacao, @QueryParam(value = "page") String page, @QueryParam(value = "pageSize") String pageSize) {

        // Crie uma instância do cliente JAX-RS
        try (Client client = ClientBuilder.newClient()) {
            // Crie um alvo Web
            WebTarget target = client.target(baseUrl);

            // Converte o requestBody de String para um objeto DTO
            TaxaDiariaDTO taxaDiariaDTO = objectMapper.readValue(requestBody, TaxaDiariaDTO.class);

            // Faça a chamada à API externa usando o método GET
            Invocation.Builder builder = target.queryParam("parametrosOrdenacao", parametrosOrdenacao).queryParam("page", page).queryParam("pageSize", pageSize).request(MediaType.APPLICATION_JSON);
            return consultaTaxaSelic(builder, taxaDiariaDTO);
        } catch (ProcessingException | JsonProcessingException e) {
            Log.info(e.getMessage());
        }

        return null;
    }
}
