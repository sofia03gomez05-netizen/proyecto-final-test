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

import co.edu.unbosque.entity.Equipo;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.EquipoServiceAPI;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@CrossOrigin(origins = "http://localhost:8181",maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/equipo")
public class EquipoRestController {

	@Autowired
	private EquipoServiceAPI equipoServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	private static final Logger logger = LogManager.getLogger(EquipoRestController.class);

	@GetMapping(value = "/getAll")
	public List<Equipo> getAll() {
		logger.info("Consulta general de equipos");
		return equipoServiceAPI.getAll();
	}

	@PostMapping(value = "/saveEquipo")
	public ResponseEntity<Equipo> save(@RequestBody Equipo equipo) {
		logger.info("Intentando guardar equipo");

		String accion = "ACTUALIZAR";

		if (equipo.getIdEquipo() == null || equipo.getIdEquipo() == 0L) {
			accion = "CREAR";
		}
		Equipo obj = equipoServiceAPI.save(equipo);

		logger.info("Equipo guardado correctamente");

		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(obj.getIdEquipo()); 
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("equipos");
		auditoria.setIdRegistroAfectado(obj.getIdEquipo()); 
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Equipo>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Equipo> getEquipoById(@PathVariable Long id) throws ResourceNotFoundException {
		Equipo equipo = equipoServiceAPI.get(id);
		if (equipo == null) {
			throw new ResourceNotFoundException("Record not found for <Equipo>" + id);
		}

		return ResponseEntity.ok().body(equipo);
	}

	@DeleteMapping(value = "/deleteEquipo/{id}")
	public ResponseEntity<Equipo> delete(@PathVariable Long id) {
		Equipo equipo = equipoServiceAPI.get(id);
		if (equipo != null) {

			equipo.setEstado("I");
			equipoServiceAPI.save(equipo);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(id.intValue());
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("equipos");
			auditoria.setIdRegistroAfectado(id.intValue()); 
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);

			logger.info("Equipo desactivado correctamente {}", id);

		} else {
			return new ResponseEntity<>(equipo, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(equipo, HttpStatus.OK);
	}
}