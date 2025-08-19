package org.caixa.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ConsultaDAO {
    @PersistenceContext(unitName = "default")
    EntityManager em;


}
