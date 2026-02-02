package SPRService.SPRService.DTOs.filtros;

public record FiltroRepuestoDTO(
        String codBarras,
        String nombre,
        String marca,
        Boolean stockNormal,
        Boolean stockBajo,
        String colOrden,
        Integer tipoOrden
) {
    public FiltroRepuestoDTO() {
        this("", "", "", true, true, "detalle", 0);
    }
}
