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

import co.edu.unbosque.entity.Partido;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.PartidoServiceAPI;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@CrossOrigin(origins = "http://localhost:8181",maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/partido")
public class PartidoRestController {

	@Autowired
	private PartidoServiceAPI partidoServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	private static final Logger logger = LogManager.getLogger(PartidoRestController.class);

	@GetMapping(value = "/getAll")
	public List<Partido> getAll() {
		logger.info("Consulta general de partidos");
		return partidoServiceAPI.getAll();
	}

	@PostMapping(value = "/savePartido")
	public ResponseEntity<Partido> save(@RequestBody Partido partido) {
		logger.info("Intentando guardar partido");

		String accion = "ACTUALIZAR";

		if (partido.getIdPartido() == null || partido.getIdPartido() == 0L) {
			accion = "CREAR";
		}
		
		Partido obj = partidoServiceAPI.save(partido);
		logger.info("Partido guardado correctamente");

		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(obj.getIdPartido().intValue()); 
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("partidos");
		auditoria.setIdRegistroAfectado(obj.getIdPartido().intValue());
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Partido>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Partido> getPartidoById(@PathVariable Long id) throws ResourceNotFoundException {
		Partido partido = partidoServiceAPI.get(id);
		if (partido == null) {
			throw new ResourceNotFoundException("Record not found for <Partido>" + id);
		}

		return ResponseEntity.ok().body(partido);
	}

	@DeleteMapping(value = "/deletePartido/{id}")
	public ResponseEntity<Partido> delete(@PathVariable Long id) {
		Partido partido = partidoServiceAPI.get(id);
		
		if (partido != null) {
			
			partido.setEstado("I");
			partidoServiceAPI.save(partido);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(id.intValue()); 
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("partidos");
			auditoria.setIdRegistroAfectado(id.intValue());
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);

			logger.info("Partido desactivado correctamente {}", id);
		} else {
			return new ResponseEntity<>(partido, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(partido, HttpStatus.OK);
	}
}