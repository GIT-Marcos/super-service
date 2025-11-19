package SPRService.SPRService.DTOs;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ReporteUsoDeRepuestosDTO(
        Double paraVenta,
        Double paraService,
        Double pctParaVenta,
        Double pctParaService
) {

    // Constructor principal (Double)
    public ReporteUsoDeRepuestosDTO(Double paraVenta, Double paraService) {
        this(
                safe(paraVenta),
                safe(paraService),
                round(calcularPorcentaje(safe(paraVenta), safe(paraService), true)),
                round(calcularPorcentaje(safe(paraVenta), safe(paraService), false))
        );
    }

    // Constructor secundario (Long)
    public ReporteUsoDeRepuestosDTO(Long paraVenta, Long paraService) {
        this(
                paraVenta == null ? 0.0 : paraVenta.doubleValue(),
                paraService == null ? 0.0 : paraService.doubleValue()
        );
    }

    // Evita nulls
    private static Double safe(Double value) {
        return value == null ? 0.0 : value;
    }

    // Calcula porcentaje según campo
    private static Double calcularPorcentaje(Double venta, Double service, boolean esVenta) {
        double v = safe(venta);
        double s = safe(service);
        double total = v + s;

        if (total == 0) return 0.0;

        return esVenta
                ? (v * 100.0 / total)
                : (s * 100.0 / total);
    }

    // Redondeo a 2 decimales
    private static Double round(Double value) {
        return BigDecimal
                .valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public Double total() {
        return safe(paraVenta) + safe(paraService);
    }
}
