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

    private static final List<String> NOME_METRICAS_COUNTERS = new ArrayList<>(
        Arrays.asList(
        "org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacao",
        "org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacoes",
        "org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacaoPorDia",
        "org.caixa.rest.SimulacaoRest.qtdRequisicoesTelemetriaPorDia",
        "/api/simular_status_200",
        "/api/simulacoes_status_200",
        "/api/simulacoes-por-dia_status_200",
        "/api/telemetria_status_200"
        )
    );

    private static final List<String> NOME_METRICAS_TIMERS = new ArrayList<>(
            Arrays.asList(
            "org.caixa.rest.SimulacaoRest.tsSimular",
            "org.caixa.rest.SimulacaoRest.tsSimulacoes",
            "org.caixa.rest.SimulacaoRest.tsSimulacoesPorDia",
            "org.caixa.rest.SimulacaoRest.tsTelemetriaPorDia"
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
                        counter.inc(metrica.getValor());
                    }else {
                        long difference = metrica.getValor() - counter.getCount();
                        if (difference > 0) {
                            counter.inc(difference);
                        }
                    }
                }
                System.out.println("Métricas carregadas: " + metricas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(every = "1m") // Na versão definitiva deixar 30m
    void coletarMetricas() {
        System.out.println(registry.getMetrics());
        Counter counter;
        for(String id : NOME_METRICAS_COUNTERS){
            counter = registry.getCounter(new MetricID(id));
            if(counter!=null) {
                dao.save(MetricsModel.builder().nome(id).valor(counter.getCount()).data(new Date()).build());
                System.out.println("Métrica: " + id + ". Valor: " + counter.getCount());
            }
        }
    }
}