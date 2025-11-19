package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.filtros.FiltroVentaRepuestoDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.util.ResultadoPaginado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VentaRepuestoServ {

    List<VentaRepuesto> verTodas();

    List<VentaRepuesto> verVentasHoy();

    ResultadoPaginado<VentaRepuesto> buscarVentasPaginado(FiltroVentaRepuestoDTO filtro, int pagina, int tamanioPagina);

    @Deprecated
    List<VentaRepuesto> buscarVentas(Long codVenta, List<EstadoVentaRepuesto> estadosVenta,
                                     BigDecimal montoMinimo, BigDecimal montomaximo, String nombreColumnaOrnenar,
                                     Integer tipoOrden, LocalDate fechaMinima, LocalDate fechaMaxima);

    /* -- INICIO REPORTES -- */
    List<ReporteIngresosEnAnioPorMesDTO> reporteTotalVentasEnAnio(int anio);

    List<ReporteCantidadEnAnioDTO> reporteCantidadVentasEnAnio(int anio);

    List<VentaRepuestosEnMesDTO> reporteTotalVentasEnMes(int anio, int mes);

    Long cantidadDeVentasEnAnio(int anio);

    BigDecimal ingresosDeVentasEnAnio(int anio);

    Double ingresosPromedioPorVentaEnAnio(int anio);
    /* -- FIN REPORTES -- */

    VentaRepuesto cargarVenta(VentaRepuesto venta);

    VentaRepuesto modificarVenta(VentaRepuesto venta);

    VentaRepuesto cancelarVenta(VentaRepuesto ventaRepuesto, boolean restablecerStocks, String motivo, Usuario usuario);
}
