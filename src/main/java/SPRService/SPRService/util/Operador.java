package SPRService.SPRService.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Operador {

    //todo: métodos repetidos que ya están en RecibirInputs
    private static void verificaNegativo(Number number) {
        if (number instanceof Integer && (Integer) number < 0) {
            lanzaExcepcionNroNegativo();
        } else if (number instanceof Double && (Double) number < 0) {
            lanzaExcepcionNroNegativo();
        } else if (number instanceof Long && (Long) number < 0) {
            lanzaExcepcionNroNegativo();
        } else if (number instanceof BigDecimal && ((BigDecimal) number).compareTo(BigDecimal.ZERO) == -1) {
            lanzaExcepcionNroNegativo();
        }
    }

    private static void lanzaExcepcionNroNegativo() {
        throw new IllegalArgumentException("El N° ingresado no puede ser nagativo.");
    }

    /**
     * Para multiplicar dinero por otra cosa, también redondea a 2 decimales.
     * @param dinero
     * @param algo
     * @return
     */
    public static BigDecimal multiplicarDineroPorAlgo(BigDecimal dinero, BigDecimal algo) {
        if (dinero == null || algo == null) {
            throw new NullPointerException("Dinero o algo es nulo");
        }

        BigDecimal result = BigDecimal.ZERO;
        try {
            verificaNegativo(dinero);
            verificaNegativo(algo);
            result = dinero.multiply(algo);
            return result.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Formato incorrecto.");
        }
    }

    public static BigDecimal aplicarDescuento(BigDecimal precioOriginal, BigDecimal porcentaje) {
        if (precioOriginal == null || porcentaje == null) {
            throw new NullPointerException("precio o descuento nulo.");
        }
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }

        BigDecimal descuento = precioOriginal.multiply(porcentaje)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return precioOriginal.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
    }

}
