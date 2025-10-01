package SPRService.SPRService.DTOs;

/**
 * DTO para generar reporte de modelos de vehículos más retirados.
 */
public record ModelosMasRegistradosDTO(
        String nombreModelo,
        String anioModelo,
        String cilindrada,
        String nombreMarca,
        String logoMarca,
        Long cantidadRetirada
) {
}
