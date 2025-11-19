package SPRService.SPRService.DTOs;

public record ReporteCantidadEnAnioDTO(int nroMes, String mes, Long cantidadVentas) {

    public ReporteCantidadEnAnioDTO {}

    public ReporteCantidadEnAnioDTO(int nroMes, Long cantidadVentas) {
        this(nroMes, buscaMes(nroMes), cantidadVentas);
    }

    // TODO: esto se repite hacer enum de utilidad
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
