package org.caixa.Metrics;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.caixa.Historico.MetricsModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
@ActivateRequestContext
public class MetricsDAO {

  @PersistenceContext
  private EntityManager em;

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void save(MetricsModel metrics) {
    if(metrics.id == null) {
      em.persist(metrics);
    }
    em.merge(metrics);
  }

  @Transactional
  public List<MetricsModel> findByDate(Date data) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT *")
        .append(" FROM METRICAS")
        .append(" WHERE CAST(DT_METRICA AS DATE) = CAST(:data AS DATE)");

    Query query = em.createNativeQuery(sql.toString(), MetricsModel.class);
    query.setParameter("data", data);

    return query.getResultList();
  }

  @Transactional
  public MetricsModel findByMetricaByDate(String metrica, Date data) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT *")
        .append(" FROM METRICAS")
        .append(" WHERE CAST(DT_METRICA AS DATE) = CAST(:data AS DATE)")
        .append(" AND CO_NOME = :metrica");

    Query query = em.createNativeQuery(sql.toString(), MetricsModel.class);
    query.setParameter("data", data);
    query.setParameter("metrica", metrica);
    try{
      MetricsModel result = (MetricsModel) query.getSingleResult();
      return result;
    }catch(NoResultException e){
      return null;
    }
  }

  @Transactional
  public List<MetricsModel> findByEndpointByDate(List<String> endpoint,Date data) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT *")
        .append(" FROM METRICAS")
        .append(" WHERE CAST(DT_METRICA AS DATE) = CAST(:data AS DATE)")
        .append(" AND CO_NOME IN (:id1, :id2, :id3)");

        System.out.println(endpoint.stream()
                           .map(e -> e)
                           .collect(Collectors.joining(", ")));
    Query query = em.createNativeQuery(sql.toString(), MetricsModel.class);
    query.setParameter("data", data);
    query.setParameter("id1", endpoint.get(1));
    query.setParameter("id2", endpoint.get(2));
    query.setParameter("id3", endpoint.get(3));

    return query.getResultList();
  }
  
}
