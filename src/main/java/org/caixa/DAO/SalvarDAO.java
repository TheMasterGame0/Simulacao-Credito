package org.caixa.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class SalvarDAO {
    @PersistenceContext(unitName = "historico")
    EntityManager em;
}
