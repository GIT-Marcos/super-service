package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.AuditoriaVenta;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VentaRepuestoDAO extends GenericDAO<VentaRepuesto, Long>{

    //LECTURA

    List<VentaRepuesto> buscarVentas(Long codVenta, List<EstadoVentaRepuesto> estadosVenta,
                                     BigDecimal montoMinimo, BigDecimal montomaximo, String nombreColOrdenar,
                                     Integer tipoOrden, LocalDate fechaMinima, LocalDate fechaMaxima);

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
    
    //ESCRITURA

    VentaRepuesto borradoLogico(VentaRepuesto ventaRepuesto, AuditoriaVenta auditoriaVenta);
}
