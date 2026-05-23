package co.edu.unbosque.service.api;

import java.io.IOException;

public interface ReporteServiceAPI {

	byte[] generarReportePartidosExcel() throws IOException;
    byte[] generarReportePartidosPDF() throws IOException;
    
}
