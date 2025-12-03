package SPRService.SPRService.DTOs;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ReporteComparacionDTO(

        // ===== Ingresos =====
        BigDecimal ingService,
        BigDecimal ingVenta,
        BigDecimal ingTotal,

        // ===== Cantidades =====
        Long cantService,
        Long cantVenta,
        Long cantTotal,

        // ===== Porcentajes =====
        Double pctService,
        Double pctVenta

) {

    public ReporteComparacionDTO(BigDecimal ingService, BigDecimal ingVenta,
                                 Long cantService, Long cantVenta) {

        this(
                safe(ingService),
                safe(ingVenta),
                safe(ingService).add(safe(ingVenta)),               // total ingresos

                safe(cantService),
                safe(cantVenta),
                safe(cantService) + safe(cantVenta),                // total operaciones

                calcularPorcentajeIngresos(safe(ingService),
                        safe(ingService).add(safe(ingVenta))),      // pct servicios según ingresos

                calcularPorcentajeIngresos(safe(ingVenta),
                        safe(ingService).add(safe(ingVenta)))       // pct ventas según ingresos
        );
    }


    // ===== Métodos auxiliares =====

    private static BigDecimal safe(BigDecimal bd) {
        return bd == null ? BigDecimal.ZERO : bd;
    }

    private static Long safe(Long value) {
        return value == null ? 0L : value;
    }

    // Porcentajes basados en ingresos
    private static Double calcularPorcentajeIngresos(BigDecimal valor, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0)
            return 0.0;

        return round(
                valor.multiply(BigDecimal.valueOf(100))
                        .divide(total, 4, RoundingMode.HALF_UP)
                        .doubleValue()
        );
    }

    private static Double round(Double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }


    // ===== Getters adicionales =====

    public BigDecimal totalIngresos() {
        return ingTotal == null ? BigDecimal.ZERO : ingTotal;
    }

    public Long totalOperaciones() {
        return cantTotal == null ? 0L : cantTotal;
    }

    public boolean isEmpty() {
        return totalOperaciones() == 0 && totalIngresos().compareTo(BigDecimal.ZERO) == 0;
    }
}
