package org.caixa.Mensageria;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import org.caixa.Exception.ErrosPrevistoException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MensagemEventHub {

    private final EventHubProducerClient cliente;

    @ConfigProperty(name = "eventhub.connection-string")
    String dadosConexao;

    public MensagemEventHub(){
      this.cliente  = new EventHubClientBuilder().connectionString(dadosConexao).buildProducerClient();
    }

    public void publicarMensagem(Object mensagem){
        String msg = converterEmJson(mensagem);
        EventData evento = new EventData(msg);
        cliente.send(List.of(evento));
    }

    private String converterEmJson(Object objeto){
        try{
            return new ObjectMapper().writeValueAsString(objeto);
        }
        catch(JsonProcessingException ex){
            throw new ErrosPrevistoException("Falha ao converter o Objeto em JSON", 500);
        }
    }
}
