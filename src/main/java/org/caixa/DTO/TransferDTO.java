package org.caixa.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    private String sistema;
    private BigDecimal valorTotal;
    private List<ParcelaDTO> parcelas;
}
