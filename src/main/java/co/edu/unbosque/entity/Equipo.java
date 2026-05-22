package co.edu.unbosque.entity;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "equipos")
public class Equipo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Integer idEquipo;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "id_grupo", length = 1)
    private String idGrupo;

    @Column(name = "entrenador", length = 100)
    private String entrenador;

    @Column(name = "estado", length = 1)
    private String estado;

    @Column(name = "bandera", length = 100)
    private String bandera;

    public Equipo() {}

    public Integer getIdEquipo() { return idEquipo; }
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdGrupo() { return idGrupo; }
    public void setIdGrupo(String idGrupo) { this.idGrupo = idGrupo; }

    public String getEntrenador() { return entrenador; }
    public void setEntrenador(String entrenador) { this.entrenador = entrenador; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getBandera() { return bandera; }
    public void setBandera(String bandera) { this.bandera = bandera; }
}