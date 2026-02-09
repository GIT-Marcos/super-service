package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.RepuestoDAO;
import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.StockServ;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ServiceServImpl implements ServiceServ {

    private final ServiceDAO daoService;
    private final StockServ stockServ;
    private final NotaRetiroServ notaRetiroServ;
    private final RepuestoDAO repuestoDAO;

    @Inject
    public ServiceServImpl(ServiceDAO daoService, StockServ stockServ, NotaRetiroServ notaRetiroServ, RepuestoDAO repuestoDAO) {
        this.daoService = daoService;
        this.stockServ = stockServ;
        this.notaRetiroServ = notaRetiroServ;
        this.repuestoDAO = repuestoDAO;
    }

    @Transactional
    @Override
    public List<Service> verTodos() {
        return daoService.verTodos();
    }

    @Transactional
    @Override
    public List<Service> buscarConFiltros(FiltroServiceDTO filtros) {
        return daoService.buscarConFiltros(filtros);
    }

    @Transactional
    @Override
    public ResultadoPaginado<Service> buscarPaginado(FiltroServiceDTO filtros, int pagina, int itemsPorPagina) {
        int offset = pagina * itemsPorPagina;
        // Crear nuevo filtro con paginación
        FiltroServiceDTO filtroPaginado = new FiltroServiceDTO(
                filtros.codigo(),
                filtros.dniCliente(),
                filtros.fchMinCarga(),
                filtros.fchMaxCarga(),
                filtros.fchMinRetiro(),
                filtros.fchMaxRetiro(),
                filtros.estados(),
                filtros.prioridadServices(),
                offset,
                itemsPorPagina
        );
        return daoService.buscarPaginado(filtroPaginado);
    }

    @Transactional
    @Override
    public Optional<Service> datosParaModificar(Long id) {
        return daoService.traerDatosParaModificar(id);
    }

    @Transactional
    @Override
    public Optional<Service> datosPagos(Long id) {
        return daoService.datosPagos(id);
    }

    @Transactional
    @Override
    public Optional<Service> datosTicket(Long id) {
        return daoService.traerDatosParaTicket(id);
    }

    @Transactional
    @Override
    public Service cargarService(Service s) {
        validarNota(s);
        s.recalcularMontos();
        daoService.save(s);
        return s;
    }

    private void validarNota(Service s) {
        if (s.getOrden().getNotaRetiro() == null || s.getOrden().getNotaRetiro().getDetallesRetiro().isEmpty()) {
            s.getOrden().setNotaRetiro(null);
        } else {
            for (DetalleRetiro d : s.getOrden().getNotaRetiro().getDetallesRetiro()) {
                // Solo restamos stock de los items NUEVOS (los que agregan al modificar)
                if (d.getId() == null) {
                    stockServ.quitarExistente(d.getRepuesto().getStock(), d.getCantidadRetirada());
                }
            }
        }
    }

    @Transactional
    @Override
    public Service modificarService(Service s) {
        // 1. Devolver stock de lo que se eliminó de la lista
        gestionarDevoluciones(s);

        // 2. Procesar lo nuevo y (MUY IMPORTANTE) actualizar referencias para evitar sobrescritura
        procesarNuevosYActualizarReferencias(s);

        s.recalcularMontos();
        return daoService.update(s);
    }

    /**
     * Compara la versión guardada en BD con la versión nueva.
     * Si un detalle existía en BD y ya no está en la nueva lista, devuelve el stock.
     */
    private void gestionarDevoluciones(Service serviceNuevo) {
        Optional<Service> serviceOriginalOpt = daoService.traerDatosParaModificar(serviceNuevo.getId());

        if (serviceOriginalOpt.isPresent()) {
            Service serviceOriginal = serviceOriginalOpt.get();
            NotaRetiro notaOriginal = serviceOriginal.getOrden().getNotaRetiro();

            if (notaOriginal == null || notaOriginal.getDetallesRetiro().isEmpty()) return;

            // IDs que sobreviven en la nueva lista
            Set<Long> idsEnNuevaLista = new HashSet<>();
            if (serviceNuevo.getOrden().getNotaRetiro() != null) {
                for (DetalleRetiro d : serviceNuevo.getOrden().getNotaRetiro().getDetallesRetiro()) {
                    if (d.getId() != null) idsEnNuevaLista.add(d.getId());
                }
            }

            // Si estaba en la original y NO está en la nueva, devolvemos el stock
            for (DetalleRetiro viejo : notaOriginal.getDetallesRetiro()) {
                if (!idsEnNuevaLista.contains(viejo.getId())) {
                    // Importante: Usar el stock del objeto viejo (que es managed) para asegurar la actualización
                    stockServ.agregarExistente(viejo.getRepuesto().getStock(), viejo.getCantidadRetirada());
                }
            }
        }
    }

    /**
     * Recorre la lista nueva.
     * 1. Recarga el Repuesto "fresco" de la BD para tener el Stock actualizado.
     * 2. Reemplaza el Repuesto en el detalle por el fresco (evita el bug de sobrescritura).
     * 3. Si es un item nuevo, resta el stock.
     */
    private void procesarNuevosYActualizarReferencias(Service s) {
        if (s.getOrden().getNotaRetiro() == null) return;

        // Iteramos sobre una copia o directamente si no modificamos la estructura de la colección
        for (DetalleRetiro d : s.getOrden().getNotaRetiro().getDetallesRetiro()) {

            // A. RECARGAR REPUESTO (CRUCIAL PARA SOLUCIONAR TU ERROR)
            // Buscamos el repuesto real en la BD para ver el stock que quedó después de las devoluciones
            // Asumo que tienes un findById o similar en tu RepuestoDAO
            Repuesto repuestoFresco = repuestoDAO.getById(d.getRepuesto().getId());

            // B. VINCULAR
            // Reemplazamos el objeto "viejo" que venía de la pantalla por el "fresco" de la BD
            d.setRepuesto(repuestoFresco);

            // C. RESTAR SI ES NUEVO
            // Al ser id nulo, sabemos que es el nuevo registro con la nueva cantidad
            if (d.getId() == null) {
                stockServ.quitarExistente(repuestoFresco.getStock(), d.getCantidadRetirada());
            }
        }
    }

    @Transactional
    @Override
    public void cancelarService(Long id, boolean restablecerStocks, String motivo, Usuario u) {
        daoService.traerDatosParaModificar(id)
                .ifPresent(managedService -> {
                    managedService.setEstadoService(EstadoService.CANCELADO);
                    managedService.getPagos().forEach(Pago::cancelarPago);

                    if (restablecerStocks) {
                        if (managedService.getOrden().getNotaRetiro() != null) {
                            notaRetiroServ.cancelarNota(managedService.getOrden().getNotaRetiro().getId());
                        }
                    }

                    daoService.cargarAuditoriaCancelacion(new AuditoriaVenta("Cancelación de service",
                            motivo, u));
                });
    }

    //===================================================================
    //============================= REPORTES ============================
    //===================================================================

    @Transactional
    @Override
    public List<ReporteIngresosEnAnioPorMesDTO> totalIngresosAnual(Integer anio) {
        List<ReporteIngresosEnAnioPorMesDTO> dtos = new ArrayList<>();
        List<Object[]> filas = daoService.totalIngresosAnual(anio);

        for (Object[] o : filas) {
            int nroMes = (int) o[0];
            BigDecimal ingresos = (BigDecimal) o[1];
            dtos.add(new ReporteIngresosEnAnioPorMesDTO(nroMes, ingresos));
        }
        return dtos;
    }

    @Transactional
    @Override
    public List<ReporteCantidadEnAnioDTO> cantidadDeServicesAnual(Integer anio) {
        List<ReporteCantidadEnAnioDTO> dtos = new ArrayList<>();
        List<Object[]> objetosVenta;
        objetosVenta = daoService.cantidadDeServicesAnual(anio);

        for (Object[] o : objetosVenta) {
            int nroMes = (int) o[0];
            Long ingresos = (Long) o[1];
            dtos.add(new ReporteCantidadEnAnioDTO(nroMes, ingresos));
        }
        return dtos;
    }

    @Transactional
    @Override
    public DatosReporteServiceDTO generarDatosAnuales(Integer anio) {
        return daoService.generarDatosAnuales(anio);
    }

    @Transactional
    @Override
    public ReporteComparacionDTO generarComparacion(LocalDate fechaMin, LocalDate fechaMax) {
        if (fechaMin == null) fechaMin = LocalDate.now().minusYears(20L);
        if (fechaMax == null) fechaMax = LocalDate.now();
        return daoService.generarComparacion(fechaMin.atStartOfDay(), fechaMax.atTime(LocalTime.MAX));
    }
}