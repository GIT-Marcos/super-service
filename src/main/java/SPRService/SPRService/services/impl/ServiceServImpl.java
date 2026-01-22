package SPRService.SPRService.services.impl;

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
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceServImpl implements ServiceServ {

    private final ServiceDAO daoService;
    private final StockServ stockServ;
    private final NotaRetiroServ notaRetiroServ;

    @Inject
    public ServiceServImpl(ServiceDAO daoService, StockServ stockServ, NotaRetiroServ notaRetiroServ) {
        this.daoService = daoService;
        this.stockServ = stockServ;
        this.notaRetiroServ = notaRetiroServ;
    }

    /**
     * Para que no se guarden notas de retiro vacías
     */
    private void validarNota(Service s) {
        if (s.getOrden().getNotaRetiro() == null || s.getOrden().getNotaRetiro().getDetallesRetiroList().isEmpty()) {
            //todo: ver si cancelar nota
            s.getOrden().setNotaRetiro(null);
        } else {
            for (DetalleRetiro d : s.getOrden().getNotaRetiro().getDetallesRetiroList()) {
                stockServ.quitarExistente(d.getRepuesto().getStock(), d.getCantidadRetirada());
            }
        }
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
    public Service cargarService(Service s) {
        validarNota(s);
        daoService.save(s);
        return s;
    }

    @Transactional
    @Override
    public Service modificarService(Service s) {
        validarNota(s);
        return daoService.update(s);
    }

    @Transactional
    @Override
    public Service cancelarService(Service s, boolean restablecerStocks, String motivo, Usuario u) {
        s.setEstadoService(EstadoService.CANCELADO);
        if (s.getPagos() != null) {
            for (Pago p : s.getPagos()) {
                p.cancelarPago();
            }
        }
        if (s.getOrden().getNotaRetiro() != null) {
            notaRetiroServ.cancelarNota(s.getOrden().getNotaRetiro());
        }

        AuditoriaVenta auditoria = new AuditoriaVenta(null, "Cancelación de service",
                motivo, LocalDateTime.now(), u);
        return daoService.cancelarService(s, auditoria);
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
        if (fechaMin == null) fechaMin = LocalDate.of(1900, 1, 1);
        if (fechaMax == null) fechaMax = LocalDate.now();
        return daoService.generarComparacion(fechaMin, fechaMax);
    }
}
