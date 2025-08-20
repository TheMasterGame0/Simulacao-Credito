package org.caixa.DTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
  private Long idSimulacao;
  private Integer codigoProduto;
  private String descricaoProduto;
  private BigDecimal taxaJuros;
  private List<SimulacaoDTO> resultadoSimulacao;
  
}
