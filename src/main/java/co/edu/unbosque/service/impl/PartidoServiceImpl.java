package co.edu.unbosque.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import co.edu.unbosque.entity.Partido;
import co.edu.unbosque.repository.PartidoRepository;
import co.edu.unbosque.service.api.PartidoServiceAPI;
import co.edu.unbosque.utils.GenericServiceImpl;

@Service
public class PartidoServiceImpl extends GenericServiceImpl<Partido, Long> implements PartidoServiceAPI {

    @Autowired
    private PartidoRepository partidoRepository;

    @Override
    public CrudRepository<Partido, Long> getDao() {
        return partidoRepository;
    }
}