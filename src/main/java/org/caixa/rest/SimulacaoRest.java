package org.caixa.rest;

import jakarta.ws.rs.Path;

import org.caixa.DTO.RequestSimulacaoDTO;
import org.caixa.DTO.ResponseDTO;
import org.caixa.DTO.SimulacaoDTO;
import org.caixa.model.Produto;
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
import org.eclipse.microprofile.metrics.annotation.Counted;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoRest {

  @Inject
  SimulacaoService simulacaoService;
  
  @GET
  @Path("/simular")
  @Counted(name = "qtdRequisicoesSimulacao", description = "Total de requisicoes de simulacao")
  public Response simular(RequestSimulacaoDTO dados) {
    try {
      // Recuperar do banco o valor de taxa e informações de produto
      Produto produto = simulacaoService.obterDadosProduto(dados);

      // Logica para calculo do SAC e do PRICE
      SimulacaoDTO sac = simulacaoService.calcularSAC(dados, produto.juros);
      SimulacaoDTO price = simulacaoService.calcularPRICE(dados, produto.juros);

      // DTO de saida
      ResponseDTO response = ResponseDTO.builder()
              .idSimulacao(1L)   // Precisa passar a ser recuperada do banco ao salvar a simulacao
              .codigoProduto(produto.id)
              .descricaoProduto(produto.descricao)
              .taxaJuros(produto.juros)
              .resultadoSimulacao(List.of(sac, price))
              .build();

      // Regra de negócio -> salvar o valor Total como sendo o menor valor calculado
      // Possibilidade, incluir no processo de simulação opção de especificar a simulação e limitar o valor salvo
      // Definir a Model pra saida que será enviado ao banco.

      return Response.ok(response).build();
    }catch(IllegalArgumentException e){
      System.out.println("Valores invalidos passados na requisição");
      System.out.println(e.getMessage());
      // Aprimorar mensagem de erro e coleta de status usando .entity e um objeto com excessão personalizada
      return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
    }catch (Exception e){
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

    // Definir DTO pra saida

    return Response.ok().build();
  }
}
