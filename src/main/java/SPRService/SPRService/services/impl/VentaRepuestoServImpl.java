package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.StockDAO;
import SPRService.SPRService.DAOs.VentaRepuestoDAO;
import SPRService.SPRService.DTOs.ReporteIngresosRepuestoDTO;
import SPRService.SPRService.DTOs.filtros.FiltroVentaRepuestoDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class VentaRepuestoServImpl implements VentaRepuestoServ {

    private final VentaRepuestoDAO daoVenta;
    private final StockDAO daoStock;
    private final NotaRetiroServ notaRetiroServ;

    @Inject
    public VentaRepuestoServImpl(VentaRepuestoDAO daoVenta, StockDAO daoStock, NotaRetiroServ notaRetiroServ) {
        this.daoVenta = daoVenta;
        this.daoStock = daoStock;
        this.notaRetiroServ = notaRetiroServ;
    }

    @Transactional
    @Override
    public Optional<VentaRepuesto> verDetalle(Long id) {
        return daoVenta.verDetalle(id);
    }

    @Transactional
    @Override
    public ResultadoPaginado<VentaRepuesto> buscarVentasPaginado(FiltroVentaRepuestoDTO filtro,
                                                                 int pagina, int tamanioPagina) {
        if (filtro == null) filtro = new FiltroVentaRepuestoDTO();
        return daoVenta.buscarPaginadoConFiltros(filtro, pagina, tamanioPagina);
    }

    @Transactional
    @Override
    public List<ReporteIngresosEnAnioPorMesDTO> reporteTotalVentasEnAnio(int anio) {
        List<ReporteIngresosEnAnioPorMesDTO> dtos = new ArrayList<>();
        List<Object[]> objetosVenta;
        objetosVenta = daoVenta.totalVentasPorMeses(anio);

        for (Object[] o : objetosVenta) {
            int nroMes = (int) o[0];
            BigDecimal ingresos = (BigDecimal) o[1];
            dtos.add(new ReporteIngresosEnAnioPorMesDTO(nroMes, ingresos));
        }
        return dtos;
    }

    @Transactional
    @Override
    public List<ReporteCantidadEnAnioDTO> reporteCantidadVentasEnAnio(int anio) {
        List<ReporteCantidadEnAnioDTO> dtos = new ArrayList<>();
        List<Object[]> objetosVenta;
        objetosVenta = daoVenta.cantidadVentasPorMeses(anio);

        for (Object[] o : objetosVenta) {
            int nroMes = (int) o[0];
            Long ingresos = (Long) o[1];
            dtos.add(new ReporteCantidadEnAnioDTO(nroMes, ingresos));
        }
        return dtos;
    }

    @Transactional
    @Override
    public List<VentaRepuestosEnMesDTO> reporteTotalVentasEnMes(int anio, int mes) {
        return daoVenta.reporteTotalVentasDiariasEnMes(anio, mes);
    }

    @Transactional
    @Override
    public Long cantidadDeVentasEnAnio(int anio) {
        return daoVenta.cantidadDeVentasEnAnio(anio);
    }

    @Transactional
    @Override
    public BigDecimal ingresosDeVentasEnAnio(int anio) {
        return daoVenta.ingresosDeVentasEnAnio(anio);
    }

    @Transactional
    @Override
    public Double ingresosPromedioPorVentaEnAnio(int anio) {
        Double result = daoVenta.ingresosPromedioPorVentaEnAnio(anio);
        return Math.round(result * 100.0) / 100.0;
    }

    @Transactional
    @Override
    public List<ReporteIngresosRepuestoDTO> ingresosPorRepuesto(LocalDate fechaMin, LocalDate fechaMax, Integer cantidad) {
        if (fechaMin == null) fechaMin = LocalDate.of(2000, 1, 1);
        if (fechaMax == null) fechaMax = LocalDate.now();
        if (cantidad < 0 || cantidad > 20) cantidad = 1;
        return daoVenta.ingresosPorRepuesto(fechaMin.atStartOfDay(), fechaMax.atTime(LocalTime.MAX), cantidad);
    }

    @Transactional
    @Override
    public VentaRepuesto cargarVenta(VentaRepuesto venta, Pago primerPago) {
        venta.asociarPago(primerPago);
        venta.recalcularMontos();

        //quita cantidades stocks
        for (DetalleRetiro d : venta.getNotaRetiro().getDetallesRetiro()) {
            d.getRepuesto().getStock().salidaDeStock(d.getCantidadRetirada());
        }
        daoStock.update(obtenerStocksDeVenta(venta));
        daoVenta.save(venta);
        return venta;
    }

    @Transactional
    @Override
    public Optional<VentaRepuesto> modificarVenta(VentaRepuesto ventaDTO) {
        Optional<VentaRepuesto> result = daoVenta.fetchParaEdicion(ventaDTO.getId());
        result.ifPresent(managedVenta -> {
            ventaDTO.getPagos().forEach(managedVenta::asociarPago);
            managedVenta.recalcularMontos();
        });
        return result;
    }

    @Transactional
    @Override
    public void cancelarVenta(Long id, boolean restablecerStocks, String motivo,
                              Usuario usuario) {
        Optional<VentaRepuesto> result = daoVenta.verDetalle(id);
        result.ifPresent(managedVenta -> {
            managedVenta.cancelarVenta();

            if (managedVenta.getPagos() != null) {
                managedVenta.getPagos().forEach(Pago::cancelarPago);
            }

            if (restablecerStocks) {
                notaRetiroServ.cancelarNota(managedVenta.getNotaRetiro().getId());
            }

            daoVenta.auditoriaCancelacion(new AuditoriaVenta("Cancelación de venta",
                    motivo, usuario));
        });
    }

    private List<Stock> obtenerStocksDeVenta(VentaRepuesto v) {
        List<Stock> stocks = new ArrayList<>();
        for (DetalleRetiro d : v.getNotaRetiro().getDetallesRetiro()) {
            stocks.add(d.getRepuesto().getStock());
        }
        return stocks;
    }
}
