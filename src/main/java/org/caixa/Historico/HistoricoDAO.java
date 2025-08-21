package org.caixa.Historico;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.caixa.DTO.FiltroDTO;
import org.caixa.DTO.RegistroDTO;

import java.util.Date;
import java.util.List;

@ApplicationScoped
@ActivateRequestContext
public class HistoricoDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Long save(SimulacaoModel simulacao) {
        em.persist(simulacao);
        return simulacao.idSimulacao;
    }

    @Transactional
    public List<RegistroDTO> getSimulacoes(FiltroDTO filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT  NU_SIMULACAO, VR_DESEJADO, PRAZO, VR_TOTAL_PARCELAS")
            .append(" FROM  SIMULACAO")
            .append(" LIMIT :qtdPorPagina OFFSET :pagina ");

        Query query = em.createNativeQuery(sql.toString(), RegistroDTO.class);
        query.setParameter("qtdPorPagina", filtro.getQtdRegistrosPagina());
        query.setParameter("pagina",  (filtro.getPagina() -1)*filtro.getQtdRegistrosPagina());

        return query.getResultList();
    }

    public Long getTotalResgitstros(){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT  COUNT(*)")
                .append(" FROM  SIMULACAO");
        return (Long) em.createNativeQuery(sql.toString()).getSingleResult();
    }

    public List<RegistroDTO> getSimulacoesPorDiaPorProduto(Date data, Integer produto) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT  NU_SIMULACAO, VR_DESEJADO, PRAZO, VR_TOTAL_PARCELAS")
            .append(" FROM  SIMULACAO")
            .append(" WHERE CAST(DT_SIMULACAO AS DATE) = :data")
            .append(" AND NU_PRODUTO = :produto");
        System.out.println(data);
        Query query = em.createNativeQuery(sql.toString(), RegistroDTO.class);
        query.setParameter("data", data);
        query.setParameter("produto", produto);

        return query.getResultList();

    }
}
