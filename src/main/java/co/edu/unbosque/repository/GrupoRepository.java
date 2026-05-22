package co.edu.unbosque.repository;

import co.edu.unbosque.entity.Grupo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends CrudRepository<Grupo, String> {
}