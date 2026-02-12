package SPRService.SPRService.DTOs.filtros;

public record FiltroVehiculoDTO(
        String patente,
        String modelo,
        String marca,
        boolean activos,
        boolean baja,
        Integer offset,
        Integer limit
) {
    // Constructor sin paginación
    public FiltroVehiculoDTO(String patente, String modelo, String marca, boolean activos, boolean baja) {
        this(patente, modelo, marca, true, true, null, null);
    }

    // Constructor por defecto
    public FiltroVehiculoDTO() {
        this("", "", "", true, true, null, null);
    }
}