package org.caixa.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.caixa.Historico.MetricsModel;
import org.eclipse.microprofile.metrics.*;
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
        "/api/simulacoes_por_dia_status_200",
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
                    if(!NOME_METRICAS_TIMERS.contains(metrica)){
                        // retorna um counter ja existente ou cria um novo (caso não exista)
                        Counter counter = registry.counter(new MetricID(metrica.getNome()));
                        long difference = metrica.getValor() - counter.getCount();
                        if (difference > 0) {
                            counter.inc(difference);
                        }
                    }
                }
                System.out.println("Métricas carregadas!");
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

        // Timer timer;
        // for(String id: NOME_METRICAS_TIMERS){
        //     MetricsModel metrica = dao.findByMetricaByDate(id, new Date());
            
        //     timer = registry.getTimer(new MetricID(id));
        //     if(timer!=null) {
        //         Snapshot snapshot = timer.getSnapshot();
        //         Long tsMax = snapshot.getMax()/1000000;
        //         Long tsMin = snapshot.getMin()/1000000;
        //         Double tsMedio = snapshot.getMean()/1000000;

        //         if(metrica != null){
        //             metrica.setTsMax(Math.max(metrica.getTsMax(),tsMax));
        //             metrica.setTsMin(Math.min(metrica.getTsMin(),tsMin));
        //             metrica.setTsMedio((metrica.getTsMedio() + tsMedio)/2);
        //             dao.save(metrica);           
        //         }else{
        //             dao.save(MetricsModel.builder()
        //                 .nome(id)
        //                 .tsMedio(tsMedio)
        //                 .tsMax(tsMax)
        //                 .tsMin(tsMin)
        //                 .data(new Date())
        //                 .build());
        //         }
                
        //         System.out.println("Métrica: "+ id +". TSMAX: "+ tsMax +". TSMIN: "+ tsMin+". TSMEDIO: "+ tsMedio);
        //     }
        // }

    }
}