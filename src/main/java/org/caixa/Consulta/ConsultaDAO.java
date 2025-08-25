package org.caixa.Consulta;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

import org.caixa.Exception.ErrorMensagem;
import org.caixa.Exception.ErrosPrevistoException;

@ApplicationScoped
@ActivateRequestContext
public class ConsultaDAO {

    @PersistenceContext(unitName = "consulta")
    EntityManager em;

    @Transactional
    public ProdutoModel getProduto(Integer prazo, BigDecimal valorDesejado) {
        try{
            final StringBuilder consulta = new StringBuilder();
            consulta.append("SELECT * FROM dbo.Produto")
                    .append(" WHERE NU_MINIMO_MESES <= :prazo")
                    .append(" AND :prazo <= NU_MAXIMO_MESES")
                    .append(" AND VR_MINIMO <= :valorDesejado")
                    .append(" AND :valorDesejado <= VR_MAXIMO");

            Query query =  em.createNativeQuery(consulta.toString(), ProdutoModel.class);
            query.setParameter("prazo", prazo);
            query.setParameter("valorDesejado", valorDesejado);

            return (ProdutoModel) query.getSingleResult();
        }catch (NoResultException e){
            throw new ErrosPrevistoException(ErrorMensagem.builder().mensagem("Os dados passados não correspondem a nenhum produto cadastrado.").build(), 400);
        }
    }

    @Transactional
    public List<ProdutoModel> getProdutos() {
        String consulta ="SELECT * FROM dbo.Produto";
        Query query =  em.createNativeQuery(consulta, ProdutoModel.class);
        return query.getResultList();
    }


}
