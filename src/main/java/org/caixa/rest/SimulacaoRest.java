package org.caixa.rest;

import jakarta.ws.rs.Path;

import org.caixa.DTO.*;
import org.caixa.model.Produto;
import org.caixa.model.Simulacao;
import org.caixa.service.SimulacaoService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoRest {

  @Inject
  SimulacaoService simulacaoService;
  
  @POST
  @Path("/simular")
  @Counted(name = "qtdRequisicoesSimulacao", description = "Total de requisicoes de simulacao")
  @Operation(summary = "Simular Financiamento", description = "Simula o financiamento do valor e prazo passado pela SAC e PRICE")
  public Response simular(RequestSimulacaoDTO dados) {
    try {
      // Recuperar do banco o valor de taxa e informações de produto
      Produto produto = simulacaoService.obterDadosProduto(dados);

      // Logica para calculo do SAC e do PRICE
      TransferDTO sac = simulacaoService.calcularSAC(dados, produto.juros);
      TransferDTO price = simulacaoService.calcularPRICE(dados, produto.juros);

      // Regra de negócio -> salvar o valor Total como sendo o menor valor calculado
      BigDecimal valorTotal = sac.getValorTotal().max(price.getValorTotal());

      Simulacao simulacao = Simulacao.builder()
              .dataSimulacao(new Date())
              .produto(produto.id)
              .valorDesejado(dados.getValorDesejado())
              .valorTotalParcelas(valorTotal)
              .prazo(dados.getPrazo())
              .build();

      Long idSimulacao = simulacaoService.salvarSimulacao(simulacao);

      // DTO de saida
      ResponseDTO response = ResponseDTO.builder()
              .idSimulacao(idSimulacao)
              .codigoProduto(produto.id)
              .descricaoProduto(produto.descricao)
              .taxaJuros(produto.juros)
              .resultadoSimulacao(List.of(new SimulacaoDTO(sac.getSistema(), sac.getParcelas()), new SimulacaoDTO(price.getSistema(), price.getParcelas())))
              .build();

      return Response.ok(response).build();
    }catch(IllegalArgumentException e){
      // Os logs seriam mais adequados usando log.error
      System.out.print("Valores invalidos passados na requisição. ");
      System.out.println(e.getMessage());
      // Aprimorar mensagem de erro e coleta de status usando .entity e um objeto com excessão personalizada
      return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
    }catch (Exception e){
      // Os logs seriam mais adequados usando log.error
      System.out.println("Erro ao realizar simulação.");
      System.out.println(e.getMessage());
      System.out.println(e.getCause() != null ? e.getCause().getMessage() : null);
      // Aprimorar mensagem de erro e coleta de status
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
    }
  }

  @GET
  @Path("/simulacoes")
  @Counted(name = "qtdRequisicoesSimulacoes", description = "Total de requisicoes para visualizar simulacoes anteriores")
  public Response simulacoesAnteriores(FiltroDTO dados) {
    // Acessar o Banco local para consultar todas as simulações feitas
    simulacaoService.obterSimulacoes();

    // Definir DTO pra saida

    return Response.ok().build();
  }
}
