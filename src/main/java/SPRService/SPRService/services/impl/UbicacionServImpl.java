package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.UbicacionDAO;
import SPRService.SPRService.entities.Ubicacion;
import SPRService.SPRService.services.UbicacionServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class UbicacionServImpl implements UbicacionServ {

    private final UbicacionDAO ubicacionDAO;

    @Inject
    public UbicacionServImpl(UbicacionDAO ubicacionDAO) {
        this.ubicacionDAO = ubicacionDAO;
    }

    @Transactional
    @Override
    public List<Ubicacion> verTodas() {
        return ubicacionDAO.verTodas();
    }

    @Transactional
    @Override
    public Optional<Ubicacion> cargarNueva(Ubicacion u) {
        try {
            validarUnicidadNombre(u);
            ubicacionDAO.save(u);
            return Optional.of(u);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Ha ocurrido un error inesperado", e);
        }
    }

    @Transactional
    @Override
    public Optional<Ubicacion> modificar(Ubicacion u) {
        return Optional.empty();
    }

    @Transactional
    private void validarUnicidadNombre(Ubicacion u) {
        long idParaGuardar = (u.getId() != null) ? u.getId() : 0;
        List<Ubicacion> results = ubicacionDAO.validarUnicidadNombre(u);
        if (!results.isEmpty() && !Objects.equals(idParaGuardar, results.getFirst().getId())) {
            if (Objects.equals(u.getUbicacion(), results.getFirst().getUbicacion()))
                throw new IllegalArgumentException("Ya existe una ubicación con el nombre: " + u.getUbicacion());
        }
    }
}
