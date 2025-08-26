package org.caixa.Metrics;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class MetricsFilter implements ContainerResponseFilter {

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        int status = responseContext.getStatus();
        String statusCode = String.valueOf(status);
        String path = requestContext.getUriInfo().getPath();
        Counter counter = registry.getCounter(new MetricID(path+"_status_" + statusCode));
        if(counter == null){
           counter =  registry.counter(new MetricID(path+"_status_"+statusCode));
        }
        counter.inc();

    }
}