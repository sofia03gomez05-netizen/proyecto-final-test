package co.edu.unbosque.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.*;

import co.edu.unbosque.entity.*;
import co.edu.unbosque.service.api.*;
import co.edu.unbosque.utils.TablaPosiciones;

@Service
public class ReporteServiceImpl implements ReporteServiceAPI {

	private static final DeviceRgb FIFA_BLUE = new DeviceRgb(51, 102, 204);
	private static final DeviceRgb GOLD = new DeviceRgb(218, 165, 32);

	@Autowired
	private PartidoServiceAPI partidoServiceAPI;
	@Autowired
	private EquipoServiceAPI equipoServiceAPI;

	@Override
	public byte[] generarReportePartidosExcel() throws IOException {
		List<Partido> partidos = partidoServiceAPI.getAll();
		List<TablaPosiciones> tabla = partidoServiceAPI.obtenerTablaPosiciones("A");
		Map<Integer, String> equipos = cargarMapaEquipos();

		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Font font = workbook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			font.setBold(true);
			headerStyle.setFont(font);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);

			XSSFSheet sheet1 = workbook.createSheet("Cronograma Oficial");
			crearFila(sheet1, 0, new String[] { "ID", "Fase", "Local", "Goles L", "Goles V", "Visitante", "Resultado" },
					headerStyle);

			int rowIdx = 1;
			for (Partido p : partidos) {
				Row row = sheet1.createRow(rowIdx++);
				row.createCell(0).setCellValue(p.getIdPartido());
				row.createCell(1).setCellValue(p.getFase());
				row.createCell(2).setCellValue(equipos.getOrDefault(p.getIdEquipoLocal(), "-"));
				row.createCell(3).setCellValue(p.getGolesLocal() != null ? p.getGolesLocal() : 0);
				row.createCell(4).setCellValue(p.getGolesVisitante() != null ? p.getGolesVisitante() : 0);
				row.createCell(5).setCellValue(equipos.getOrDefault(p.getIdEquipoVisitante(), "-"));
				row.createCell(6).setCellValue(calcularResultado(p));
			}

			XSSFSheet sheet2 = workbook.createSheet("Tabla de Posiciones");
			crearFila(sheet2, 0, new String[] { "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "Puntos" },
					headerStyle);

			int rowIdx2 = 1;
			for (TablaPosiciones t : tabla) {
				Row row = sheet2.createRow(rowIdx2++);
				row.createCell(0).setCellValue(t.getNombreEquipo());
				row.createCell(1).setCellValue(t.getPartidosJugados());
				row.createCell(2).setCellValue(t.getPartidosGanados());
				row.createCell(3).setCellValue(t.getPartidosEmpatados());
				row.createCell(4).setCellValue(t.getPartidosPerdidos());
				row.createCell(5).setCellValue(t.getGolesAFavor());
				row.createCell(6).setCellValue(t.getGolesEnContra());
				row.createCell(7).setCellValue(t.getDiferenciaGoles());
				row.createCell(8).setCellValue(t.getPuntos());
			}

			workbook.write(out);
			return out.toByteArray();
		}
	}

	@Override
	public byte[] generarReportePartidosPDF() throws IOException {
		List<Partido> partidos = partidoServiceAPI.getAll();
		List<TablaPosiciones> tabla = partidoServiceAPI.obtenerTablaPosiciones("A");
		Map<Integer, String> equipos = cargarMapaEquipos();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Document doc = new Document(new PdfDocument(new PdfWriter(out)));

			doc.add(new Paragraph("INFORME OFICIAL DE RESULTADOS").setFontSize(24).setBold().setFontColor(FIFA_BLUE)
					.setTextAlignment(TextAlignment.CENTER));
			doc.add(new Paragraph("Competición: Fase de Grupos - Mundial 2026").setTextAlignment(TextAlignment.CENTER)
					.setItalic());
			doc.add(new Paragraph("\n"));

			Table t1 = new Table(UnitValue.createPercentArray(new float[] { 1, 2, 2, 2, 2, 2 }))
					.setWidth(UnitValue.createPercentValue(100));
			addTableHeader(t1, new String[] { "Fase", "Local", "GL", "GV", "Visitante", "Resultado" });

			for (Partido p : partidos) {
				t1.addCell(p.getFase()).addCell(equipos.get(p.getIdEquipoLocal()))
						.addCell(String.valueOf(p.getGolesLocal())).addCell(String.valueOf(p.getGolesVisitante()))
						.addCell(equipos.get(p.getIdEquipoVisitante())).addCell(calcularResultado(p));
			}
			doc.add(t1);

			doc.add(new AreaBreak());
			doc.add(new Paragraph("ESTADÍSTICAS DEL TORNEO").setFontSize(18).setBold().setFontColor(FIFA_BLUE));

			Table t2 = new Table(UnitValue.createPercentArray(new float[] { 3, 1, 1, 1, 1, 1, 1, 1, 1 }))
					.setWidth(UnitValue.createPercentValue(100));
			addTableHeader(t2, new String[] { "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "Pts" });

			for (TablaPosiciones t : tabla) {
				t2.addCell(t.getNombreEquipo()).addCell(String.valueOf(t.getPartidosJugados()))
						.addCell(String.valueOf(t.getPartidosGanados()))
						.addCell(String.valueOf(t.getPartidosEmpatados()))
						.addCell(String.valueOf(t.getPartidosPerdidos())).addCell(String.valueOf(t.getGolesAFavor()))
						.addCell(String.valueOf(t.getGolesEnContra())).addCell(String.valueOf(t.getDiferenciaGoles()))
						.addCell(String.valueOf(t.getPuntos()));
			}
			doc.add(t2);
			doc.close();
			return out.toByteArray();
		}
	}

	private void addTableHeader(Table table, String[] headers) {
		for (String h : headers) {
			table.addHeaderCell(new com.itextpdf.layout.element.Cell()
					.add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(FIFA_BLUE)
					.setTextAlignment(TextAlignment.CENTER));
		}
	}

	private void crearFila(Sheet sheet, int rowNum, String[] values, CellStyle style) {
		Row row = sheet.createRow(rowNum);
		for (int i = 0; i < values.length; i++) {
			org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
			cell.setCellValue(values[i]);
			cell.setCellStyle(style);
		}
	}

	private Map<Integer, String> cargarMapaEquipos() {
		Map<Integer, String> map = new HashMap<>();
		equipoServiceAPI.getAll().forEach(e -> map.put(e.getIdEquipo(), e.getNombre()));
		return map;
	}

	private String calcularResultado(Partido p) {
	    if (p.getGolesLocal() == null || p.getGolesVisitante() == null) {
	        return "Pendiente";
	    }
	    
	    if (p.getGolesLocal() == 0 && p.getGolesVisitante() == 0) {
	        return "Pendiente"; 
	    }

	    if (p.getGolesLocal() > p.getGolesVisitante()) return "Gana Local";
	    if (p.getGolesVisitante() > p.getGolesLocal()) return "Gana Vis";
	    
	    return "Empate";
	}
}
