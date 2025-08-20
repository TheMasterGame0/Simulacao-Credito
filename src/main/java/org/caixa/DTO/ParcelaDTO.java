package org.caixa.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelaDTO {
  private Integer numero;
  private BigDecimal valorAmortizacao;
  private BigDecimal valorJuros;
  private BigDecimal valorPrestacao;
}
