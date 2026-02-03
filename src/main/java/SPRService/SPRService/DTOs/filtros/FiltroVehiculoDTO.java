package SPRService.SPRService.DTOs.filtros;

public record FiltroVehiculoDTO(
        String patente,
        String modelo,
        String marca,
        Integer offset,
        Integer limit
) {
    // Constructor sin paginación
    public FiltroVehiculoDTO(String patente, String modelo, String marca) {
        this(patente, modelo, marca, null, null);
    }

    // Constructor por defecto
    public FiltroVehiculoDTO() {
        this("", "", "", null, null);
    }
}