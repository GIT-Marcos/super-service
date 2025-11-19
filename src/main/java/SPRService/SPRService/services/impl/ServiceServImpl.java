package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.StockServ;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceServImpl implements ServiceServ {

    private final ServiceDAO daoService;
    private final StockServ stockServ;

    @Inject
    public ServiceServImpl(ServiceDAO daoService, StockServ stockServ) {
        this.daoService = daoService;
        this.stockServ = stockServ;
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
        if (s.getOrden().getNotaRetiro() != null) {
            for (DetalleRetiro d : s.getOrden().getNotaRetiro().getDetallesRetiroList()) {
                stockServ.quitarExistente(d.getRepuesto().getStock(), d.getCantidadRetirada());
            }
        }

        daoService.save(s);
        return s;
    }

    @Transactional
    @Override
    public Service modificarService(Service s) {
        return daoService.update(s);
    }

    @Transactional
    @Override
    public void borrarService(Service s) {

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
