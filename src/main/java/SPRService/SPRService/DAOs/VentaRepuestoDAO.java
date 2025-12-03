package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.filtros.FiltroVentaRepuestoDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.AuditoriaVenta;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.util.ResultadoPaginado;

import java.math.BigDecimal;
import java.util.List;

public interface VentaRepuestoDAO extends GenericDAO<VentaRepuesto, Long> {

    //LECTURA

    ResultadoPaginado<VentaRepuesto> verTodosPaginado(int pagina, int tamanioPagina);

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

    //ESCRITURA

    VentaRepuesto borradoLogico(VentaRepuesto ventaRepuesto, AuditoriaVenta auditoriaVenta);
}
