package org.caixa.DTO;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // required for JSON deserialization
@AllArgsConstructor
public class RequestSimulacaoDTO {
  
  private BigDecimal valorDesejado;
  private Integer prazo;
  
}
