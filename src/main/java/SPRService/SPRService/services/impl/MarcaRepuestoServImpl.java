package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.MarcaRepuestoDAO;
import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.services.MarcaRepuestoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import jakarta.persistence.PersistenceException;

import java.util.Set;

@Singleton
public class MarcaRepuestoServImpl implements MarcaRepuestoServ {

    private final MarcaRepuestoDAO dao;

    @Inject
    public MarcaRepuestoServImpl(MarcaRepuestoDAO dao) {
        this.dao = dao;
    }

    @Transactional
    @Override
    public Set<MarcaRepuesto> verTodas() {
        return dao.verTodas();
    }

    @Transactional
    @Override
    public MarcaRepuesto cargarMarca(MarcaRepuesto m) {
        if (m == null)
            throw new NullPointerException("Error: la marca de repuesto en el servicio es nula.");
        try {
            dao.save(m);
        } catch (PersistenceException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                    e.getCause() instanceof org.postgresql.util.PSQLException) {
                throw new IllegalArgumentException("Ya existe una marca con el nombre: "
                        + m.getNombreMarca() + " en el sistema.");
            } else {
                throw e;
            }
        }
        return m;
    }
}
