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
    private Integer tempoMedio;
    private Integer tempoMinimo;
    private Integer tempoMaximo;
    private Float percentualSucesso;
}
