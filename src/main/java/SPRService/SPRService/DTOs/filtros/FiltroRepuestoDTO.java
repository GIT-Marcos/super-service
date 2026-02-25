package SPRService.SPRService.DTOs.filtros;

import java.util.List;

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
        List<String> ubicaciones,
        Integer offset,
        Integer limit
) {
    // Constructor vacío (sin filtros)
    public FiltroRepuestoDTO() {
        this("", "", "", true, true, true, true,
                "detalle", 0, List.of(), null, null);
    }

    // Constructor sin paginación ni ubicaciones (compatibilidad)
    public FiltroRepuestoDTO(String codBarras, String nombre, String marca,
                             boolean stockNormal, boolean stockBajo, boolean activos,
                             boolean inactivos, String colOrden, Integer tipoOrden) {
        this(codBarras, nombre, marca, stockNormal, stockBajo, activos,
                inactivos, colOrden, tipoOrden, List.of(), null, null);
    }

    // Constructor sin paginación pero con ubicaciones
    public FiltroRepuestoDTO(String codBarras, String nombre, String marca,
                             boolean stockNormal, boolean stockBajo, boolean activos,
                             boolean inactivos, String colOrden, Integer tipoOrden,
                             List<String> ubicaciones) {
        this(codBarras, nombre, marca, stockNormal, stockBajo, activos,
                inactivos, colOrden, tipoOrden, ubicaciones, null, null);
    }
}