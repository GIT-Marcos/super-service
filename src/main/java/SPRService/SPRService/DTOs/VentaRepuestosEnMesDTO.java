package SPRService.SPRService.DTOs;

import java.math.BigDecimal;

public record VentaRepuestosEnMesDTO(
        Integer dia,
        BigDecimal totalVendido
) {}
