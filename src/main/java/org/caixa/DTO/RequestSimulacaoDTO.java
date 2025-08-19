package org.caixa.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // required for JSON deserialization
@AllArgsConstructor
public class RequestSimulacaoDTO {
  
  private BigDecimal valorDesejado;
  private Integer prazo;
  
}
