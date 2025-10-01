package SPRService.SPRService.services;

import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VentaRepuestoServ {

    List<VentaRepuesto> verTodas();

    Map<String, BigDecimal> reporteTotalVentasPorMeses(Integer anio);

    Map<String, Long> reporteCantidadVentasPorMeses(Integer anio);

    List<VentaRepuesto> verVentasHoy();

    List<VentaRepuesto> buscarVentas(Long codVenta, List<EstadoVentaRepuesto> estadosVenta,
                                     BigDecimal montoMinimo, BigDecimal montomaximo, String nombreColumnaOrnenar,
                                     Integer tipoOrden, LocalDate fechaMinima, LocalDate fechaMaxima);

    VentaRepuesto cargarVenta(VentaRepuesto venta);

    VentaRepuesto modificarVenta(VentaRepuesto venta);

    VentaRepuesto cancelarVenta(VentaRepuesto ventaRepuesto, boolean restablecerStocks, String motivo, Usuario usuario);
}
