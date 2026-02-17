package SPRService.SPRService.DTOs;

import java.math.BigDecimal;

public record ClientesMasIngresosDTO(
        Long idCliente,
        String nombreCliente,
        String apellidoCliente,
        String documentoCliente,
        Integer cantidadServices,
        Integer cantidadVentas,
        Integer totalTransacciones,
        BigDecimal totalServices,
        BigDecimal totalVentas,
        BigDecimal totalIngresos
) {
    /**
     * Constructor alternativo para cuando solo se consulta un tipo de ingreso
     */
    public ClientesMasIngresosDTO(Long idCliente, String nombreCliente, String apellidoCliente,
                                  String documentoCliente, Integer totalTransacciones,
                                  BigDecimal totalIngresos) {
        this(idCliente, nombreCliente, apellidoCliente, documentoCliente,
                0, 0, totalTransacciones, BigDecimal.ZERO, BigDecimal.ZERO, totalIngresos);
    }

    public String getNombreCompleto() {
        return nombreCliente + " " + apellidoCliente;
    }
}