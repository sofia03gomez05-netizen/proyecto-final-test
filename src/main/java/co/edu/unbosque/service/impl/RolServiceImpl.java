package co.edu.unbosque.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import co.edu.unbosque.entity.Rol;
import co.edu.unbosque.repository.RolRepository;
import co.edu.unbosque.service.api.RolServiceAPI;
import co.edu.unbosque.utils.GenericServiceImpl;

@Service
public class RolServiceImpl extends GenericServiceImpl<Rol, Long> implements RolServiceAPI {

    @Autowired
    private RolRepository rolRepository;       

    @Override
    public CrudRepository<Rol, Long> getDao() {
        return rolRepository;
    }
}