package org.caixa.model;

import io.quarkus.agroal.DataSource;
import jakarta.persistence.*;

import java.math.BigDecimal;


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
@Table(name = "Simulacao")
public class Simulacao {
    @Id
    @GeneratedValue
    public Long idSimulacao;

    @Column(name = "tipo", nullable = false)
    public String tipo;

    @Column(name = "valorDesejado", nullable = false, scale= 2)
    public BigDecimal valorDesejado;

    @Column(name = "prazo", nullable = false)
    public Integer prazo;

    @Column(name = "valorTotalParcelas", nullable = false, scale= 2)
    public BigDecimal valorTotalParcelas;

}
