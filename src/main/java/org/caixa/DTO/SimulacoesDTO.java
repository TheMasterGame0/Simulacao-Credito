package org.caixa.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacoesDTO {
    private Integer pagina;
    private Long qtdRegistros;
    private Integer qtdRegistrosPagina;
    private List<RegistroDTO> registros;
}
