package co.edu.unbosque.service.api;

import java.util.List;
import co.edu.unbosque.entity.Partido;
import co.edu.unbosque.utils.GenericServiceAPI;
import co.edu.unbosque.utils.TablaPosiciones;

public interface PartidoServiceAPI extends GenericServiceAPI<Partido, Long> {
	
	List<Partido> generarCronogramaFaseGrupos();
	
	Partido registrarResultadoPartidos(Long idPartido, int golesLocal, int golesVisitante);

	List<TablaPosiciones> obtenerTablaPosiciones(String grupo);
}