package org.caixa.Exception;

import org.caixa.DTO.ErrorMensagemDTO;

public class ErroPrevistoException extends RuntimeException {
    public ErrorMensagemDTO mensagem;
    public Integer status;

    // Construtor padrão
    public ErroPrevistoException() {
        super("Ocorreu um erro na requisição."); 
    }

    // Construtor com mensagem personalizada
    public ErroPrevistoException(String mensagem) {
        this.mensagem = ErrorMensagemDTO.builder().mensagem(mensagem).build();
        this.status = 400; // Definir status padrão
    }

    public ErroPrevistoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public ErroPrevistoException(String mensagem, int status) {
        this.mensagem = ErrorMensagemDTO.builder().mensagem(mensagem).build();
        this.status = status;
    }
}
