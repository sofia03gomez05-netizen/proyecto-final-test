package co.edu.unbosque.controller;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import co.edu.unbosque.service.api.ReporteServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import co.edu.unbosque.utils.TablaPosiciones;
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

	@Autowired
	private ReporteServiceAPI reporteServiceAPI;

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

	@GetMapping(value = "/reporte/excel")
	public ResponseEntity<byte[]> descargarReporteExcel() {
		try {
			logger.info("Generando reporte completo en Excel");
			byte[] contenido = reporteServiceAPI.generarReportePartidosExcel();

			String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String nombreArchivo = "Reporte_General_Torneo_" + fecha + ".xlsx";

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(contenido);
		} catch (Exception e) {
			logger.error("Error al generar reporte Excel", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/reporte/pdf")
	public ResponseEntity<byte[]> descargarReportePDF() {
		try {
			logger.info("Generando reporte completo en PDF");
			byte[] contenido = reporteServiceAPI.generarReportePartidosPDF();

			String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String nombreArchivo = "Reporte_General_Torneo_" + fecha + ".pdf";

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
					.contentType(MediaType.APPLICATION_PDF).body(contenido);
		} catch (Exception e) {
			logger.error("Error al generar reporte PDF", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/generar-cronograma-fase-grupos")
	public ResponseEntity<List<Partido>> generarCronograma() {
		try {
			logger.info("Iniciando la generación automática del cronograma de partidos");

			List<Partido> partidosCreados = partidoServiceAPI.generarCronogramaFaseGrupos();

			if (partidosCreados.isEmpty()) {
				logger.warn("No se pudo generar el cronograma: No hay equipos registrados.");
				return new ResponseEntity<>(partidosCreados, HttpStatus.NO_CONTENT); // HTTP 204
			}

			if (partidosCreados.get(0).getIdPartido() > 0L) {
				logger.info("El cronograma ya existía previamente. Retornando datos guardados.");
				return new ResponseEntity<>(partidosCreados, HttpStatus.OK); // HTTP 200
			}

			logger.info("Cronograma de la Fase de Grupos generado y guardado con éxito.");
			return new ResponseEntity<>(partidosCreados, HttpStatus.CREATED); // HTTP 201

		} catch (Exception e) {
			logger.error("Error inesperado al generar el cronograma", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/registrarResultado")
	public ResponseEntity<Partido> registrarResultado(@RequestBody Partido partidoDatos) {
		logger.info("Intentando registrar resultado para el partido ID: {}", partidoDatos.getIdPartido());

		Partido partidoExistente = partidoServiceAPI.get(partidoDatos.getIdPartido());

		if (partidoExistente == null) {
			logger.warn("No se encontró el partido con ID: {}", partidoDatos.getIdPartido());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // HTTP 404
		}

		partidoExistente.setGolesLocal(partidoDatos.getGolesLocal());
		partidoExistente.setGolesVisitante(partidoDatos.getGolesVisitante());
		partidoExistente.setEstado("I"); // "F" de Finalizado

		Partido objActualizado = partidoServiceAPI.save(partidoExistente);
		logger.info("Resultado registrado correctamente");

		Auditoria auditoria = new Auditoria();
		// Aqui se pondra el id del usuario que registrara los resultados.
		auditoria.setIdUsuario(2);
		auditoria.setAccionAudtria("REGISTRAR RESULTADO");
		auditoria.setTablaAfectada("partidos");
		auditoria.setIdRegistroAfectado(objActualizado.getIdPartido().intValue());
		auditoria.setFchaHoraAudtria(LocalDateTime.now());
		auditoriaServiceAPI.save(auditoria);

		return new ResponseEntity<Partido>(objActualizado, HttpStatus.OK);
	}

	@GetMapping(value = "/tabla-posiciones/{grupo}")
	public ResponseEntity<List<TablaPosiciones>> obtenerTablaPosiciones(@PathVariable String grupo) {
		try {
			List<TablaPosiciones> tabla = partidoServiceAPI.obtenerTablaPosiciones(grupo.toUpperCase());

			return new ResponseEntity<>(tabla, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}