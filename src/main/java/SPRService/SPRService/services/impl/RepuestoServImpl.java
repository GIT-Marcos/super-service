package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.RepuestoDAO;
import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
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

    private final RepuestoDAO dao;

    @Inject
    public RepuestoServImpl(RepuestoDAO dao) {
        this.dao = dao;
    }

    @Transactional
    @Override
    public List<Repuesto> verTodos() {
        return dao.allActiveProducts();
    }

    @Transactional
    @Override
    public Long contarStockBajo() {
        return dao.cuentaRespBajoStock();
    }

    /**
     * @param tipoOrden pasar nulo si no importa el orden
     */
    @Transactional
    @Deprecated
    @Override
    public List<Repuesto> buscarConFiltros(String inputParaBuscar, Integer opcionBusqueda,
                                           Boolean stockNormal, Boolean stockBajo, String nombreColumnaOrnenar,
                                           Integer tipoOrden) {
        if (nombreColumnaOrnenar == null) {
            nombreColumnaOrnenar = "detalle";
        }
        if (tipoOrden == null) {
            tipoOrden = 0;
        }
        Integer finalTipoOrden = tipoOrden;
        String finalNombreColumnaOrnenar = nombreColumnaOrnenar;
        return dao.buscarConFiltros(inputParaBuscar, opcionBusqueda, stockNormal,
                stockBajo, finalNombreColumnaOrnenar, finalTipoOrden);
    }

    @Transactional
    @Override
    public List<Repuesto> buscarConCriteria(String codBarras, String nombreProd, String marcaProd,
                                            Boolean verStockNormal, Boolean verStockBajo,
                                            String colParaOrdenar, Integer tipoOrden) {
        if (codBarras == null) codBarras = "";
        if (nombreProd == null) nombreProd = "";
        if (marcaProd == null) marcaProd = "";

        return dao.buscarConCriteria(codBarras, nombreProd, marcaProd, verStockNormal, verStockBajo,
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
        List<Object[]> objects = dao.masRetiradosParaVenta(finalCantidad, finalFechaMin, finalFechaMax);
        List<RepuestoRetiradoReporteDTO> dtos = new ArrayList<>();
        for (Object[] fila : objects) {
            Repuesto repuesto = (Repuesto) fila[0];
            Long x = (Long) fila[1];
            RepuestoRetiradoReporteDTO dto = new RepuestoRetiradoReporteDTO(
                    repuesto.getCodBarra(),
                    repuesto.getMarca(),
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
            dao.save(repuesto);
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
        return dao.update(repuesto);
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
        dao.update(r);
    }

//    @Transactional
//    private Boolean consultarEstado(Repuesto r) {
//        List<Boolean> list = dao.consultaEstado(r.getCodBarra());
//        if (list.isEmpty()) return null;
//        return list.getFirst();
//    }

    /**
     * Usado cuando un repuesto se tiene que cargar o modificar y ya existe uno
     * BORRADO LÓGICAMENTE y con su mismo código de barras que es único.
     *
     * @param r con datos que sobreescriben los que ya están en db.
     */
//    // todo: esto va en la interfaz?
//    @Transactional
//    private void activaRepuesto(Repuesto r) {
//        r.setActivo(Boolean.TRUE);
//        r.getStock().setActivo(Boolean.TRUE);
//        Long id = dao.consultarId(r.getCodBarra());
//        r.setId(id);
//        dao.update(r);
//    }

}
