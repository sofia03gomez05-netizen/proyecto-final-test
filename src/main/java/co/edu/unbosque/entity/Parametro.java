package co.edu.unbosque.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "parametros")
public class Parametro implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Integer idParametro;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @Column(name = "valor_texto", length = 50, nullable = false)
    private String valorTexto;

    @Column(name = "valor_numero")
    private Integer valorNumero;

    @Column(name = "fecha_inicial")
    private LocalDateTime fechaInicial;

    @Column(name = "fecha_final")
    private LocalDateTime fechaFinal;

    @Column(name = "estado", length = 1)
    private String estado;

    public Parametro() {}

    public Integer getIdParametro() { return idParametro; }
    public void setIdParametro(Integer idParametro) { this.idParametro = idParametro; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getValorTexto() { return valorTexto; }
    public void setValorTexto(String valorTexto) { this.valorTexto = valorTexto; }

    public Integer getValorNumero() { return valorNumero; }
    public void setValorNumero(Integer valorNumero) { this.valorNumero = valorNumero; }

    public LocalDateTime getFechaInicial() { return fechaInicial; }
    public void setFechaInicial(LocalDateTime fechaInicial) { this.fechaInicial = fechaInicial; }

    public LocalDateTime getFechaFinal() { return fechaFinal; }
    public void setFechaFinal(LocalDateTime fechaFinal) { this.fechaFinal = fechaFinal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}