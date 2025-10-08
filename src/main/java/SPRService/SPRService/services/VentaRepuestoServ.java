package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.VentaRepuestosCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnAnioDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VentaRepuestoServ {

    List<VentaRepuesto> verTodas();

    List<VentaRepuestosEnAnioDTO> reporteTotalVentasEnAnio(int anio);

    List<VentaRepuestosCantidadEnAnioDTO> reporteCantidadVentasEnAnio(int anio);

    List<VentaRepuestosEnMesDTO> reporteTotalVentasEnMes(int anio, int mes);

    List<VentaRepuesto> verVentasHoy();

    List<VentaRepuesto> buscarVentas(Long codVenta, List<EstadoVentaRepuesto> estadosVenta,
                                     BigDecimal montoMinimo, BigDecimal montomaximo, String nombreColumnaOrnenar,
                                     Integer tipoOrden, LocalDate fechaMinima, LocalDate fechaMaxima);

    VentaRepuesto cargarVenta(VentaRepuesto venta);

    VentaRepuesto modificarVenta(VentaRepuesto venta);

    VentaRepuesto cancelarVenta(VentaRepuesto ventaRepuesto, boolean restablecerStocks, String motivo, Usuario usuario);
}
