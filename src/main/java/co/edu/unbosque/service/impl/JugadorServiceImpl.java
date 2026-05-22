package co.edu.unbosque.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import co.edu.unbosque.entity.Jugador;
import co.edu.unbosque.repository.JugadorRepository;
import co.edu.unbosque.service.api.JugadorServiceAPI;
import co.edu.unbosque.utils.GenericServiceImpl;

@Service
public class JugadorServiceImpl extends GenericServiceImpl<Jugador, Long> implements JugadorServiceAPI {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Override
    public CrudRepository<Jugador, Long> getDao() {
        return jugadorRepository;
    }
}