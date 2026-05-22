package co.edu.unbosque.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.entity.Usuario;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.UsuarioServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;

import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.service.api.CorreoServiceAPI;
import co.edu.unbosque.service.api.ParametroServiceAPI;
import co.edu.unbosque.utils.Utilidad;
import co.edu.unbosque.utils.VerificacionContrasenaException;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

//@CrossOrigin(origins = "http://localhost:8181",maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/usuario")
public class UsuarioRestController {

	@Autowired
	private UsuarioServiceAPI usuarioServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	@Autowired
	private ParametroServiceAPI parametroServiceAPI;

	@Autowired
	private CorreoServiceAPI emailServiceAPI;

	private static final Logger logger = LogManager.getLogger(UsuarioRestController.class);

	@GetMapping(value = "/getAll")
	public List<Usuario> getAll() {
		logger.info("Consulta general de usuarios");
		return usuarioServiceAPI.getAll();
	}

	@PostMapping(value = "/saveUsuario")
	public ResponseEntity<Usuario> save(@RequestBody Usuario usuario) throws VerificacionContrasenaException {
		logger.info("Intentando guardar usuario: {}", usuario.getUsername());

		Utilidad utilidad = new Utilidad();
		String accion = "ACTUALIZAR";
		String password = usuario.getPassword();

		if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {

			if (usuario.getPassword().length() != 40) {

				if (!utilidad.validarComplejidadClave(usuario.getPassword())) {
					logger.warn("Estructura de contraseña inválida para el usuario: {}", usuario.getUsername());
					throw new VerificacionContrasenaException("La contraseña debe tener:"
							+ "\n - Entre 6 y 8 caracteres" 
							+ "\n - Incluir al menos una mayúscula "
							+ "\n - Incluir una minúscula" 
							+ "\n - Incluir un número.");
				}

				usuario.setPassword(utilidad.generarHash(usuario.getPassword()));
				usuario.setFechaUltClave(LocalDateTime.now());
			}
		}

		// Validar si crea o actualiza
		if (usuario.getIdUsuario() == null || usuario.getIdUsuario() == 0L) {
			accion = "CREAR";
			usuario.setIntentos(0);
			if (usuario.getFechaUltClave() == null) {
				usuario.setFechaUltClave(LocalDateTime.now());
			}
		}
		Usuario obj = usuarioServiceAPI.save(usuario);

		logger.info("Usuario guardado correctamente: {}", obj.getUsername());
		
		if (accion.equals("CREAR") && obj.getIdRol() != 1 && obj.getCorreo() != null) {
		    emailServiceAPI.enviarCredenciales(obj.getCorreo(), obj.getUsername(), password);
		}

		// Registramos en auditoria
		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(obj.getIdUsuario());
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("usuarios");
		auditoria.setIdRegistroAfectado(obj.getIdUsuario());
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Usuario>(obj, HttpStatus.OK); // 200
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) throws ResourceNotFoundException {
		Usuario usuario = usuarioServiceAPI.get(id);
		if (usuario == null) {
			throw new ResourceNotFoundException("Record not found for <Usuario>" + id);
		}

		return ResponseEntity.ok().body(usuario);
	}

	@DeleteMapping(value = "/deleteUsuario/{id}")
	public ResponseEntity<Usuario> delete(@PathVariable Long id) {
		Usuario usuario = usuarioServiceAPI.get(id);
		if (usuario != null) {

			usuario.setEstado("I");
			usuarioServiceAPI.save(usuario);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(id.intValue());
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("usuarios");
			auditoria.setIdRegistroAfectado(id.intValue());
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);

			logger.info("Usuario desactivado correctamente {}", id);

		} else {
			return new ResponseEntity<>(usuario, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(usuario, HttpStatus.OK);
	}

	@PostMapping(value = "/login")
	public ResponseEntity<?> login(@RequestBody Usuario loginData) {
		List<Usuario> usuarios = usuarioServiceAPI.getAll();
		Usuario u = null;

		for (Usuario usuario : usuarios) {
			if (usuario.getUsername().equals(loginData.getUsername())) {
				u = usuario;
				break;
			}
		}

		if (u == null)
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		if ("I".equals(u.getEstado()))
			return new ResponseEntity<>("Esta cuenta se encuentra inactiva.", HttpStatus.FORBIDDEN);

		Utilidad utilidad = new Utilidad();
		String hashContrasena = utilidad.generarHash(loginData.getPassword());

		if (u.getPassword().equals(hashContrasena)) {
			if (u.getIdRol() != 1 && u.getFechaUltClave() != null) {
				try {
					int limiteDiasContrasena = parametroServiceAPI.get(1L).getValorNumero();

					long fechaActual = LocalDateTime.now().toLocalDate().toEpochDay();
					long fechaUltimaClave = u.getFechaUltClave().toLocalDate().toEpochDay();

					long diasContrasena = fechaActual - fechaUltimaClave;

					if (diasContrasena > limiteDiasContrasena) {
						logger.warn("Contraseña expirada por tiempo para: {}", u.getUsername());
						return new ResponseEntity<>("CONTRASENA_EXPIRADA", HttpStatus.PRECONDITION_REQUIRED);
					}
				} catch (Exception e) {
					logger.error("Error en la resta de días: {}", e.getMessage());
				}
			}

			u.setIntentos(0);
			usuarioServiceAPI.save(u);
			logger.info("Inicio de sesión exitoso para el usuario: {}", u.getUsername());
			return new ResponseEntity<>(u, HttpStatus.OK);
		}

		if (u.getIdRol() != 1) { // el id del admin = 1
			u.setIntentos(u.getIntentos() + 1);

			if (u.getIntentos() >= 3) {
				u.setEstado("I");
				logger.error("Cuenta INACTIVADA por exceso de intentos: {}", u.getUsername());
			}

			usuarioServiceAPI.save(u);

			if ("I".equals(u.getEstado())) {
				return new ResponseEntity<>("Has superado el límite de intentos. Tu cuenta ha sido inactivada.",
						HttpStatus.FORBIDDEN);
			}

			return new ResponseEntity<>("Credenciales incorrectas. Intentos restantes: " + (3 - u.getIntentos()),
					HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<>("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
	}

}