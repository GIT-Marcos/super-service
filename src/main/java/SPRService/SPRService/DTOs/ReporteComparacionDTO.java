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

                calcularPorcentaje(safe(cantService),
                        safe(cantService) + safe(cantVenta)),       // pct services

                calcularPorcentaje(safe(cantVenta),
                        safe(cantService) + safe(cantVenta))        // pct ventas
        );
    }


    // ===== Métodos auxiliares =====

    private static BigDecimal safe(BigDecimal bd) {
        return bd == null ? BigDecimal.ZERO : bd;
    }

    private static Long safe(Long value) {
        return value == null ? 0L : value;
    }

    private static Double calcularPorcentaje(Long valor, Long total) {
        if (total == null || total == 0) return 0.0;
        return round(valor * 100.0 / total);
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
