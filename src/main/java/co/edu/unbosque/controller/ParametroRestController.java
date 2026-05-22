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

import co.edu.unbosque.entity.Parametro;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.ParametroServiceAPI;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/parametro")
public class ParametroRestController {

	@Autowired
	private ParametroServiceAPI parametroServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	private static final Logger logger = LogManager.getLogger(ParametroRestController.class);

	@GetMapping(value = "/getAll")
	public List<Parametro> getAll() {
		logger.info("Consulta general de parámetros");
		return parametroServiceAPI.getAll();
	}

	@PostMapping(value = "/saveParametro")
	public ResponseEntity<Parametro> save(@RequestBody Parametro parametro) {
		logger.info("Intentando guardar parámetro");

		String accion = "ACTUALIZAR";

		if (parametro.getIdParametro() == null || parametro.getIdParametro() == 0L) {
			accion = "CREAR";
		}
		
		Parametro obj = parametroServiceAPI.save(parametro);
		logger.info("Parámetro guardado correctamente");

		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(obj.getIdParametro().intValue()); 
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("parametros");
		auditoria.setIdRegistroAfectado(obj.getIdParametro().intValue());
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Parametro>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Parametro> getParametroById(@PathVariable Long id) throws ResourceNotFoundException {
		Parametro parametro = parametroServiceAPI.get(id);
		
		if (parametro == null) {
			throw new ResourceNotFoundException("Record not found for <Parametro> " + id);
		}

		return ResponseEntity.ok().body(parametro);
	}

	@DeleteMapping(value = "/deleteParametro/{id}")
	public ResponseEntity<Parametro> delete(@PathVariable Long id) {
		Parametro parametro = parametroServiceAPI.get(id);
		
		if (parametro != null) {
			parametroServiceAPI.delete(id);
			logger.info("Parámetro eliminado correctamente: {}", id);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(id.intValue()); 
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("parametros");
			auditoria.setIdRegistroAfectado(id.intValue());
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);
		} else {
			return new ResponseEntity<Parametro>(parametro, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Parametro>(parametro, HttpStatus.OK);
	}
}