package org.caixa.Exception;

public class ErrosPrevistoException extends RuntimeException {
    public ErrorMensagem mensagem;
    public Integer status;

    // Construtor padrão
    public ErrosPrevistoException() {
        super("Ocorreu um erro na requisição."); 
    }

    // Construtor com mensagem personalizada
    public ErrosPrevistoException(String mensagem) {
        super(mensagem);
    }

    public ErrosPrevistoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public ErrosPrevistoException(ErrorMensagem mensagem,int status) {
        this.mensagem = mensagem;
        this.status = status;
    }
}
