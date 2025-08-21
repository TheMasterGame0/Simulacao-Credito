package org.caixa.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {
    private Long idSimulacao;
    private BigDecimal valorDesejado;
    private Integer prazo;
    private BigDecimal valorTotalParcelas;
}
