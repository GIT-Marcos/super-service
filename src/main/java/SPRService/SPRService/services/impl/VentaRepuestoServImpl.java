package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.StockDAO;
import SPRService.SPRService.DAOs.VentaRepuestoDAO;
import SPRService.SPRService.DTOs.VentaRepuestosCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnAnioDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.services.VentaRepuestoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class VentaRepuestoServImpl implements VentaRepuestoServ {

    private final VentaRepuestoDAO daoVenta;
    private final StockDAO daoStock;

    @Inject
    public VentaRepuestoServImpl(VentaRepuestoDAO daoVenta, StockDAO daoStock) {
        this.daoVenta = daoVenta;
        this.daoStock = daoStock;
    }

    @Transactional
    @Override
    public List<VentaRepuesto> verTodas() {
        return daoVenta.getAll();
    }

    @Transactional
    @Override
    public List<VentaRepuestosEnAnioDTO> reporteTotalVentasEnAnio(int anio) {
        List<VentaRepuestosEnAnioDTO> dtos = new ArrayList<>();
        List<Object[]> objetosVenta;
        objetosVenta = daoVenta.totalVentasPorMeses(anio);

        for (Object[] o : objetosVenta) {
            int nroMes = (int) o[0];
            BigDecimal ingresos = (BigDecimal) o[1];
            dtos.add(new VentaRepuestosEnAnioDTO(nroMes, ingresos));
        }
        return dtos;
    }

    @Transactional
    @Override
    public List<VentaRepuestosCantidadEnAnioDTO> reporteCantidadVentasEnAnio(int anio) {
        List<VentaRepuestosCantidadEnAnioDTO> dtos = new ArrayList<>();
        List<Object[]> objetosVenta;
        objetosVenta = daoVenta.cantidadVentasPorMeses(anio);

        for (Object[] o : objetosVenta) {
            int nroMes = (int) o[0];
            Long ingresos = (Long) o[1];
            dtos.add(new VentaRepuestosCantidadEnAnioDTO(nroMes, ingresos));
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
    public List<VentaRepuesto> verVentasHoy() {
        return daoVenta.buscarVentas(null, null, null,
                null, "id", 0, LocalDate.now(), LocalDate.now());
    }

    /**
     * @param tipoOrden pasar nulo si no importa el orden
     */
    @Transactional
    @Override
    public List<VentaRepuesto> buscarVentas(Long codVenta, List<EstadoVentaRepuesto> estadosVenta,
                                            BigDecimal montoMinimo, BigDecimal montomaximo, String nombreColumnaOrnenar,
                                            Integer tipoOrden, LocalDate fechaMinima, LocalDate fechaMaxima) {
        if (codVenta == 0L) {
            codVenta = null;
        }
        if (nombreColumnaOrnenar == null) {
            nombreColumnaOrnenar = "id";
        }
        if (tipoOrden == null) {
            tipoOrden = 0;
        }
        if (montoMinimo.compareTo(BigDecimal.ZERO) == 0) {
            montoMinimo = null;
        }
        if (montomaximo.compareTo(BigDecimal.ZERO) == 0) {
            montomaximo = null;
        }
        Long finalCodVenta = codVenta;
        BigDecimal finalMontoMinimo = montoMinimo;
        BigDecimal finalMontomaximo = montomaximo;
        String finalNombreColumnaOrnenar = nombreColumnaOrnenar;
        Integer finalTipoOrden = tipoOrden;
        return daoVenta.buscarVentas(finalCodVenta, estadosVenta, finalMontoMinimo, finalMontomaximo,
                finalNombreColumnaOrnenar, finalTipoOrden, fechaMinima, fechaMaxima);
    }

    @Transactional
    @Override
    public VentaRepuesto cargarVenta(VentaRepuesto venta) {
        if (venta == null) {
            throw new NullPointerException("venta nula recibida en el servicio");
        }
        // la cantidad del stock se actualiza al crearse el objeto DetalleRetiro.
        daoVenta.save(venta);
        daoStock.update(obtenerStocksDeVenta(venta));
        return venta;
    }

    @Transactional
    @Override
    public VentaRepuesto modificarVenta(VentaRepuesto venta) {
        if (venta == null) {
            throw new NullPointerException("venta nula recibida en el servicio");
        }
        return daoVenta.update(venta);
    }

    @Transactional
    @Override
    public VentaRepuesto cancelarVenta(VentaRepuesto ventaRepuesto, boolean restablecerStocks, String motivo,
                                       Usuario usuario) {
        if (ventaRepuesto == null || usuario == null) {
            throw new NullPointerException("error: venta o usuario nulo en servicio.");
        }
        ventaRepuesto.cancelarVenta(restablecerStocks);
        AuditoriaVenta auditoriaVenta = new AuditoriaVenta(null, "Cancelación",
                motivo, LocalDateTime.now(), usuario);

        daoStock.update(obtenerStocksDeVenta(ventaRepuesto));
        return daoVenta.borradoLogico(ventaRepuesto, auditoriaVenta);
    }

    private List<Stock> obtenerStocksDeVenta(VentaRepuesto v) {
        List<Stock> stocks = new ArrayList<>();
        for (DetalleRetiro d : v.getNotaRetiro().getDetallesRetiroList()) {
            stocks.add(d.getRepuesto().getStock());
        }
        return stocks;
    }
}
