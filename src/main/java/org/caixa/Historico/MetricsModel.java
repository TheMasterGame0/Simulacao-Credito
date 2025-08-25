package org.caixa.Historico;

import java.util.Date;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "METRICAS")
public class MetricsModel extends PanacheEntityBase{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "NU_METRICA", nullable = false)
  public Long id;

  @Column(name = "CO_NOME", nullable = false)
  public String nome; // Trocar pra usar string

  @Column(name = "NU_VALOR", nullable = false)
  public Long valor;

  @Column(name = "DT_METRICA", nullable = false)
  public Date data;
}
