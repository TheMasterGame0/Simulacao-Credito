package org.caixa.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetriaDTO {
    private String nomeApi;
    private Long qtdRequisicoes;
    private Double tempoMedio;
    private Long tempoMinimo;
    private Long tempoMaximo;
    private Float percentualSucesso;
}
