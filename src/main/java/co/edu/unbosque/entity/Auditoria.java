package co.edu.unbosque.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_log", nullable = false)
	private long id;

	@Column(name = "id_usuario")
	private int idUsuario;

	@Column(name = "accion")
	private String accionAudtria;

	@Column(name = "tabla_afectada")
	private String tablaAfectada;

	@Column(name = "id_registro_afectado")
	private int idRegistroAfectado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_hora")
	private LocalDateTime fechaHoraAudtria;

	@Column(name = "ipcliente")
	private String ipCliente;

	public Auditoria() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccionAudtria() {
		return this.accionAudtria;
	}

	public void setAccionAudtria(String accionAudtria) {
		this.accionAudtria = accionAudtria;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getTablaAfectada() {
		return tablaAfectada;
	}

	public void setTablaAfectada(String tablaAfectada) {
		this.tablaAfectada = tablaAfectada;
	}

	public int getIdRegistroAfectado() {
		return idRegistroAfectado;
	}

	public void setIdRegistroAfectado(int idRegistroAfectado) {
		this.idRegistroAfectado = idRegistroAfectado;
	}

	public LocalDateTime getFechaHoraAudtria() {
		return fechaHoraAudtria;
	}

	public void setFchaHoraAudtria(LocalDateTime fchaHoraAudtria) {
		this.fechaHoraAudtria = fchaHoraAudtria;
	}

	public String getIpCliente() {
		return ipCliente;
	}

	public void setIpCliente(String ipCliente) {
		this.ipCliente = ipCliente;
	}

}