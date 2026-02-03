package SPRService.SPRService.DTOs.filtros;

import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para encapsular los criterios de filtrado de servicios.
 * Convierte las fechas (LocalDate) de la UI a fecha y hora (LocalDateTime) para la consulta.
 */
public record FiltroServiceDTO(
        Long codigo,
        String dniCliente,
        LocalDateTime fchMinCarga,
        LocalDateTime fchMaxCarga,
        LocalDateTime fchMinRetiro,
        LocalDateTime fchMaxRetiro,
        List<EstadoService> estados,
        List<PrioridadService> prioridadServices,
        Integer offset,
        Integer limit
) {

    /**
     * Constructor principal que recibe los valores de los controles de la UI.
     * Se encarga de la conversión segura de LocalDate a LocalDateTime y maneja los valores nulos.
     */
    public FiltroServiceDTO(Long codigo, String dniCliente, LocalDate fchMinCarga, LocalDate fchMaxCarga,
                            LocalDate fchMinRetiro, LocalDate fchMaxRetiro,
                            List<EstadoService> estados, List<PrioridadService> prioridadServices) {
        this(
                codigo,
                dniCliente,
                (fchMinCarga != null) ? fchMinCarga.atStartOfDay() : null,
                (fchMaxCarga != null) ? fchMaxCarga.atTime(LocalTime.MAX) : null,
                (fchMinRetiro != null) ? fchMinRetiro.atStartOfDay() : null,
                (fchMaxRetiro != null) ? fchMaxRetiro.atTime(LocalTime.MAX) : null,
                estados,
                prioridadServices,
                null,
                null
        );
    }

    /**
     * Constructor con paginación (recibe LocalDate)
     */
    public FiltroServiceDTO(Long codigo, String dniCliente, LocalDate fchMinCarga, LocalDate fchMaxCarga,
                            LocalDate fchMinRetiro, LocalDate fchMaxRetiro,
                            List<EstadoService> estados, List<PrioridadService> prioridadServices,
                            Integer offset, Integer limit) {
        this(
                codigo,
                dniCliente,
                (fchMinCarga != null) ? fchMinCarga.atStartOfDay() : null,
                (fchMaxCarga != null) ? fchMaxCarga.atTime(LocalTime.MAX) : null,
                (fchMinRetiro != null) ? fchMinRetiro.atStartOfDay() : null,
                (fchMaxRetiro != null) ? fchMaxRetiro.atTime(LocalTime.MAX) : null,
                estados,
                prioridadServices,
                offset,
                limit
        );
    }

    /**
     * Constructor para un estado de "ver todos" o sin filtros aplicados.
     */
    public FiltroServiceDTO() {
        this(null, null, (LocalDateTime) null, null, null, null, null, null, null, null);
    }
}