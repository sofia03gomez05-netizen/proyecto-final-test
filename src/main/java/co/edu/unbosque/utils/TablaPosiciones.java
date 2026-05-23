package co.edu.unbosque.utils;

import lombok.Data;
import co.edu.unbosque.entity.Partido;

@Data
public class TablaPosiciones {
    private Integer idEquipo;
    private String nombreEquipo;
    private String grupo;
    
    private int partidosJugados;
    private int partidosGanados;
    private int partidosEmpatados;
    private int partidosPerdidos;
    
    private int golesAFavor;
    private int golesEnContra;
    private int diferenciaGoles;
    private int puntos;

    public TablaPosiciones(Integer idEquipo, String nombreEquipo, String grupo) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.grupo = grupo;
        this.partidosJugados = 0;
        this.partidosGanados = 0;
        this.partidosEmpatados = 0;
        this.partidosPerdidos = 0;
        this.golesAFavor = 0;
        this.golesEnContra = 0;
        this.diferenciaGoles = 0;
        this.puntos = 0;
    }
    
    

    public Integer getIdEquipo() {
		return idEquipo;
	}



	public void setIdEquipo(Integer idEquipo) {
		this.idEquipo = idEquipo;
	}



	public String getNombreEquipo() {
		return nombreEquipo;
	}



	public void setNombreEquipo(String nombreEquipo) {
		this.nombreEquipo = nombreEquipo;
	}



	public String getGrupo() {
		return grupo;
	}



	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}



	public int getPartidosJugados() {
		return partidosJugados;
	}



	public void setPartidosJugados(int partidosJugados) {
		this.partidosJugados = partidosJugados;
	}



	public int getPartidosGanados() {
		return partidosGanados;
	}



	public void setPartidosGanados(int partidosGanados) {
		this.partidosGanados = partidosGanados;
	}



	public int getPartidosEmpatados() {
		return partidosEmpatados;
	}



	public void setPartidosEmpatados(int partidosEmpatados) {
		this.partidosEmpatados = partidosEmpatados;
	}



	public int getPartidosPerdidos() {
		return partidosPerdidos;
	}



	public void setPartidosPerdidos(int partidosPerdidos) {
		this.partidosPerdidos = partidosPerdidos;
	}



	public int getGolesAFavor() {
		return golesAFavor;
	}



	public void setGolesAFavor(int golesAFavor) {
		this.golesAFavor = golesAFavor;
	}



	public int getGolesEnContra() {
		return golesEnContra;
	}



	public void setGolesEnContra(int golesEnContra) {
		this.golesEnContra = golesEnContra;
	}



	public int getDiferenciaGoles() {
		return diferenciaGoles;
	}



	public void setDiferenciaGoles(int diferenciaGoles) {
		this.diferenciaGoles = diferenciaGoles;
	}



	public int getPuntos() {
		return puntos;
	}



	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}



	public void procesarResultado(Partido partido, boolean esLocal) {
        int golesAFavorPartido;
        int golesEnContraPartido;

        if (esLocal) {
            golesAFavorPartido = partido.getGolesLocal();
            golesEnContraPartido = partido.getGolesVisitante();
        } else {
            golesAFavorPartido = partido.getGolesVisitante();
            golesEnContraPartido = partido.getGolesLocal();
        }
        
        this.partidosJugados++;
        this.golesAFavor += golesAFavorPartido;
        this.golesEnContra += golesEnContraPartido;
        
        this.diferenciaGoles = this.golesAFavor - this.golesEnContra;
        
        if (golesAFavorPartido > golesEnContraPartido) {
            this.partidosGanados++;
            this.puntos += 3;
        } else if (golesAFavorPartido == golesEnContraPartido) {
            this.partidosEmpatados++;
            this.puntos += 1;
        } else {
            this.partidosPerdidos++;
        }
    }
    
    
}