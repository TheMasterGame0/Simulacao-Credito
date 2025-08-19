package org.caixa.DTO;

@Data
@Builder
@NoArgsConstructor // required for JSON deserialization
@AllArgsConstructor
public class FiltroDTO {
  private Integer pagina;
  private Integer qtdRegistros;
  private Integer qtdRegistrosPagina;

  public Integer getPagina() {
    return pagina;
  }

  public void setPagina(Integer pagina) {
    this.pagina = pagina;
  }

  public Integer getQtdRegistros() {
    return qtdRegistros;
  }

  public void setQtdRegistros(Integer qtdRegistros) {
    this.qtdRegistros = qtdRegistros;
  }

  public Integer getQtdRegistrosPagina() {
    return qtdRegistrosPagina;
  }

  public void setQtdRegistrosPagina(Integer qtdRegistrosPagina) {
    this.qtdRegistrosPagina = qtdRegistrosPagina;
  }
}
