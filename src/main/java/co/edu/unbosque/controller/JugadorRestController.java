package co.edu.unbosque.controller;

import java.util.List;
import java.time.LocalDateTime;

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

import co.edu.unbosque.entity.Jugador;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.JugadorServiceAPI;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@CrossOrigin(origins = "http://localhost:8181",maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/jugador")
public class JugadorRestController {

	@Autowired
	private JugadorServiceAPI jugadorServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	private static final Logger logger = LogManager.getLogger(JugadorRestController.class);

	@GetMapping(value = "/getAll")
	public List<Jugador> getAll() {
		logger.info("Consulta general de jugadores");
		return jugadorServiceAPI.getAll();
	}

	@PostMapping(value = "/saveJugador")
	public ResponseEntity<Jugador> save(@RequestBody Jugador jugador) {
		logger.info("Intentando guardar jugador");

		String accion = "ACTUALIZAR";

		if (jugador.getIdJugador() == null || jugador.getIdJugador() == 0L) {
			accion = "CREAR";
		}
		
		Jugador obj = jugadorServiceAPI.save(jugador);
		logger.info("Jugador guardado correctamente");

		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(obj.getIdJugador().intValue()); 
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("jugadores");
		auditoria.setIdRegistroAfectado(obj.getIdJugador().intValue());
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Jugador>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Jugador> getJugadorById(@PathVariable Long id) throws ResourceNotFoundException {
		Jugador jugador = jugadorServiceAPI.get(id);
		if (jugador == null) {
			throw new ResourceNotFoundException("Record not found for <Jugador>" + id);
		}

		return ResponseEntity.ok().body(jugador);
	}

	@DeleteMapping(value = "/deleteJugador/{id}")
	public ResponseEntity<Jugador> delete(@PathVariable Long id) {
		Jugador jugador = jugadorServiceAPI.get(id);
		
		if (jugador != null) {
			
			jugador.setEstado("I");
			jugadorServiceAPI.save(jugador);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(id.intValue()); 
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("jugadores");
			auditoria.setIdRegistroAfectado(id.intValue());
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);

			logger.info("Jugador desactivado correctamente {}", id);
		} else {
			return new ResponseEntity<>(jugador, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jugador, HttpStatus.OK);
	}
}