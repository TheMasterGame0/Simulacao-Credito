package org.caixa.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

import org.caixa.model.Simulacao;

@ApplicationScoped
@ActivateRequestContext
public class HistoricoDAO {

    @PersistenceContext(unitName = "historico")
    private EntityManager em;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Long save(Simulacao simulacao) {
        em.persist(simulacao);
        return simulacao.idSimulacao;
    }

    public List<Simulacao> getSimulacoes() {
        return em.createQuery("select s from Simulacao s").getResultList();
    }
}
