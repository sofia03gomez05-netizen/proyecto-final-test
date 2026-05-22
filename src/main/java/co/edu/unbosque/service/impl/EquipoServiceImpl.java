package co.edu.unbosque.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import co.edu.unbosque.entity.Equipo;
import co.edu.unbosque.repository.EquipoRepository;
import co.edu.unbosque.service.api.EquipoServiceAPI;
import co.edu.unbosque.utils.GenericServiceImpl;

@Service
public class EquipoServiceImpl extends GenericServiceImpl<Equipo, Long> implements EquipoServiceAPI {

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    public CrudRepository<Equipo, Long> getDao() {
        return equipoRepository;
    }
}