package co.edu.unbosque.repository;

import co.edu.unbosque.entity.Partido;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidoRepository extends CrudRepository<Partido, Long> {
}