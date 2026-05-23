package co.edu.unbosque.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import co.edu.unbosque.entity.Equipo;
import co.edu.unbosque.entity.Partido;
import co.edu.unbosque.repository.EquipoRepository;
import co.edu.unbosque.repository.PartidoRepository;
import co.edu.unbosque.service.api.PartidoServiceAPI;
import co.edu.unbosque.utils.*;

@Service
public class PartidoServiceImpl extends GenericServiceImpl<Partido, Long> implements PartidoServiceAPI {
	
	@Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    public CrudRepository<Partido, Long> getDao() {
        return partidoRepository;
    }
    
    @Override
    public List<Partido> generarCronogramaFaseGrupos() {
    	if (partidoRepository.count() > 0) {
            return (List<Partido>) partidoRepository.findAll(); 
        }

        if (equipoRepository.count() == 0) {
            return new ArrayList<Partido>(); 
        }

        List<Partido> partidosAgendados = new ArrayList<>(); 
        
        String[] deGrupos = {"A", "B", "C", "D", "E", "F", "G", "H"};
        
        LocalDateTime fechaBase = LocalDateTime.of(2026, 6, 10, 14, 0); 

        for (String grupo : deGrupos) {
            
            List<Equipo> grupoEquipos = equipoRepository.findByIdGrupo(grupo);
            
            if (grupoEquipos.size() == 4) {
                Equipo e1 = grupoEquipos.get(0);
                Equipo e2 = grupoEquipos.get(1);
                Equipo e3 = grupoEquipos.get(2);
                Equipo e4 = grupoEquipos.get(3);

                partidosAgendados.add(crearPartidoInstancia(e1, e2, "GRUPO " + grupo, fechaBase));
                partidosAgendados.add(crearPartidoInstancia(e3, e4, "GRUPO " + grupo, fechaBase.plusHours(3)));

                partidosAgendados.add(crearPartidoInstancia(e1, e3, "GRUPO " + grupo, fechaBase.plusDays(4)));
                partidosAgendados.add(crearPartidoInstancia(e2, e4, "GRUPO " + grupo, fechaBase.plusDays(4).plusHours(3)));

                partidosAgendados.add(crearPartidoInstancia(e1, e4, "GRUPO " + grupo, fechaBase.plusDays(8)));
                partidosAgendados.add(crearPartidoInstancia(e2, e3, "GRUPO " + grupo, fechaBase.plusDays(8).plusHours(3)));
            }
            
            fechaBase = fechaBase.plusDays(2);
        }

        return (List<Partido>) partidoRepository.saveAll(partidosAgendados);
    }

    private Partido crearPartidoInstancia(Equipo local, Equipo visitante, String jornada, LocalDateTime fecha) {
        Partido partido = new Partido();
        partido.setIdEquipoLocal(local.getIdEquipo());
        partido.setIdEquipoVisitante(visitante.getIdEquipo());
        partido.setFase("GRUPO " + local.getIdGrupo());
        partido.setFechaHora(fecha);
        partido.setGolesLocal(0);      
        partido.setGolesVisitante(0);  
        partido.setEstado("A");  
        return partido;
    }
    
    @Override
    public Partido registrarResultadoPartidos(Long idPartido, int golesLocal, int golesVisitante) {
        Optional<Partido> partidoOpt = partidoRepository.findById(idPartido);
        
        if (partidoOpt.isPresent()) {
            Partido partido = partidoOpt.get();
            partido.setGolesLocal(golesLocal);
            partido.setGolesVisitante(golesVisitante);
            partido.setEstado("I"); // <-- CAMBIADO A "I" PARA QUE COINCIDA
            return partidoRepository.save(partido);
        }
        return null;
    }

    @Override
    public List<TablaPosiciones> obtenerTablaPosiciones(String grupo) {
        List<TablaPosiciones> tabla = new java.util.ArrayList<>();
        
        List<Equipo> todosLosEquipos = (List<Equipo>) equipoRepository.findAll();
        List<Partido> todosLosPartidos = (List<Partido>) partidoRepository.findAll();
        
        String faseBuscada = "GRUPO " + grupo; 
        
        for (Equipo equipo : todosLosEquipos) {
            if (equipo.getIdGrupo() != null && ("GRUPO " + equipo.getIdGrupo()).equals(faseBuscada)) {
                
                TablaPosiciones fila = new TablaPosiciones(equipo.getIdEquipo(), equipo.getNombre(), equipo.getIdGrupo());
                
                for (Partido partido : todosLosPartidos) {
                    if ("I".equals(partido.getEstado()) && faseBuscada.equals(partido.getFase())) {
                        if (partido.getIdEquipoLocal().equals(equipo.getIdEquipo()) || 
                            partido.getIdEquipoVisitante().equals(equipo.getIdEquipo())) {
                            
                            boolean esLocal = partido.getIdEquipoLocal().equals(equipo.getIdEquipo());
                            fila.procesarResultado(partido, esLocal);
                        }
                    }
                }
                tabla.add(fila);
            }
        }
        return tabla;
    }
}

  