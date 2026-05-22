package co.edu.unbosque.service.api;

import java.io.IOException;

/**
 * Servicio para generar reportes de los partidos del torneo.
 * Genera dos formatos:
 *  - Excel (.xlsx) usando Apache POI
 *  - PDF usando iText 7
 */
public interface ReporteServiceAPI {

	/**
	 * Genera el reporte de los resultados de los partidos en formato Excel (.xlsx).
	 *
	 * @return arreglo de bytes con el contenido del archivo Excel
	 * @throws IOException si ocurre un error al escribir el archivo
	 */
	byte[] generarReportePartidosExcel() throws IOException;

	/**
	 * Genera el reporte de los resultados de los partidos en formato PDF.
	 *
	 * @return arreglo de bytes con el contenido del archivo PDF
	 * @throws IOException si ocurre un error al escribir el archivo
	 */
	byte[] generarReportePartidosPDF() throws IOException;
}
