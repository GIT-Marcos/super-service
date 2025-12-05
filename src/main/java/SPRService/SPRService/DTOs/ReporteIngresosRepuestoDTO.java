package SPRService.SPRService.DTOs;

import java.math.BigDecimal;

public record ReporteIngresosRepuestoDTO(
        String codBarra,
        String marca,
        String detalle,
        Double cantidadVendida,
        BigDecimal IngresosGenerados
) {
}
