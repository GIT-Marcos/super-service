package SPRService.SPRService.DTOs;

/**
 * DTO para generar reporte de productos más retirados del depósito.
 */
public record RepuestoRetiradoReporteDTO(
    String codBarra,
    String marca,
    String detalle,
    Long cantidad
) {}
