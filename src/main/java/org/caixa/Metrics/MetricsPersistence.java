package org.caixa.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.caixa.Historico.MetricsModel;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class MetricsPersistence {

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Inject
    MetricsDAO dao;

    private static final List<String> NOME_METRICAS = new ArrayList<>(
        Arrays.asList(
        "org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacao",
        "/api/simular_status_200",
        "/api/simulacoes_status_200",
        "/api/simulacoes_status_200"
        )
    );

    void onStart(@Observes StartupEvent ev) {
        List<MetricsModel> metricas = dao.findByDate(new Date());

        // Carrega as métricas(se existirem)
        if (!metricas.isEmpty()) {
            try {
                for(MetricsModel metrica : metricas) {
                    Counter counter = registry.getCounter(new MetricID(metrica.getNome()));
                    if(counter == null){
                        counter =  registry.counter(metrica.getNome());
                    }
                    counter.inc(metrica.getValor());
                }
                System.out.println("Métricas carregadas: " + metricas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(every = "1m") // ou "24h" para diário
    void coletarMetricas() {
        long simularStatus200 = registry.getCounter(new MetricID("/api/simular_status_200")).getCount();
        dao.save(MetricsModel.builder().nome("/api/simular_status_200").valor(simularStatus200).data(new Date()).build());


        long simularTotal = registry.getCounter(new MetricID("org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacao")).getCount();



        dao.save(MetricsModel.builder().nome("org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacao").valor(simularStatus200).data(new Date()).build());

        System.out.printf("Status 200: %d | Total: %d%n", simularStatus200, simularTotal);
    }
}