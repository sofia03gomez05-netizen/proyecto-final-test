package co.edu.unbosque.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import co.edu.unbosque.entity.Estadio;
import co.edu.unbosque.repository.EstadioRepository;
import co.edu.unbosque.service.api.EstadioServiceAPI;
import co.edu.unbosque.utils.GenericServiceImpl;

@Service
public class EstadioServiceImpl extends GenericServiceImpl<Estadio, Long> implements EstadioServiceAPI {

    @Autowired
    private EstadioRepository estadioRepository;

    @Override
    public CrudRepository<Estadio, Long> getDao() {
        return estadioRepository;
    }
}