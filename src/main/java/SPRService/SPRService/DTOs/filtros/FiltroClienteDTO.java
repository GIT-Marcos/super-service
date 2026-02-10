package SPRService.SPRService.DTOs.filtros;

public record FiltroClienteDTO(
        String dni,
        String apellido,
        String nombre,
        boolean activos,
        boolean baja,
        Integer offset,
        Integer limit
) {
    // Constructor sin paginación
    public FiltroClienteDTO(String dni, String apellido, String nombre, boolean activos, boolean baja) {
        this(dni, apellido, nombre, activos, baja, null, null);
    }

    // Constructor por defecto
    public FiltroClienteDTO() {
        this("", "", "", true, true, null, null);
    }
}