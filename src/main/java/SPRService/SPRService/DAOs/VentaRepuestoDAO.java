package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.ReporteIngresosRepuestoDTO;
import SPRService.SPRService.DTOs.filtros.FiltroVentaRepuestoDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.AuditoriaVenta;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.util.ResultadoPaginado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepuestoDAO extends GenericDAO<VentaRepuesto, Long> {

    //LECTURA

    Optional<VentaRepuesto> verDetalle(Long id);

    /**
     * Usado para modificación de venta.
     * Trae venta con todos sus pagos.
     * Trae venta con su cliente y todas las ventas de este.
     */
    Optional<VentaRepuesto> fetchParaEdicion(Long id);

    ResultadoPaginado<VentaRepuesto> buscarPaginadoConFiltros(FiltroVentaRepuestoDTO filtro,
                                                              int pagina, int tamanioPagina);

    /**
     * Trae datos para crear los DTOs que son necesarios para el reporte sobre la cantidad de ventas de repuestos
     * por mes en un año.
     *
     * @param anio para buscar las ventas.
     */
    List<Object[]> cantidadVentasPorMeses(Integer anio);

    /**
     * Trae datos para crear los DTOs que son necesarios para el reporte sobre total de ganancias de ventas
     * de repuestos.
     *
     * @param anio para buscar las ventas.
     */
    List<Object[]> totalVentasPorMeses(Integer anio);

    /**
     * Trae los DTOs necesarios para crear un reporte sobre el total de ingresos de ventas de repuestos por día
     * en un mes en particular.
     */
    List<VentaRepuestosEnMesDTO> reporteTotalVentasDiariasEnMes(Integer anio, Integer mes);

    /**
     * Usado para información de reportes.
     *
     * @param anio año sobre el que se debe calcular.
     * @return número de ventas de repuestos activas que se realizaron en el año argumentado.
     */
    Long cantidadDeVentasEnAnio(int anio);

    /**
     * Usado para información de reportes.
     *
     * @param anio año sobre el que se debe calcular.
     * @return total de ingresos sobre ventas de repuestos activas que se realizaron en el año argumentado.
     */
    BigDecimal ingresosDeVentasEnAnio(int anio);

    /**
     * Usado para información de reportes.
     *
     * @param anio año sobre el que se debe calcular.
     * @return promedio de ingresos sobre ventas de repuestos activas que se realizaron en el año argumentado.
     */
    Double ingresosPromedioPorVentaEnAnio(int anio);

    /**
     * Genera DTOs con datos para poblar chart de reportes con más ingresos.
     * @param cantidad de elementos a buscar.
     * @return lista con DTOs que representan un repuesto con datos de negocio.
     */
    List<ReporteIngresosRepuestoDTO> ingresosPorRepuesto(LocalDateTime fechaMin, LocalDateTime fechaMax, Integer cantidad);

    //ESCRITURA

    /**
     * Guarda un registro de auditoría al cancelarse una venta.
     */
    // todo: crear dao específico para manejar todas las auditorías.
    void auditoriaCancelacion(AuditoriaVenta auditoriaVenta);
}
