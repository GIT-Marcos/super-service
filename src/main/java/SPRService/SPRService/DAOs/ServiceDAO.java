package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.AuditoriaVenta;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServiceDAO extends GenericDAO<Service, Long> {

    /**
     * Trae datos básicos de entidad service para tabla principal. No trae ninguna de sus relaciones.
     */
    List<Service> verTodos();

    /**
     * Relaciones de service necesarios para modificación.
     */
    Optional<Service> traerDatosParaModificar(Long id);

    /**
     * Service solo con relación pago.
     */
    Optional<Service> datosPagos(Long id);

    /**
     * Auto y cliente de un service para la impresión de tickets.
     */
    Optional<Service> traerDatosParaTicket(Long id);

    List<Service> buscarConFiltros(FiltroServiceDTO filtros);

    ResultadoPaginado<Service> buscarPaginado(FiltroServiceDTO filtros);

    //--- ESCRITURA ---

    void cargarAuditoriaCancelacion(AuditoriaVenta a);

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
    ReporteComparacionDTO generarComparacion(LocalDateTime fechaMin, LocalDateTime fechaMax);
}
