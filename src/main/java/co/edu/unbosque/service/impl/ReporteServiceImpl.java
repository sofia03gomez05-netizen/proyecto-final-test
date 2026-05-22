package co.edu.unbosque.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import co.edu.unbosque.entity.Equipo;
import co.edu.unbosque.entity.Partido;
import co.edu.unbosque.service.api.EquipoServiceAPI;
import co.edu.unbosque.service.api.PartidoServiceAPI;
import co.edu.unbosque.service.api.ReporteServiceAPI;

/**
 * Implementacion del servicio de reportes de partidos.
 *
 * Excel -> Apache POI (XSSFWorkbook)
 * PDF   -> iText 7
 */
@Service
public class ReporteServiceImpl implements ReporteServiceAPI {

	private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Autowired
	private PartidoServiceAPI partidoServiceAPI;

	@Autowired
	private EquipoServiceAPI equipoServiceAPI;

	// ============================================================
	// EXCEL - Apache POI
	// ============================================================
	@Override
	public byte[] generarReportePartidosExcel() throws IOException {
		List<Partido> partidos = partidoServiceAPI.getAll();
		Map<Integer, String> equipos = cargarMapaEquipos();

		try (XSSFWorkbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			XSSFSheet sheet = workbook.createSheet("Resultados Partidos");

			// --- Titulo principal ---
			CellStyle tituloStyle = construirEstiloTitulo(workbook);
			Row tituloRow = sheet.createRow(0);
			tituloRow.setHeightInPoints(28);
			Cell tituloCell = tituloRow.createCell(0);
			tituloCell.setCellValue("REPORTE DE RESULTADOS DE PARTIDOS");
			tituloCell.setCellStyle(tituloStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

			// --- Cabecera de la tabla ---
			String[] cabeceras = { "ID", "Fase", "Equipo Local", "Goles Local",
					"Goles Visitante", "Equipo Visitante", "Fecha y Hora", "Resultado" };
			CellStyle headerStyle = construirEstiloCabecera(workbook);
			Row headerRow = sheet.createRow(2);
			headerRow.setHeightInPoints(20);
			for (int i = 0; i < cabeceras.length; i++) {
				Cell c = headerRow.createCell(i);
				c.setCellValue(cabeceras[i]);
				c.setCellStyle(headerStyle);
			}

			// --- Datos ---
			CellStyle dataStyle = construirEstiloDatos(workbook);
			CellStyle centerStyle = construirEstiloCentrado(workbook);
			int rowIdx = 3;
			for (Partido p : partidos) {
				Row row = sheet.createRow(rowIdx++);

				crearCelda(row, 0, p.getIdPartido() != null ? p.getIdPartido().toString() : "", centerStyle);
				crearCelda(row, 1, p.getFase() != null ? p.getFase() : "-", dataStyle);
				crearCelda(row, 2, equipos.getOrDefault(p.getIdEquipoLocal(), "Equipo " + p.getIdEquipoLocal()), dataStyle);
				crearCelda(row, 3, p.getGolesLocal() != null ? p.getGolesLocal().toString() : "-", centerStyle);
				crearCelda(row, 4, p.getGolesVisitante() != null ? p.getGolesVisitante().toString() : "-", centerStyle);
				crearCelda(row, 5, equipos.getOrDefault(p.getIdEquipoVisitante(), "Equipo " + p.getIdEquipoVisitante()), dataStyle);
				crearCelda(row, 6, p.getFechaHora() != null ? p.getFechaHora().format(FMT_FECHA) : "-", centerStyle);
				crearCelda(row, 7, calcularResultado(p), centerStyle);
			}

			// --- Fila de totales ---
			CellStyle totalStyle = construirEstiloTotales(workbook);
			Row totalRow = sheet.createRow(rowIdx + 1);
			Cell totalLabel = totalRow.createCell(0);
			totalLabel.setCellValue("Total de partidos: " + partidos.size());
			totalLabel.setCellStyle(totalStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowIdx + 1, rowIdx + 1, 0, 7));

			// --- Ajuste de columnas ---
			for (int i = 0; i < cabeceras.length; i++) {
				sheet.autoSizeColumn(i);
				// margen extra
				int currentWidth = sheet.getColumnWidth(i);
				sheet.setColumnWidth(i, currentWidth + 512);
			}

			workbook.write(out);
			return out.toByteArray();
		}
	}

	// ============================================================
	// PDF - iText 7
	// ============================================================
	@Override
	public byte[] generarReportePartidosPDF() throws IOException {
		List<Partido> partidos = partidoServiceAPI.getAll();
		Map<Integer, String> equipos = cargarMapaEquipos();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PdfWriter writer = new PdfWriter(out);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			// --- Titulo ---
			Paragraph titulo = new Paragraph("Reporte de Resultados de Partidos")
					.setBold()
					.setFontSize(18)
					.setTextAlignment(TextAlignment.CENTER)
					.setFontColor(new DeviceRgb(33, 64, 154));
			document.add(titulo);

			Paragraph subtitulo = new Paragraph("Listado general de partidos del torneo")
					.setFontSize(11)
					.setItalic()
					.setTextAlignment(TextAlignment.CENTER)
					.setFontColor(ColorConstants.DARK_GRAY);
			document.add(subtitulo);

			document.add(new Paragraph("\n"));

			// --- Tabla ---
			float[] anchos = { 1f, 2f, 3f, 1.2f, 1.2f, 3f, 2.5f, 2f };
			Table tabla = new Table(UnitValue.createPercentArray(anchos));
			tabla.setWidth(UnitValue.createPercentValue(100));

			String[] cabeceras = { "ID", "Fase", "Equipo Local", "Goles L",
					"Goles V", "Equipo Visitante", "Fecha y Hora", "Resultado" };
			for (String h : cabeceras) {
				tabla.addHeaderCell(
						new com.itextpdf.layout.element.Cell().add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE))
								.setBackgroundColor(new DeviceRgb(33, 64, 154))
								.setTextAlignment(TextAlignment.CENTER)
								.setPadding(5));
			}

			boolean alterna = false;
			DeviceRgb gris = new DeviceRgb(240, 240, 245);
			for (Partido p : partidos) {
				DeviceRgb fondo = alterna ? gris : new DeviceRgb(255, 255, 255);

				agregarCeldaPDF(tabla, p.getIdPartido() != null ? p.getIdPartido().toString() : "", fondo, TextAlignment.CENTER);
				agregarCeldaPDF(tabla, p.getFase() != null ? p.getFase() : "-", fondo, TextAlignment.LEFT);
				agregarCeldaPDF(tabla, equipos.getOrDefault(p.getIdEquipoLocal(), "Equipo " + p.getIdEquipoLocal()), fondo, TextAlignment.LEFT);
				agregarCeldaPDF(tabla, p.getGolesLocal() != null ? p.getGolesLocal().toString() : "-", fondo, TextAlignment.CENTER);
				agregarCeldaPDF(tabla, p.getGolesVisitante() != null ? p.getGolesVisitante().toString() : "-", fondo, TextAlignment.CENTER);
				agregarCeldaPDF(tabla, equipos.getOrDefault(p.getIdEquipoVisitante(), "Equipo " + p.getIdEquipoVisitante()), fondo, TextAlignment.LEFT);
				agregarCeldaPDF(tabla, p.getFechaHora() != null ? p.getFechaHora().format(FMT_FECHA) : "-", fondo, TextAlignment.CENTER);
				agregarCeldaPDF(tabla, calcularResultado(p), fondo, TextAlignment.CENTER);

				alterna = !alterna;
			}

			document.add(tabla);

			// --- Resumen ---
			document.add(new Paragraph("\n"));
			Paragraph resumen = new Paragraph("Total de partidos: " + partidos.size())
					.setBold()
					.setFontSize(11)
					.setTextAlignment(TextAlignment.RIGHT);
			document.add(resumen);

			document.close();
			return out.toByteArray();
		}
	}

	// ============================================================
	// Utilitarios
	// ============================================================
	private Map<Integer, String> cargarMapaEquipos() {
		Map<Integer, String> map = new HashMap<>();
		List<Equipo> equipos = equipoServiceAPI.getAll();
		if (equipos != null) {
			for (Equipo e : equipos) {
				if (e.getIdEquipo() != null) {
					map.put(e.getIdEquipo(), e.getNombre());
				}
			}
		}
		return map;
	}

	private String calcularResultado(Partido p) {
		if (p.getGolesLocal() == null || p.getGolesVisitante() == null) {
			return "Pendiente";
		}
		int gl = p.getGolesLocal();
		int gv = p.getGolesVisitante();
		if (gl > gv) {
			return "Gana Local";
		} else if (gv > gl) {
			return "Gana Visitante";
		} else {
			return "Empate";
		}
	}

	private void crearCelda(Row row, int col, String valor, CellStyle style) {
		Cell cell = row.createCell(col);
		cell.setCellValue(valor);
		cell.setCellStyle(style);
	}

	private void agregarCeldaPDF(Table tabla, String valor, DeviceRgb fondo, TextAlignment alineacion) {
		tabla.addCell(new com.itextpdf.layout.element.Cell()
				.add(new Paragraph(valor != null ? valor : "-").setFontSize(9))
				.setBackgroundColor(fondo)
				.setTextAlignment(alineacion)
				.setPadding(4));
	}

	// --- Estilos Excel ---
	private CellStyle construirEstiloTitulo(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		font.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}

	private CellStyle construirEstiloCabecera(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		aplicarBordes(style);
		return style;
	}

	private CellStyle construirEstiloDatos(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		aplicarBordes(style);
		return style;
	}

	private CellStyle construirEstiloCentrado(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		aplicarBordes(style);
		return style;
	}

	private CellStyle construirEstiloTotales(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.RIGHT);
		return style;
	}

	private void aplicarBordes(CellStyle style) {
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}
}
