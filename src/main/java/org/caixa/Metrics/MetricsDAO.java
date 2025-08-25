package org.caixa.Metrics;

import java.util.Date;
import java.util.List;

import org.caixa.Historico.MetricsModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
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
  
}
