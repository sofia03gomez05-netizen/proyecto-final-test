package co.edu.unbosque.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "partidos")
public class Partido implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partido")
    private Long idPartido;

    @Column(name = "id_equipo_local")
    private Integer idEquipoLocal;

    @Column(name = "id_equipo_visitante")
    private Integer idEquipoVisitante;

    @Column(name = "fase", length = 50)
    private String fase;

    @Column(name = "goles_local")
    private Integer golesLocal;

    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "estado", length = 1)
    private String estado;

    public Partido() {}

    public Long getIdPartido() { return idPartido; }
    public void setIdPartido(Long idPartido) { this.idPartido = idPartido; }

    public Integer getIdEquipoLocal() { return idEquipoLocal; }
    public void setIdEquipoLocal(Integer idEquipoLocal) { this.idEquipoLocal = idEquipoLocal; }

    public Integer getIdEquipoVisitante() { return idEquipoVisitante; }
    public void setIdEquipoVisitante(Integer idEquipoVisitante) { this.idEquipoVisitante = idEquipoVisitante; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(Integer golesVisitante) { this.golesVisitante = golesVisitante; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}