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

import co.edu.unbosque.entity.Grupo;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.GrupoServiceAPI;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@CrossOrigin(origins = "http://localhost:8181",maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/grupo")
public class GrupoRestController {

	@Autowired
	private GrupoServiceAPI grupoServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	private static final Logger logger = LogManager.getLogger(GrupoRestController.class);

	@GetMapping(value = "/getAll")
	public List<Grupo> getAll() {
		logger.info("Consulta general de grupos");
		return grupoServiceAPI.getAll();
	}

	@PostMapping(value = "/saveGrupo")
	public ResponseEntity<Grupo> save(@RequestBody Grupo grupo) {
		logger.info("Intentando guardar grupo");

		String accion = "ACTUALIZAR";

		if (grupo.getIdGrupo() == null || grupo.getIdGrupo().trim().isEmpty()) {
			accion = "CREAR";
		}
		
		Grupo obj = grupoServiceAPI.save(grupo);
		logger.info("Grupo guardado correctamente: {}", obj.getIdGrupo());

		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(0); 
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("grupos");
		auditoria.setIdRegistroAfectado(0); 
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Grupo>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Grupo> getGrupoById(@PathVariable String id) throws ResourceNotFoundException {
		Grupo grupo = grupoServiceAPI.get(id);
		if (grupo == null) {
			throw new ResourceNotFoundException("Record not found for <Grupo> " + id);
		}

		return ResponseEntity.ok().body(grupo);
	}

	@DeleteMapping(value = "/deleteGrupo/{id}")
	public ResponseEntity<Grupo> delete(@PathVariable String id) {
		Grupo grupo = grupoServiceAPI.get(id);
		
		if (grupo != null) {
			
			grupo.setEstado("I");
			grupoServiceAPI.save(grupo);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(0); 
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("grupos");
			auditoria.setIdRegistroAfectado(0);
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);

			logger.info("Grupo desactivado correctamente {}", id);
		} else {
			return new ResponseEntity<>(grupo, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(grupo, HttpStatus.OK);
	}
}