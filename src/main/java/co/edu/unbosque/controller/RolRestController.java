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

import co.edu.unbosque.entity.Rol;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.RolServiceAPI;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@CrossOrigin(origins = "http://localhost:8181",maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/rol")
public class RolRestController {

	@Autowired
	private RolServiceAPI rolServiceAPI;

	@Autowired
	private AuditoriaServiceAPI auditoriaServiceAPI;

	private static final Logger logger = LogManager.getLogger(RolRestController.class);

	@GetMapping(value = "/getAll")
	public List<Rol> getAll() {
		logger.info("Consulta general de roles");
		return rolServiceAPI.getAll();
	}

	@PostMapping(value = "/saveRol")
	public ResponseEntity<Rol> save(@RequestBody Rol rol) {
		logger.info("Intentando guardar rol");

		String accion = "ACTUALIZAR";

		if (rol.getIdRol() == null || rol.getIdRol() == 0L) {
			accion = "CREAR";
		}
		
		Rol obj = rolServiceAPI.save(rol);
		logger.info("Rol guardado correctamente");

		// Registramos en auditoria clonando la base exacta de Usuario (.intValue())
		Auditoria auditoria = new Auditoria();
		auditoria.setIdUsuario(obj.getIdRol().intValue()); 
		auditoria.setAccionAudtria(accion);
		auditoria.setTablaAfectada("roles");
		auditoria.setIdRegistroAfectado(obj.getIdRol().intValue());
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Rol>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/findRecord/{id}")
	public ResponseEntity<Rol> getRolById(@PathVariable Long id) throws ResourceNotFoundException {
		Rol rol = rolServiceAPI.get(id);
		if (rol == null) {
			throw new ResourceNotFoundException("Record not found for <Rol>" + id);
		}

		return ResponseEntity.ok().body(rol);
	}

	@DeleteMapping(value = "/deleteRol/{id}")
	public ResponseEntity<Rol> delete(@PathVariable Long id) {
		Rol rol = rolServiceAPI.get(id);
		
		if (rol != null) {
			
			rol.setEstado("I");
			rolServiceAPI.save(rol);

			Auditoria auditoria = new Auditoria();
			auditoria.setIdUsuario(id.intValue()); 
			auditoria.setAccionAudtria("ELIMINAR");
			auditoria.setTablaAfectada("roles");
			auditoria.setIdRegistroAfectado(id.intValue());
			auditoria.setFchaHoraAudtria(LocalDateTime.now());
			auditoriaServiceAPI.save(auditoria);

			logger.info("Rol desactivado correctamente {}", id);
		} else {
			return new ResponseEntity<>(rol, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(rol, HttpStatus.OK);
	}
}