package SPRService.SPRService.DTOs;

import java.math.BigDecimal;

public record ReporteIngresosEnAnioPorMesDTO(int nroMes, String mes, BigDecimal totalVendido) {

    public ReporteIngresosEnAnioPorMesDTO {}

    public ReporteIngresosEnAnioPorMesDTO(int nroMes, BigDecimal totalVendido) {
        this(nroMes, buscaMes(nroMes), totalVendido);
    }

    private static String buscaMes(int nroMes) {
        return switch (nroMes) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "Mes inválido";
        };
    }
}

