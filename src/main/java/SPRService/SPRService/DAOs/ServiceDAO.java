package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Service;

import java.time.LocalDate;
import java.util.List;

public interface ServiceDAO extends GenericDAO<Service, Long> {

    List<Service> verTodos();

    List<Service> buscarConFiltros(FiltroServiceDTO filtros);

    //--- REPORTES ---

    /**
     * Para generar reporte de ingresos de services por mes en 1 año entero
     */
    List<Object[]> totalIngresosAnual(Integer anio);

    /**
     * Para generar reporte de cantidad de services por mes en 1 año entero
     */
    List<Object[]> cantidadDeServicesAnual(Integer anio);

    /**
     * Consulta varios datos para reporte sobre 1 año entero
     */
    DatosReporteServiceDTO generarDatosAnuales(Integer anio);

    /**
     * Para generar datos comparativos sobre ventas y service durante un periodo de tiempo.
     */
    ReporteComparacionDTO generarComparacion(LocalDate fechaMin, LocalDate fechaMax);
}
