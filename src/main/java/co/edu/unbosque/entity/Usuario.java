package co.edu.unbosque.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Integer idUsuario;

	@Column(name = "username", length = 50, nullable = false, unique = true)
	private String username;

	@Column(name = "password", length = 255, nullable = false)
	private String password;

	@Column(name = "nombreapellido", length = 50, nullable = false)
	private String nombreapellido;

	@Column(name = "id_rol", nullable = false)
	private Integer idRol;

	@Column(name = "estado", length = 1, nullable = false)
	private String estado;

	@Column(name = "intentos")
	private int intentos;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_ult_clave", nullable = true)
	private LocalDateTime  fechaUltClave;
	
	@Column(name = "correo", length = 100, nullable = true) 
	private String correo;
	
	public Usuario() {
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNombreapellido() {
		return nombreapellido;
	}

	public void setNombreapellido(String nombreapellido) {
		this.nombreapellido = nombreapellido;
	}

	public Integer getIdRol() {
		return idRol;
	}

	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getIntentos() {
		return intentos;
	}

	public void setIntentos(int intentos) {
		this.intentos = intentos;
	}

	public LocalDateTime  getFechaUltClave() {
		return fechaUltClave;
	}

	public void setFechaUltClave(LocalDateTime  fechaUltClave) {
		this.fechaUltClave = fechaUltClave;
	}
	
	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

}