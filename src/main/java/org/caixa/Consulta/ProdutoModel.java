package org.caixa.Consulta;

import io.quarkus.agroal.DataSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@DataSource("consulta")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PRODUTO")
public class ProdutoModel {
    @Id
    @Column(name = "CO_PRODUTO", nullable = false)
    public Integer id;

    @Column(name = "NO_PRODUTO", nullable = false)
    public String descricao;

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
