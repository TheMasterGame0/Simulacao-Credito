package org.caixa.Metrics;

import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class MetricsCollector {

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Scheduled(every = "1m") // ou "24h" para diário
    void coletarMetricas() {
        // Exemplo: coleta de contadores por status
        System.out.println(registry.getMetrics());
        long simularStatus200 = registry.getCounter(new MetricID("/api/simular_status_200")).getCount();
        long simularTotal = registry.getCounter(new MetricID("org.caixa.rest.SimulacaoRest.qtdRequisicoesSimulacoes")).getCount();

        // Aqui você pode persistir em banco, arquivo, etc.
        System.out.printf("Status 200: %d | Total: %d%n", simularStatus200, simularTotal);
    }
}