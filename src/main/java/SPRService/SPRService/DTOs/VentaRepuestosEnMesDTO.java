package SPRService.SPRService.DTOs;

import java.math.BigDecimal;

public record VentaRepuestosEnMesDTO(
        int dia,
        BigDecimal totalVendido
) {}
