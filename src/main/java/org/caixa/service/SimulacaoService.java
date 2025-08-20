package org.caixa.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.caixa.DAO.ConsultaDAO;
import org.caixa.DAO.SalvarDAO;
import org.caixa.DTO.ParcelaDTO;
import org.caixa.DTO.RequestSimulacaoDTO;
import org.caixa.DTO.TransferDTO;
import org.caixa.model.Produto;
import org.caixa.model.Simulacao;

@ApplicationScoped
public class SimulacaoService {

  @Inject
  ConsultaDAO consutaDao;

  @Inject
  SalvarDAO salvarDao;

  public Produto obterDadosProduto(RequestSimulacaoDTO dados){
    // Validar os dados da requisicao
    if(dados == null || dados.getPrazo() == null || dados.getValorDesejado() == null){
      throw new IllegalArgumentException("Dados de simulação inválidos: prazo e valor desejado são obrigatórios.");
    }
    return consutaDao.getProduto(dados.getPrazo(), dados.getValorDesejado());
  }

  public Long salvarSimulacao(Simulacao simulacao){
    // Salvar a simulação no banco de dados
    return salvarDao.save(simulacao);
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
