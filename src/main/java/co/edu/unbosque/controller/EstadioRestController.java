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

import co.edu.unbosque.entity.Estadio;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.EstadioServiceAPI;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@CrossOrigin(origins = "http://localhost:8181",maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/estadio")
public class EstadioRestController {

	@Autowired
	private EstadioServiceAPI estadioServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	private static final Logger logger = LogManager.getLogger(EstadioRestController.class);

	@GetMapping(value = "/getAll")
	public List<Estadio> getAll() {
		logger.info("Consulta general de estadios");
		return estadioServiceAPI.getAll();
	}

	@PostMapping(value = "/saveEstadio")
	public ResponseEntity<Estadio> save(@RequestBody Estadio estadio) {
		logger.info("Intentando guardar estadio");

		String accion = "ACTUALIZAR";

		if (estadio.getIdEstadio() == null || estadio.getIdEstadio() == 0L) {
			accion = "CREAR";
		}
		
		Estadio obj = estadioServiceAPI.save(estadio);
		logger.info("Estadio guardado correctamente");

		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(obj.getIdEstadio().intValue()); 
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("estadios");
		auditoria.setIdRegistroAfectado(obj.getIdEstadio().intValue());
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Estadio>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Estadio> getEstadioById(@PathVariable Long id) throws ResourceNotFoundException {
		Estadio estadio = estadioServiceAPI.get(id);
		if (estadio == null) {
			throw new ResourceNotFoundException("Record not found for <Estadio>" + id);
		}

		return ResponseEntity.ok().body(estadio);
	}

	@DeleteMapping(value = "/deleteEstadio/{id}")
	public ResponseEntity<Estadio> delete(@PathVariable Long id) {
		Estadio estadio = estadioServiceAPI.get(id);
		
		if (estadio != null) {
			
			estadio.setEstado("I");
			estadioServiceAPI.save(estadio);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(id.intValue()); 
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("estadios");
			auditoria.setIdRegistroAfectado(id.intValue());
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);

			logger.info("Estadio desactivado correctamente {}", id);
		} else {
			return new ResponseEntity<>(estadio, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(estadio, HttpStatus.OK);
	}
}