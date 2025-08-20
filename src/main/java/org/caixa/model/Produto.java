package org.caixa.model;

import io.quarkus.agroal.DataSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@DataSource("default")
@Entity
@Table(name = "Produto")
public class Produto {
    @Id
    public Integer id;

    @Column(name = "NO_PRODUTO", nullable = false)
    public String nome;

    @Column(name = "PC_TAXA_JUROS", nullable = false, precision=10, scale= 9)
    public BigDecimal juros;

    @Column(name = "NU_MINIMO_MESES", nullable = false)
    public Integer minMeses;

    @Column(name = "NU_MAXIMO_MESES", nullable = false)
    public Integer maxMeses;

    @Column(name = "VR_MINIMO", nullable = false, precision=18, scale= 2)
    public BigDecimal valorMinimo;

    @Column(name = "VR_MAXIMO", nullable = false, precision=18, scale= 2)
    public BigDecimal valorMaximo;
}
