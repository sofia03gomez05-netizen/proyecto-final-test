package co.edu.unbosque.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.edu.unbosque.entity.Auditoria;
import co.edu.unbosque.service.api.AuditoriaServiceAPI;
import co.edu.unbosque.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/auditoria")
public class AuditoriaRestController {

    @Autowired
    private AuditoriaServiceAPI auditoriaServiceAPI;

    @GetMapping(value = "/getAll")
    public List<Auditoria> getAll() {
        return auditoriaServiceAPI.getAll();
    }

    @PostMapping(value = "/saveAuditoria")
    public ResponseEntity<Auditoria> save(@RequestBody Auditoria auditoria) {
        Auditoria obj = auditoriaServiceAPI.save(auditoria);
        return new ResponseEntity<Auditoria>(obj, HttpStatus.OK);
    }

    @GetMapping(value = "/findRecord/{id}")
    public ResponseEntity<Auditoria> getAuditoriaById(@PathVariable Long id) throws ResourceNotFoundException {
        Auditoria auditoria = auditoriaServiceAPI.get(id);
        if (auditoria == null) {
            new ResourceNotFoundException("Record not found for <Auditoria> " + id);
        }
        return ResponseEntity.ok().body(auditoria);
    }

    @DeleteMapping(value = "/deleteAuditoria/{id}")
    public ResponseEntity<Auditoria> delete(@PathVariable Long id) {
        Auditoria auditoria = auditoriaServiceAPI.get(id);
        if (auditoria != null) {
            auditoriaServiceAPI.delete(id);
        } else {
            return new ResponseEntity<Auditoria>(auditoria, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Auditoria>(auditoria, HttpStatus.OK);
    }
}