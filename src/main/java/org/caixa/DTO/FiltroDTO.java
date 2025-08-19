package org.caixa.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // required for JSON deserialization
@AllArgsConstructor
public class FiltroDTO {
  private Integer pagina;
  private Integer qtdRegistros;
  private Integer qtdRegistrosPagina;

}
