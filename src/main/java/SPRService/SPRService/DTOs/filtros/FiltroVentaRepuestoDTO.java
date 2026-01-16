package SPRService.SPRService.DTOs.filtros;

import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public record FiltroVentaRepuestoDTO(
        Long codVenta,
        BigDecimal montoMin,
        BigDecimal montoMax,
        LocalDateTime fechaMin,
        LocalDateTime fechaMax,
        List<EstadoVentaRepuesto> estados,
        String colOrden,
        Integer tipoOrden
) {

    public FiltroVentaRepuestoDTO(Long codVenta, BigDecimal montoMin, BigDecimal montoMax, LocalDate fechaMin,
                                  LocalDate fechaMax, List<EstadoVentaRepuesto> estados, String colOrden,
                                  Integer tipoOrden) {
        this(codVenta,
                tomarBigDecimal(montoMin),
                tomarBigDecimal(montoMax),
                (fechaMin != null) ? fechaMin.atStartOfDay() : null,
                (fechaMax != null) ? fechaMax.atTime(LocalTime.MAX) : null,
                estados,
                colOrden,
                tipoOrden);
    }

    public FiltroVentaRepuestoDTO() {
        this(null, null, null, LocalDateTime.of(1990, 1, 1, 0, 0),
                LocalDateTime.now(), null, "fechaVenta", 0);
    }

    // 0 y null deben ser lo mismo: no interesa el límite.
    private static BigDecimal tomarBigDecimal(BigDecimal bd) {
        if (Objects.equals(bd, BigDecimal.ZERO)) {
            return null;
        }
        return bd;
    }
}
