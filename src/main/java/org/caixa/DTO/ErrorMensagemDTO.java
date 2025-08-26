package org.caixa.DTO;

import java.util.Date;

import org.caixa.Util.DataUtil;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ErrorMensagemDTO {

  public String data;
  public String mensagem;

  public ErrorMensagemDTO(String mensagem) {
    this.mensagem = mensagem;
    this.data = DataUtil.getDataFormatada(new Date()); 
  }

  public ErrorMensagemDTO(String data, String mensagem) {
    this.mensagem = mensagem;
    this.data = DataUtil.getDataFormatada(new Date()); 
  }
  
}
