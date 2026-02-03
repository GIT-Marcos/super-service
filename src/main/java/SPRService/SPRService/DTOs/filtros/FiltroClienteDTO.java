package SPRService.SPRService.DTOs.filtros;

public record FiltroClienteDTO(
        String dni,
        String apellido,
        String nombre,
        Integer offset,
        Integer limit
) {
    // Constructor sin paginación
    public FiltroClienteDTO(String dni, String apellido, String nombre) {
        this(dni, apellido, nombre, null, null);
    }

    // Constructor por defecto
    public FiltroClienteDTO() {
        this("", "", "", null, null);
    }
}