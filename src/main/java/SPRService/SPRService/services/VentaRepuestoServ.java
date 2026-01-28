package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.ReporteIngresosRepuestoDTO;
import SPRService.SPRService.DTOs.filtros.FiltroVentaRepuestoDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.util.ResultadoPaginado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VentaRepuestoServ {

    Optional<VentaRepuesto> verDetalle(Long id);

    ResultadoPaginado<VentaRepuesto> buscarVentasPaginado(FiltroVentaRepuestoDTO filtro, int pagina, int tamanioPagina);

    /* -- INICIO REPORTES -- */
    List<ReporteIngresosEnAnioPorMesDTO> reporteTotalVentasEnAnio(int anio);

    List<ReporteCantidadEnAnioDTO> reporteCantidadVentasEnAnio(int anio);

    List<VentaRepuestosEnMesDTO> reporteTotalVentasEnMes(int anio, int mes);

    Long cantidadDeVentasEnAnio(int anio);

    BigDecimal ingresosDeVentasEnAnio(int anio);

    Double ingresosPromedioPorVentaEnAnio(int anio);

    List<ReporteIngresosRepuestoDTO> ingresosPorRepuesto(LocalDate fechaMin, LocalDate fechaMax, Integer cantidad);

    /* -- FIN REPORTES -- */

    VentaRepuesto cargarVenta(VentaRepuesto venta, Pago primerPago);

    /**
     * Usado para agregar pagos a una venta, solo hace eso.
     */
    Optional<VentaRepuesto> modificarVenta(VentaRepuesto ventaDTO);

    void cancelarVenta(Long id, boolean restablecerStocks, String motivo, Usuario usuario);
}
