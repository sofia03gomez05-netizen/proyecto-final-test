package co.edu.unbosque.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import co.edu.unbosque.entity.Grupo;
import co.edu.unbosque.repository.GrupoRepository;
import co.edu.unbosque.service.api.GrupoServiceAPI;
import co.edu.unbosque.utils.GenericServiceImpl;

@Service
public class GrupoServiceImpl extends GenericServiceImpl<Grupo, String> implements GrupoServiceAPI {

    @Autowired
    private GrupoRepository grupoRepository;

    @Override
    public CrudRepository<Grupo, String> getDao() {
        return grupoRepository;
    }
}