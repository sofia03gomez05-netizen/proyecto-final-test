package co.edu.unbosque.repository;

import co.edu.unbosque.entity.Equipo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipoRepository extends CrudRepository<Equipo, Long> {
	
	List<Equipo> findByIdGrupo(String idGrupo);
}