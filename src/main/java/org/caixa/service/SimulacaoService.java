package org.caixa.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import org.caixa.Consulta.ConsultaDAO;
import org.caixa.DTO.*;
import org.caixa.Historico.HistoricoDAO;
import org.caixa.Consulta.ProdutoModel;
import org.caixa.Historico.SimulacaoModel;

@ApplicationScoped
public class SimulacaoService {

  @Inject
  ConsultaDAO consultaDao;

  @Inject
  HistoricoDAO historicoDao;

  public ProdutoModel obterDadosProduto(RequestSimulacaoDTO dados){
    // Validar os dados da requisicao
    if(dados == null || dados.getPrazo() == null || dados.getValorDesejado() == null){
      throw new IllegalArgumentException("Dados de simulação inválidos: prazo e valor desejado são obrigatórios.");
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

  public SimulacoesPorDataDTO obterSimulacoesPorDia(Date data){
    // Validar dados
    // Pagina >= 1
    // qtd registrosPagina > 0

    List<ProdutoModel> produtos =  consultaDao.getProdutos();

    if (produtos == null || produtos.isEmpty()) {
      throw new NoResultException("Nenhum produto encontrado.");
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
            .dataReferencia(data.toString())
            .simulacoes(volume)
            .build();
  }

  public TransferDTO calcularSAC(RequestSimulacaoDTO dados, BigDecimal taxa) {
    // Realizar validação dos valores enviados no dados

    BigDecimal saldo = dados.getValorDesejado();
    // Amortizacao constante
    BigDecimal amortizacao = saldo.divide(BigDecimal.valueOf(dados.getPrazo()), 2, RoundingMode.HALF_UP);

    List<ParcelaDTO> parcelas = new ArrayList<>();
    BigDecimal totalPago = new BigDecimal(0), totalJuros = new BigDecimal(0);

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
    BigDecimal totalPago = new BigDecimal(0), totalJuros = new BigDecimal(0);

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
