package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.MarcaRepuestoDAO;
import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.services.MarcaRepuestoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    public Optional<MarcaRepuesto> cargarMarca(MarcaRepuesto m) {
        if (m == null)
            throw new NullPointerException("Error: la marca de repuesto en el servicio es nula.");
        try {
            validarUnicidadNombre(m);
            dao.save(m);
            return Optional.of(m);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error inesperado al guardar la marca", e);
        }
    }

    private void validarUnicidadNombre(MarcaRepuesto m) {
        long idParaGuardar = (m.getId() != null) ? m.getId() : 0;
        List<MarcaRepuesto> results = dao.validarUnicidadNombre(m);
        if (!results.isEmpty() && !Objects.equals(idParaGuardar, results.getFirst().getId())) {
            if (Objects.equals(m.getNombreMarca(), results.getFirst().getNombreMarca()))
                throw new IllegalArgumentException("Ya existe una marca con el nombre: " + m.getNombreMarca());
        }
    }
}
