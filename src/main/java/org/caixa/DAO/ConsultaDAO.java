package org.caixa.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
@ActivateRequestContext
public class ConsultaDAO {

    @PersistenceContext
    EntityManager em;


}
