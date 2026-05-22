package co.edu.unbosque.entity;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "estadios")
public class Estadio implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadio")
    private Integer idEstadio;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @Column(name = "estado", length = 1)
    private String estado;

    public Estadio() {}

    public Integer getIdEstadio() { return idEstadio; }
    public void setIdEstadio(Integer idEstadio) { this.idEstadio = idEstadio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}