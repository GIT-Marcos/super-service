package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Service;

import java.util.List;

public interface ServiceServ {

    List<Service> verTodos();

    List<Service> buscarConFiltros(FiltroServiceDTO filtros);

    //--- REPORTES ---

    /**
     * Para generar reporte de ingresos de services por mes en 1 año entero
     */
    List<ReporteIngresosEnAnioPorMesDTO> totalIngresosAnual(Integer anio);

    /**
     * Para generar reporte de cantidad de services por mes en 1 año entero
     */
    List<ReporteCantidadEnAnioDTO> cantidadDeServicesAnual(Integer anio);

    /**
     * Consulta varios datos para reporte sobre 1 año entero
     */
    DatosReporteServiceDTO generarDatosAnuales(Integer anio);

    //======================
    //===== ESCRITURA ======
    //======================
    Service cargarService(Service s);

    Service modificarService(Service s);

    void borrarService(Service s);
}
