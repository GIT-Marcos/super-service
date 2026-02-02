package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServiceServ {

    List<Service> verTodos();

    List<Service> buscarConFiltros(FiltroServiceDTO filtros);

    Optional<Service> datosParaModificar(Long id);

    Optional<Service> datosPagos(Long id);

    Optional<Service> datosTicket(Long id);

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

    /**
     * Para generar datos comparativos sobre ventas y service durante un periodo de tiempo.
     */
    ReporteComparacionDTO generarComparacion(LocalDate fechaMin, LocalDate fechaMax);

    //======================
    //===== ESCRITURA ======
    //======================
    Service cargarService(Service s);

    Service modificarService(Service s);

    void cancelarService(Long id, boolean restablecerStocks, String motivo, Usuario u);
}
