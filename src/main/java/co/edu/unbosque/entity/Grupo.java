package co.edu.unbosque.entity;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "grupos")
public class Grupo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_grupo", length = 1)
    private String idGrupo;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @Column(name = "estado", length = 1)
    private String estado;

    public Grupo() {}

    public String getIdGrupo() { return idGrupo; }
    public void setIdGrupo(String idGrupo) { this.idGrupo = idGrupo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}