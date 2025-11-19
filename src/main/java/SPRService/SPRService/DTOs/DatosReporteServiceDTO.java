package SPRService.SPRService.DTOs;

import java.math.BigDecimal;

public record DatosReporteServiceDTO(BigDecimal ingTotales, Double ingPromedioPorService, Long cantidadDeService,
                                     BigDecimal ingPorTrabajos, BigDecimal ingPorRepuestos) {
}
