package org.caixa.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import org.caixa.DTO.ParcelaDTO;
import org.caixa.DTO.RequestSimulacaoDTO;
import org.caixa.DTO.SimulacaoDTO;

@ApplicationScoped
public class SimulacaoService {
  
  public SimulacaoDTO calcularSAC(RequestSimulacaoDTO dados, BigDecimal taxa) {
    // Realizar validação dos valores enviados no dados

    BigDecimal saldo = dados.getValorDesejado();
    // Amortizacao constante
    BigDecimal amortizacao = saldo.divide(BigDecimal.valueOf(dados.getPrazo()), 2, RoundingMode.HALF_UP);

    List<ParcelaDTO> parcelas = new ArrayList<>();
    BigDecimal totalPago = new BigDecimal(0), totalJuros = new BigDecimal(0);

    for (int i = 1; i <= dados.getPrazo(); i++) {
      BigDecimal juros = saldo.multiply(taxa);
      BigDecimal valorParcela = amortizacao.add(juros);
      saldo = saldo.subtract(amortizacao);

      parcelas.add(ParcelaDTO.builder()
              .numero(i)
              .valorAmortizacao(amortizacao)
              .valorJuros(juros)
              .valorPrestacao(valorParcela)
              .build());

      totalPago = totalPago.add(valorParcela);
      totalJuros = totalJuros.add(juros);
    }

    // Salvar totalPago e Juros

    return SimulacaoDTO.builder()
          .sistema("SAC")
          .parcelas(parcelas)
          .build();
  }

  public SimulacaoDTO calcularPRICE(RequestSimulacaoDTO dados, BigDecimal taxa) {
    // Realizar validação dos valores enviados no dados

    BigDecimal saldo = dados.getValorDesejado();
    //double r = dados.getTaxa();
    Integer prazo = dados.getPrazo();

    // Fórmula da parcela fixa (Price)
    BigDecimal jurosCompostos = taxa.add(BigDecimal.valueOf(1.0)).pow(prazo);
    BigDecimal parcelaFixa = saldo.multiply(taxa).multiply(jurosCompostos).divide(jurosCompostos.subtract(BigDecimal.valueOf(1)), 2, RoundingMode.HALF_UP);

    List<ParcelaDTO> parcelas = new ArrayList<>();
    BigDecimal totalPago = new BigDecimal(0), totalJuros = new BigDecimal(0);

    for (int i = 1; i <= prazo; i++) {
      BigDecimal juros = saldo.multiply(taxa) ;
      BigDecimal amortizacao = parcelaFixa.subtract(juros).setScale(2, RoundingMode.HALF_UP);
      saldo = saldo.subtract(amortizacao);

      parcelas.add(ParcelaDTO.builder()
              .numero(i)
              .valorAmortizacao(amortizacao)
              .valorJuros(juros)
              .valorPrestacao(parcelaFixa)
              .build());

      totalPago = totalPago.add(parcelaFixa);
      totalJuros = totalJuros.add(juros);
    }

    // Salvar totalPago e Juros

    return SimulacaoDTO.builder()
          .sistema("PRICE")
          .parcelas(parcelas)
          .build();
  }
}
