package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.MarcaRepuestoDAO;
import SPRService.SPRService.DAOs.RepuestoDAO;
import SPRService.SPRService.DAOs.UbicacionDAO;
import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.DTOs.filtros.FiltroRepuestoDTO;
import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Ubicacion;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.services.RepuestoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class RepuestoServImpl implements RepuestoServ {

    private final RepuestoDAO daoRepuesto;
    private final MarcaRepuestoDAO daoMarca;
    private final UbicacionDAO daoUbicacion;

    @Inject
    public RepuestoServImpl(RepuestoDAO daoRepuesto, MarcaRepuestoDAO daoMarca, UbicacionDAO daoUbicacion) {
        this.daoRepuesto = daoRepuesto;
        this.daoMarca = daoMarca;
        this.daoUbicacion = daoUbicacion;
    }

    @Transactional
    @Override
    public List<Repuesto> verTodos() {
        return daoRepuesto.verTodos();
    }

    @Transactional
    @Override
    public Long contarStockBajo() {
        return daoRepuesto.cuentaRespBajoStock();
    }

    @Transactional
    @Override
    public List<Repuesto> buscarRepuestos(FiltroRepuestoDTO filtro) {
        return daoRepuesto.buscarRepuestos(filtro);
    }

    @Transactional
    @Override
    public List<RepuestoRetiradoReporteDTO> repuestosMasRetiradosParaVenta(Integer cantidad, LocalDate fechaMin,
                                                                           LocalDate fechaMax) {
        if (cantidad == null || cantidad < 0 || cantidad > 30) {
            cantidad = 5;
        }
        if (fechaMin == null) {
            fechaMin = LocalDate.now().minusYears(20L);
        }
        if (fechaMax == null) {
            fechaMax = LocalDate.now();
        }
        Integer finalCantidad = cantidad;
        LocalDate finalFechaMin = fechaMin;
        LocalDate finalFechaMax = fechaMax;
        List<Object[]> objects = daoRepuesto.masRetiradosParaVenta(finalCantidad, finalFechaMin, finalFechaMax);
        List<RepuestoRetiradoReporteDTO> dtos = new ArrayList<>();
        for (Object[] fila : objects) {
            Repuesto repuesto = (Repuesto) fila[0];
            Long x = (Long) fila[1];
            RepuestoRetiradoReporteDTO dto = new RepuestoRetiradoReporteDTO(
                    repuesto.getCodBarra(),
                    repuesto.getMarcaRepuesto().getNombreMarca(),
                    repuesto.getDetalle(),
                    x
            );
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    @Override
    public ReporteUsoDeRepuestosDTO usoDeRepuestos(LocalDate fechaMin, LocalDate fechaMax) {
        if (fechaMin == null) fechaMin = LocalDate.of(1900, 1, 1);
        if (fechaMax == null) fechaMax = LocalDate.now();
        ReporteUsoDeRepuestosDTO dto = daoRepuesto.usoDeRepuestos(fechaMin, fechaMax);
        if (dto.paraService() == null || dto.paraVenta() == null) {
            return null;
        }
        return dto;
    }

    @Transactional
    @Override
    public Optional<Repuesto> cargarRepuesto(Repuesto repuesto) {
        if (repuesto == null || repuesto.getStock() == null)
            throw new NullPointerException("Error: el repuesto o el stock es nulo.");
        try {
            verificarUnicidadCodBarras(repuesto);
            MarcaRepuesto marcaAttached = daoMarca.update(repuesto.getMarcaRepuesto());
            repuesto.vincularRepuestoYMarca(marcaAttached);

            Ubicacion ubicacionAttached = daoUbicacion.update(repuesto.getStock().getUbicacion());
            repuesto.getStock().asociarUbicacion(ubicacionAttached);

            daoRepuesto.save(repuesto);
        } catch (DuplicateProductException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(repuesto);
    }

    @Transactional
    private void verificarUnicidadCodBarras(Repuesto r) throws DuplicateProductException {
        long idParaGuardar = (r.getId() != null) ? r.getId() : 0;
        List<Repuesto> results = daoRepuesto.validarUnicidadCodBarras(r);
        // Si el id es el mismo se encontró a sí mismo
        if (!results.isEmpty() && !Objects.equals(idParaGuardar, results.getFirst().getId())) {
            if (Objects.equals(r.getCodBarra(), results.getFirst().getCodBarra()))
                throw new DuplicateProductException("Ya existe un repuesto con ese código de barras: " + r.getCodBarra());
        }
    }

    @Transactional
    @Override
    public Optional<Repuesto> modificarRepuesto(Repuesto repuesto) {
        try {
            verificarUnicidadCodBarras(repuesto);
            return Optional.ofNullable(daoRepuesto.update(repuesto));
        } catch (DuplicateProductException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public void borrarRepuesto(Repuesto r) {
        if (r == null || r.getStock() == null) {
            throw new NullPointerException("El repuesto a borrar o su stock son nulo.");
        }
        r.setActivo(Boolean.FALSE);
        r.getStock().setActivo(Boolean.FALSE);
        r.setCodBarra(r.getCodBarra() + ".DEL" + r.getId());
        daoRepuesto.update(r);
    }
}
