package org.caixa.Rest;

import jakarta.ws.rs.Path;

import org.caixa.DTO.RequestSimulacaoDTO;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoRest {
  
  @GET
  @Path("/simular")
  public Response simular(RequestSimulacaoDTO dados) {
    // Logica para calculo do SAC e do PRICE

    // Definir DTO pra saida

    return Response.ok(output).build();
  }

  @GET
  @Path("/simulacoes")
  public Response simulacoesAnteriores(FiltroDTO dados) {
    // Acessar o Banco local para consultar todas as simulações feitas

    // Regra de negócio -> salvar o valor Total como sendo o menor valor calculado
    // Possibilidade, incluir no processo de simulação opção de especificar a simulação e limitar o valor salvo 

    // Definir DTO pra saida

    return Response.ok(output).build();
  }
}
