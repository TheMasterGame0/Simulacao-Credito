package org.caixa.Metrics;

import java.util.*;

import org.caixa.Historico.MetricsModel;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.Timer;
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

    private List<MetricsModel> metricas = new ArrayList<>();

    void onStart(@Observes StartupEvent ev) {
       this.metricas = dao.findByDate(new Date());

        // Carrega as métricas(se existirem)
        if (!metricas.isEmpty()) {
            try {
                for(MetricsModel metrica : metricas) {
                    if(!NOME_METRICAS_TIMERS.contains(metrica.nome)){
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

    @Scheduled(every = "30m")
    void coletarMetricas() {
        Counter counter;
        MetricsModel metrica;
        for(String id : NOME_METRICAS_COUNTERS){
            counter = registry.getCounter(new MetricID(id));
            if(counter!=null) {
                Optional<Long> idEncontrado = this.metricas.stream()
                        .filter(m -> id.equals(m.getNome()))
                        .map(MetricsModel::getId)
                        .findFirst();
                metrica = MetricsModel.builder().nome(id).valor(counter.getCount()).data(new Date()).build();
                if(idEncontrado.isPresent()){
                    metrica.setId(idEncontrado.get());
                }
                dao.save(metrica);
                //System.out.println("Métrica: " + id + ". Valor: " + counter.getCount());
            }
        }

         Timer timer;
         for(String id: NOME_METRICAS_TIMERS){
             metrica = dao.findByMetricaByDate(id, new Date());
            
             timer = registry.getTimer(new MetricID(id));
             if(timer!=null) {
                 Snapshot snapshot = timer.getSnapshot();
                 Long tsMax = snapshot.getMax()/1000000;
                 Long tsMin = null;
                 Double tsMedio = null;

                 if(metrica != null){
                     tsMin = (snapshot.getMin()!=0)? (metrica.getTsMin() + snapshot.getMin()/1000000)/2: metrica.getTsMin();
                     tsMedio = (snapshot.getMean()!=0)? (metrica.getTsMedio()+ snapshot.getMean()/1000000)/2 : metrica.getTsMedio();
                     metrica.setTsMax(Math.max(metrica.getTsMax(),tsMax));
                     metrica.setTsMin(tsMin);
                     metrica.setTsMedio(tsMedio);
                     dao.save(metrica);
                 }else{
                     dao.save(MetricsModel.builder()
                         .nome(id)
                         .tsMedio(snapshot.getMean()/1000000)
                         .tsMax(tsMax)
                         .tsMin(snapshot.getMin()/1000000)
                         .data(new Date())
                         .build());
                 }
                 //System.out.println("Métrica: "+ id +". TSMAX: "+ tsMax +". TSMIN: "+ tsMin+". TSMEDIO: "+ tsMedio);
             }
         }

    }
}