package org.caixa.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
@ActivateRequestContext
public class SalvarDAO {

    @PersistenceContext(unitName = "historico")
    private EntityManager em;

    @Transactional
    public void salvar(Object entidade) {
        em.persist(entidade);
    }
}
