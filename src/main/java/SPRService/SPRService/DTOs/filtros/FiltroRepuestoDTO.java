package SPRService.SPRService.DTOs.filtros;

public record FiltroRepuestoDTO(
        String codBarras,
        String nombre,
        String marca,
        boolean stockNormal,
        boolean stockBajo,
        boolean activos,
        boolean inactivos,
        String colOrden,
        Integer tipoOrden,
        Integer offset,
        Integer limit
) {
    // Constructor por defecto (sin paginación - para compatibilidad)
    public FiltroRepuestoDTO() {
        this("", "", "", true, true, true, true, "detalle", 0, null, null);
    }

    // Constructor sin paginación (el original)
    public FiltroRepuestoDTO(String codBarras, String nombre, String marca,
                             boolean stockNormal, boolean stockBajo, boolean activos,
                             boolean inactivos, String colOrden, Integer tipoOrden) {
        this(codBarras, nombre, marca, stockNormal, stockBajo, activos, inactivos, colOrden, tipoOrden, null, null);
    }
}
