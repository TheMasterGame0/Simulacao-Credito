package org.caixa.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.caixa.model.Produto;

import java.math.BigDecimal;

@ApplicationScoped
@ActivateRequestContext
public class ConsultaDAO {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public  Produto getProduto(Integer prazo, BigDecimal valorDesejado) {
        final StringBuilder consulta = new StringBuilder();
        consulta.append("SELECT * FROM dbo.Produto")
                .append(" WHERE NU_MINIMO_MESES <= :prazo")
                .append(" AND :prazo <= NU_MAXIMO_MESES")
                .append(" AND VR_MINIMO <= :valorDesejado")
                .append(" AND :valorDesejado <= VR_MAXIMO");

        Query query =  em.createNativeQuery(consulta.toString(), Produto.class);
        query.setParameter("prazo", prazo);
        query.setParameter("valorDesejado", valorDesejado);

        return (Produto) query.getSingleResult();
    }


}
