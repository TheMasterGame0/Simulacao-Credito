package org.caixa.Historico;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SIMULACAO")
public class SimulacaoModel extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NU_SIMULACAO", nullable = false)
    public Long idSimulacao;

    @Column(name = "DT_SIMULACAO", nullable = false)
    public Date dataSimulacao;

    @Column(name = "NU_PRODUTO", nullable = false)
    public Integer produto;

    @Column(name = "VR_DESEJADO", nullable = false, scale= 2)
    public BigDecimal valorDesejado;

    @Column(name = "PRAZO", nullable = false)
    public Integer prazo;

    @Column(name = "VR_TOTAL_PARCELAS", nullable = false, scale= 2)
    public BigDecimal valorTotalParcelas;

}
