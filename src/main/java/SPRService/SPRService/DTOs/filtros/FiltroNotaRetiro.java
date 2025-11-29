package SPRService.SPRService.DTOs.filtros;

import SPRService.SPRService.entities.NotaRetiro;

import java.time.LocalDate;
import java.util.Set;

public record FiltroNotaRetiro(
        LocalDate fechaMin,
        LocalDate fechaMax,
        Boolean activo,
        Set<NotaRetiro.TipoUsoRetiro> tipoDeUsos
) {
}
