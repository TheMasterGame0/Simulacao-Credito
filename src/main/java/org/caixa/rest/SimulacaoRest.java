package org.caixa.rest;

import jakarta.ws.rs.Path;

import org.caixa.DTO.RequestSimulacaoDTO;
import org.caixa.DTO.ResponseDTO;
import org.caixa.DTO.SimulacaoDTO;
import org.caixa.service.SimulacaoService;

import java.math.BigDecimal;
import java.util.List;

import org.caixa.DTO.FiltroDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoRest {

  @Inject
  SimulacaoService simulacaoService;
  
  @GET
  @Path("/simular")
  public Response simular(RequestSimulacaoDTO dados) {
    // Recuperar do banco o valor de taxa e informações de produto
    BigDecimal taxa = new BigDecimal(0.0179);
    Integer codigoProduto = 1;
    String descricaoProduto = "Produto 1";

    // Logica para calculo do SAC e do PRICE
    SimulacaoDTO sac = simulacaoService.calcularSAC(dados, taxa);
    SimulacaoDTO price = simulacaoService.calcularPRICE(dados, taxa);
    
    // DTO de saida
    ResponseDTO response = ResponseDTO.builder()
      .idSimulacao(1L)   // Precisa passar a ser recuperada do banco ao salvar a simulacao
      .codigoProduto(codigoProduto)
      .descricaoProduto(descricaoProduto)
      .taxaJuros(taxa)
      .resultadoSimulacao(List.of(sac, price))
      .build();

    // Regra de negócio -> salvar o valor Total como sendo o menor valor calculado
    // Possibilidade, incluir no processo de simulação opção de especificar a simulação e limitar o valor salvo 
    // Definir a Model pra saida que será enviado ao banco.

    return Response.ok(response).build();
  }

  @GET
  @Path("/simulacoes")
  public Response simulacoesAnteriores(FiltroDTO dados) {
    // Acessar o Banco local para consultar todas as simulações feitas

    // Definir DTO pra saida

    return Response.ok().build();
  }
}
