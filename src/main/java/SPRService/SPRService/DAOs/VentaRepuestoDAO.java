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

    List<Object[]> cantidadVentasPorMeses(Integer anio);
    
    List<Object[]> totalVentasPorMeses(Integer anio);

    /**
     * Genera reporte por DÍA de total ganancias de ventas de repuestos en un mes en particular.
     */
    List<VentaRepuestosEnMesDTO> reporteTotalVentasDiariasEnMes(int anio, int mes);
    
    //ESCRITURA

    VentaRepuesto borradoLogico(VentaRepuesto ventaRepuesto, AuditoriaVenta auditoriaVenta);
}
