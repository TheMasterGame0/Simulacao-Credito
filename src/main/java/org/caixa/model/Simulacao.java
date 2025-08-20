package org.caixa.model;

import io.quarkus.agroal.DataSource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Usage (more example on the documentation)
 * {@code
 *     public void doSomething() {
 *         MyEntity entity1 = new MyEntity();
 *         entity1.field = "field-1";
 *         entity1.persist();
 *
 *         List<MyEntity> entities = MyEntity.listAll();
 *     }
 * }
 */

@DataSource("historico")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Simulacao")
public class Simulacao {
    @Id
    @GeneratedValue
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
