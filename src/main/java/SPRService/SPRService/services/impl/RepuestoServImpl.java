package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.MarcaRepuestoDAO;
import SPRService.SPRService.DAOs.RepuestoDAO;
import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.services.RepuestoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RepuestoServImpl implements RepuestoServ {

    private final RepuestoDAO daoRepuesto;
    private final MarcaRepuestoDAO daoMarca;

    @Inject
    public RepuestoServImpl(RepuestoDAO daoRepuesto, MarcaRepuestoDAO daoMarca) {
        this.daoRepuesto = daoRepuesto;
        this.daoMarca = daoMarca;
    }

    @Transactional
    @Override
    public List<Repuesto> verTodos() {
        return daoRepuesto.todosProductosActivos();
    }

    @Transactional
    @Override
    public Long contarStockBajo() {
        return daoRepuesto.cuentaRespBajoStock();
    }

    @Transactional
    @Override
    public List<Repuesto> buscarConCriteria(String codBarras, String nombreProd, String marcaProd,
                                            Boolean verStockNormal, Boolean verStockBajo,
                                            String colParaOrdenar, Integer tipoOrden) {
        if (codBarras == null) codBarras = "";
        if (nombreProd == null) nombreProd = "";
        if (marcaProd == null) marcaProd = "";

        return daoRepuesto.buscarConCriteria(codBarras, nombreProd, marcaProd, verStockNormal, verStockBajo,
                colParaOrdenar, tipoOrden);
    }

    @Transactional
    @Override
    public List<RepuestoRetiradoReporteDTO> repuestosMasRetiradosParaVenta(Integer cantidad, LocalDate fechaMin,
                                                                           LocalDate fechaMax) {
        if (cantidad == null || cantidad < 0 || cantidad > 13) {
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
    public Repuesto cargarRepuesto(Repuesto repuesto) {
        if (repuesto == null || repuesto.getStock() == null)
            throw new NullPointerException("Error: el repuesto o el stock es nulo.");
        try {
            MarcaRepuesto marcaAttached = daoMarca.update(repuesto.getMarcaRepuesto());
            repuesto.vincularRepuestoYMarca(marcaAttached);
            daoRepuesto.save(repuesto);
        } catch (PersistenceException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                    e.getCause() instanceof org.postgresql.util.PSQLException) {
                throw new DuplicateProductException("Ya existe un producto con el código de barras: "
                + repuesto.getCodBarra() + " en el sistema.");
            } else {
                throw e;
            }
        }
        return repuesto;
    }

    @Transactional
    @Override
    public Repuesto modificarRepuesto(Repuesto repuesto) {
        if (repuesto.getStock() == null) throw new NullPointerException("Error: el stock es nulo.");
        return daoRepuesto.update(repuesto);
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
