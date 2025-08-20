package org.caixa.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.caixa.model.Simulacao;

@ApplicationScoped
@ActivateRequestContext
public class SalvarDAO {

    @PersistenceContext(unitName = "historico")
    private EntityManager em;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Long save(Simulacao simulacao) {
        em.persist(simulacao);
        return simulacao.idSimulacao;
    }
}
