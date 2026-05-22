package co.edu.unbosque.repository;

import co.edu.unbosque.entity.Estadio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadioRepository extends CrudRepository<Estadio, Long> {
}