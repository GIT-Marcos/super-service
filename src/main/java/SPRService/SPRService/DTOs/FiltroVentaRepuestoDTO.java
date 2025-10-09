package SPRService.SPRService.DTOs;

import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record FiltroVentaRepuestoDTO(Long codVenta, BigDecimal montoMin, BigDecimal montoMax, LocalDate fechaMin,
                                     LocalDate fechaMax, List<EstadoVentaRepuesto> estados, String colOrden,
                                     Integer tipoOrden) {

    public FiltroVentaRepuestoDTO(Long codVenta, BigDecimal montoMin, BigDecimal montoMax, LocalDate fechaMin,
                                  LocalDate fechaMax, List<EstadoVentaRepuesto> estados, String colOrden,
                                  Integer tipoOrden) {
        this.codVenta = codVenta;
        this.montoMin = tomarBigDecimal(montoMin);
        this.montoMax = tomarBigDecimal(montoMax);
        this.fechaMin = fechaMin;
        this.fechaMax = fechaMax;
        this.estados = estados;
        this.colOrden = colOrden;
        this.tipoOrden = tipoOrden;
    }

    public FiltroVentaRepuestoDTO() {
        this(null, null, null, LocalDate.of(1990, 1, 1),
                LocalDate.now(), null, "fechaVenta", 0);
    }

    // 0 y null deben ser lo mismo: no interesa el límite.
    private static BigDecimal tomarBigDecimal(BigDecimal bd) {
        if (Objects.equals(bd, BigDecimal.ZERO)) {
            return null;
        }
        return bd;
    }
}
