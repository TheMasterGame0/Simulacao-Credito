package org.caixa.Exception;

import java.util.Date;

import org.caixa.Util.DataUtil;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class  ErrorMensagem {

  public String data;
  public String mensagem;

  public ErrorMensagem(String mensagem) {
    this.mensagem = mensagem;
    this.data = DataUtil.getDataFormatada(new Date()); 
  }

  public ErrorMensagem(String data, String mensagem) {
    this.mensagem = mensagem;
    this.data = DataUtil.getDataFormatada(new Date()); 
  }
  
}
