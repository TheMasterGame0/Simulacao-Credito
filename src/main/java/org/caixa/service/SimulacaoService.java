package org.caixa.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.caixa.Consulta.ConsultaDAO;
import org.caixa.DTO.*;
import org.caixa.Exception.ErroPrevistoException;
import org.caixa.Historico.HistoricoDAO;
import org.caixa.Historico.MetricsModel;
import org.caixa.Consulta.ProdutoModel;
import org.caixa.Historico.SimulacaoModel;
import org.caixa.Metrics.MetricsDAO;
import org.caixa.Util.DataUtil;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import javax.swing.text.html.Option;

@ApplicationScoped
public class SimulacaoService {

  @Inject
  ConsultaDAO consultaDao;

  @Inject
  HistoricoDAO historicoDao;

  @Inject
  MetricsDAO metricsDao;

  @Inject
  @RegistryType(type = MetricRegistry.Type.APPLICATION)
  MetricRegistry registry;


  private static final List<List<String>> endpoints = new ArrayList<>(Arrays.asList(
      Arrays.asList("Simular",
                    "org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacao",
                    "/api/simular_status_200",
                    "org.caixa.rest.SimulacaoRest.tsSimular"), 
      Arrays.asList("Simulacoes",
                    "org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacoes",
                    "/api/simulacoes_status_200",
                    "org.caixa.rest.SimulacaoRest.tsSimulacoes"),
      Arrays.asList("SimulacoesPorDia",
                    "org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacaoPorDia",
                    "/api/simulacoes_por_dia_status_200",
                    "org.caixa.rest.SimulacaoRest.tsSimulacoesPorDia"),
      Arrays.asList("TelemetriaPorDia",
                    "org.caixa.rest.SimulacaoRest.qtdRequisicoesTelemetriaPorDia",
                    "/api/telemetria_status_200",
                    "org.caixa.rest.SimulacaoRest.tsTelemetriaPorDia")
    ));

  public ProdutoModel obterDadosProduto(RequestSimulacaoDTO dados){
    // Validar os dados da requisicao
    if(dados == null || dados.getPrazo() == null || dados.getValorDesejado() == null){
      throw new ErroPrevistoException("Dados de simulação inválidos: prazo e valor desejado são obrigatórios.", 400);
    }
    return consultaDao.getProduto(dados.getPrazo(), dados.getValorDesejado());
  }

  public Long salvarSimulacao(SimulacaoModel simulacao){
    // Salvar a simulação no banco de dados
    return historicoDao.save(simulacao);
  }

  public SimulacoesDTO obterSimulacoes(FiltroDTO filtro){
    // Validar dados
    // Pagina >= 1
    // qtd registrosPagina > 0

    Long totalRegistros = historicoDao.getTotalResgitstros();
    List<RegistroDTO> registros = historicoDao.getSimulacoes(filtro);
    return SimulacoesDTO.builder()
            .pagina(filtro.getPagina())
            .qtdRegistros(totalRegistros)
            .qtdRegistrosPagina(filtro.getQtdRegistrosPagina())
            .registros(registros).build();
  }

  public SimulacoesPorDataDTO obterSimulacoesPorDia(String dataRecebida){
    // Validar dados
    Date data = DataUtil.getDataFormatada(dataRecebida);

    List<ProdutoModel> produtos =  consultaDao.getProdutos();

    if (produtos == null || produtos.isEmpty()) {
      throw new ErroPrevistoException("Nenhum produto encontrado.", 204);
    }

    List<VolumeDTO> volume = new ArrayList<>();
    for(ProdutoModel produto : produtos) {
      List<RegistroDTO> registros = historicoDao.getSimulacoesPorDiaPorProduto(data, produto.id);
      if(!registros.isEmpty()){
        BigDecimal valorTotal = registros.stream().map(RegistroDTO::getValorTotalParcelas).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalParcelas = new BigDecimal(registros.stream().mapToInt(RegistroDTO::getPrazo).sum());

        volume.add(VolumeDTO.builder()
                .codigoProduto(produto.id)
                .descricaoProduto(produto.descricao)
                .taxaMediaJuro(produto.juros)               // Todos tem  mesma taxa mensal
                .valorMedioPrestacao(valorTotal.divide(totalParcelas, 2, RoundingMode.HALF_UP))
                .valorTotalDesejado(registros.stream().map(RegistroDTO::getValorDesejado).reduce(BigDecimal.ZERO, BigDecimal::add))
                .valorTotalCredito(valorTotal)
                .build());
      }
    }

    return SimulacoesPorDataDTO.builder()
            .dataReferencia(DataUtil.getDataFormatada(data))
            .simulacoes(volume)
            .build();
  }

  public ResponseTelemetriaDTO obterDadosTelemetria(String data){
    List<TelemetriaDTO> telemetria = new ArrayList<>();
    List<MetricsModel> metricas;
    
    for(List<String> endpoint : endpoints){
      metricas = metricsDao.findByEndpointByDate(endpoint, DataUtil.getDataFormatada(data));

      Optional<Long> tsMax = metricas.stream().filter(m -> m.nome.equals(endpoint.get(3))).map(MetricsModel::getTsMax).findFirst();
      Optional<Long> tsMin = metricas.stream().filter(m -> m.nome.equals(endpoint.get(3))).map(MetricsModel::getTsMin).findFirst();
      Optional<Double> tsMedio = metricas.stream().filter(m -> m.nome.equals(endpoint.get(3))).map(MetricsModel::getTsMedio).findFirst();

      Counter counter = registry.getCounter(new MetricID(endpoint.get(2)));
      Long qtdRequisicoes = registry.getCounter(new MetricID(endpoint.get(1))).getCount();
      
      if(qtdRequisicoes > 0){
        TelemetriaDTO dto = TelemetriaDTO.builder()
        .nomeApi(endpoint.get(0))
        .qtdRequisicoes(qtdRequisicoes)
        .tempoMaximo(tsMax.orElse(null))
        .tempoMinimo(tsMin.orElse(null))
        .tempoMedio(tsMedio.orElse(null))
        .build();
        if(counter!=null) {
          dto.setPercentualSucesso((float) (counter.getCount()) / qtdRequisicoes);
        }

        telemetria.add(dto);

      }
    }
    
    return ResponseTelemetriaDTO.builder().data(data).endpoints(telemetria).build();
  }

  public TransferDTO calcularSAC(RequestSimulacaoDTO dados, BigDecimal taxa) {
    // Realizar validação dos valores enviados no dados

    BigDecimal saldo = dados.getValorDesejado();
    // Amortizacao constante
    BigDecimal amortizacao = saldo.divide(BigDecimal.valueOf(dados.getPrazo()), 2, RoundingMode.HALF_UP);

    List<ParcelaDTO> parcelas = new ArrayList<>();
    BigDecimal totalPago = new BigDecimal(0);

    for (int i = 1; i <= dados.getPrazo(); i++) {
      BigDecimal juros = saldo.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
      BigDecimal valorParcela = amortizacao.add(juros).setScale(2, RoundingMode.HALF_UP);
      saldo = saldo.subtract(amortizacao).setScale(2, RoundingMode.HALF_UP);

      parcelas.add(ParcelaDTO.builder()
              .numero(i)
              .valorAmortizacao(amortizacao)
              .valorJuros(juros)
              .valorPrestacao(valorParcela)
              .build());

      totalPago = totalPago.add(valorParcela);
    }

    // Salvar totalPago e Juros

    return TransferDTO.builder()
          .sistema("SAC")
          .valorTotal(totalPago)
          .parcelas(parcelas)
          .build();
  }

  public TransferDTO calcularPRICE(RequestSimulacaoDTO dados, BigDecimal taxa) {
    // Realizar validação dos valores enviados no dados

    BigDecimal saldo = dados.getValorDesejado();
    Integer prazo = dados.getPrazo();

    // Fórmula da parcela fixa (Price)
    BigDecimal jurosCompostos = taxa.add(BigDecimal.valueOf(1.0)).pow(prazo);
    BigDecimal parcelaFixa = saldo.multiply(taxa).multiply(jurosCompostos).divide(jurosCompostos.subtract(BigDecimal.valueOf(1)), 2, RoundingMode.HALF_UP);

    List<ParcelaDTO> parcelas = new ArrayList<>();
    BigDecimal totalPago = new BigDecimal(0);

    for (int i = 1; i <= prazo; i++) {
      BigDecimal juros = saldo.multiply(taxa).setScale(2, RoundingMode.HALF_UP) ;
      BigDecimal amortizacao = parcelaFixa.subtract(juros).setScale(2, RoundingMode.HALF_UP);
      saldo = saldo.subtract(amortizacao);

      parcelas.add(ParcelaDTO.builder()
              .numero(i)
              .valorAmortizacao(amortizacao)
              .valorJuros(juros)
              .valorPrestacao(parcelaFixa)
              .build());

      totalPago = totalPago.add(parcelaFixa);
    }

    // Salvar totalPago e Juros

    return TransferDTO.builder()
          .sistema("PRICE")
          .valorTotal(totalPago)
          .parcelas(parcelas)
          .build();
  }
}
