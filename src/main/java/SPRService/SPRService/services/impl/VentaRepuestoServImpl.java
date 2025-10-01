package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.StockDAO;
import SPRService.SPRService.DAOs.VentaRepuestoDAO;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, BigDecimal> reporteTotalVentasPorMeses(Integer anio) {
        List<Object[]> objetos;
        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        objetos = daoVenta.totalVentasPorMeses(anio);
        Map<String, BigDecimal> ventasPorMes = new LinkedHashMap<>();
        // Inicializa con 0 para todos los meses
        for (int i = 0; i < 12; i++) {
            ventasPorMes.put(meses[i], BigDecimal.ZERO);
        }
        // Llena con los datos reales desde la BD
        for (Object[] fila : objetos) {
            Integer mes = (Integer) fila[0];  // mes: 1 - 12
            BigDecimal cantidad = (BigDecimal) fila[1];
            ventasPorMes.put(meses[mes - 1], cantidad);
        }
        return ventasPorMes;
    }

    @Transactional
    @Override
    public Map<String, Long> reporteCantidadVentasPorMeses(Integer anio) {
        List<Object[]> objetos;
        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        objetos = daoVenta.cantidadVentasPorMeses(anio);
        Map<String, Long> ventasPorMes = new LinkedHashMap<>();
        // Inicializa con 0 para todos los meses
        for (int i = 0; i < 12; i++) {
            ventasPorMes.put(meses[i], 0L);
        }
        // Llena con los datos reales desde la BD
        for (Object[] fila : objetos) {
            Integer mes = (Integer) fila[0];  // mes: 1 - 12
            Long cantidad = (Long) fila[1];
            ventasPorMes.put(meses[mes - 1], cantidad);
        }
        return ventasPorMes;
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
