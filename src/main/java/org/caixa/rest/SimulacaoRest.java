package org.caixa.rest;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.*;

import org.caixa.DTO.*;
import org.caixa.Exception.ErroPrevistoException;
import org.caixa.Consulta.ProdutoModel;
import org.caixa.Historico.SimulacaoModel;
import org.caixa.Mensageria.MensagemEventHub;
import org.caixa.service.SimulacaoService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoRest {

  @Inject
  SimulacaoService simulacaoService;

  @Inject
  MensagemEventHub eventHub;
  
  @POST
  @Path("/simular")
  @Counted(name = "qtdRequisicoesSimulacao", description = "Total de requisicoes de simulacao")
  @Timed(name = "tsSimular", description = "Tempo de execução da simulação", unit = "milliseconds")
  @Operation(summary = "Simular Financiamento", description = "Simula o financiamento do valor e prazo passado pela SAC e PRICE")
  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "Simulação realizada com sucesso."
        ),
        @APIResponse(
            responseCode = "500",
            description = "Erro interno no servidor."
        )
      }
  )
  public Response simular(RequestSimulacaoDTO dados) {
    try {
      // Recuperar do banco o valor de taxa e informações de produto
      // ProdutoModel produto = simulacaoService.obterDadosProduto(dados);
      // Para testes sem o banco:
      ProdutoModel produto = ProdutoModel.builder().juros(new BigDecimal(0.0179)).id(1).descricao("Produto 1").build();

      // Logica para calculo do SAC e do PRICE
      TransferDTO sac = simulacaoService.calcularSAC(dados, produto.juros);
      TransferDTO price = simulacaoService.calcularPRICE(dados, produto.juros);

      // Regra de negócio -> salvar o valor Total como sendo o menor valor calculado
      BigDecimal valorTotal = sac.getValorTotal().min(price.getValorTotal());

      SimulacaoModel simulacao = SimulacaoModel.builder()
              .dataSimulacao(new Date())
              .produto(produto.id)
              .valorDesejado(dados.getValorDesejado())
              .valorTotalParcelas(valorTotal)
              .prazo(dados.getPrazo())
              .build();

      Long idSimulacao = simulacaoService.salvarSimulacao(simulacao);

      // Enviar mensagem para o Event Hub
      //eventHub.publicarMensagem(simulacao);

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
    }catch (ErroPrevistoException e){
      return Response.status(e.status).entity(e.mensagem).build();
    }catch (Exception e){
      // Os logs seriam mais adequados usando log.error
      System.out.println("Erro ao realizar simulação.");
      System.out.println(e.getMessage());
      System.out.println(e.getCause() != null ? e.getCause().getMessage() : null);
      // Aprimorar mensagem de erro e coleta de status
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
    }
  }

  @POST
  @Path("/simulacoes")
  @Counted(name = "qtdRequisicoesSimulacoes", description = "Total de requisicoes para visualizar simulacoes anteriores")
  @Timed(name = "tsSimulacoes", description = "Tempo de execução da lista de simulações", unit = "milliseconds")
  @Operation(summary = "Obter Simulações Paginadas", description = "Obtem um retorno paginado das simulacoes realizadas anteriormente.\nO valor de paginação deve ser maior que 0 e a pagina deve ser maior ou igual a 1.\nO valor total fornecido é o menor valor da simulação entre SAC e PRICE.")
  @APIResponses(
          value = {
                  @APIResponse(
                          responseCode = "200",
                          description = "Busca realizada com sucesso."
                  ),
                  @APIResponse(
                          responseCode = "500",
                          description = "Erro interno no servidor."
                  )
          }
  )
  public Response simulacoesAnteriores(FiltroDTO dados) {
    try {
      SimulacoesDTO simulacoes = simulacaoService.obterSimulacoes(dados);
      return Response.ok(simulacoes).build();
    }catch(IllegalArgumentException e){
      // Os logs seriam mais adequados usando log.error
      System.out.print("Valores invalidos passados na requisição. ");
      System.out.println(e.getMessage());
      // Aprimorar mensagem de erro e coleta de status usando .entity e um objeto com excessão personalizada
      return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
    }catch (Exception e){
      // Os logs seriam mais adequados usando log.error
      System.out.println("Erro ao obter simulações.");
      System.out.println(e.getMessage());
      System.out.println(e.getCause() != null ? e.getCause().getMessage() : null);
      // Aprimorar mensagem de erro e coleta de status
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
    }

  }

  @GET
  @Path("/simulacoes-por-dia")
  @Counted(name = "qtdRequisicoesSimulacaoPorDia", description = "Total de requisicoes para visualizar simulacoes de um certo dia")
  @Timed(name = "tsSimulacoesPorDia", description = "Tempo de execução do resumo de simulacoes de um dia", unit = "milliseconds")
  @Operation(summary = "Obter volume de simulacoes por dia", description = "Obtem um retorno com o total de valores imulados na data fornecida dividida por produto encontrado.\nA data deve ser passada no formato DD/MM/YYYY.")
  @APIResponses(
          value = {
                  @APIResponse(
                          responseCode = "200",
                          description = "Busca realizada com sucesso."
                  ),
                  @APIResponse(
                          responseCode = "500",
                          description = "Erro interno no servidor."
                  )
          }
  )
  public Response simulacoesPorProdutoPorDia(@QueryParam("data") String data) {
    try {
      SimulacoesPorDataDTO simulacoes = simulacaoService.obterSimulacoesPorDia(data);

      return Response.ok(simulacoes).build();
    }catch (ErroPrevistoException e){
      return Response.status(e.status).entity(e.mensagem).build();
    }catch (NoResultException e){
      // Os logs seriam mais adequados usando log.error
      System.out.print("Não foram encontrados resultados para a busca. ");
      System.out.println(e.getMessage());

      // Aprimorar mensagem de erro e coleta de status usando .entity e um objeto com excessão personalizada
      return Response.status(Response.Status.NOT_FOUND.getStatusCode()).entity(e.getMessage()).build();

    }
    catch(IllegalArgumentException e){
      // Os logs seriam mais adequados usando log.error
      System.out.print("Valores invalidos passados na requisição. ");
      System.out.println(e.getMessage());

      // Aprimorar mensagem de erro e coleta de status usando .entity e um objeto com excessão personalizada
      return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();
    }catch (Exception e){
      // Os logs seriam mais adequados usando log.error
      System.out.println("Erro ao obter simulações.");
      System.out.println(e.getMessage());
      System.out.println(e.getCause() != null ? e.getCause().getMessage() : null);

      // Aprimorar mensagem de erro e coleta de status
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
    }

  }

  @GET
  @Path("/telemetria")
  @Counted(name = "qtdRequisicoesTelemetriaPorDia", description = "Total de requisicoes para visualizar os dados de telemetria de um certo dia")
  @Timed(name = "tsTelemetriaPorDia", description = "Tempo de execução da obtenção da telemetria das simulações de um dia", unit = "milliseconds")
  @Operation(summary = "Obter dados de telemetria por dia", description = "")
  @APIResponses(
          value = {
                  @APIResponse(
                          responseCode = "200",
                          description = "Busca realizada com sucesso."
                  ),
                  @APIResponse(
                          responseCode = "500",
                          description = "Erro interno no servidor."
                  )
          }
  )
  public Response telemetriaPorDia(@QueryParam("data") String data) {
    try {
      ResponseTelemetriaDTO simulacoes = simulacaoService.obterDadosTelemetria(data);

      return Response.ok(simulacoes).build();
    }catch (ErroPrevistoException e){
      return Response.status(e.status).entity(e.mensagem).build();
    }catch (Exception e){
      // Os logs seriam mais adequados usando log.error
      System.out.println("Erro ao obter simulações.");
      System.out.println(e.getMessage());
      System.out.println(e.getCause() != null ? e.getCause().getMessage() : null);

      // Aprimorar mensagem de erro e coleta de status
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
    }

  }


}
