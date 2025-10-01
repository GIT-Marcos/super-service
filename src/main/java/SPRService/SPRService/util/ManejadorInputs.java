package SPRService.SPRService.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManejadorInputs {

    private static final String CONTRASENA_REGEX = "^[a-zA-Z0-9!@#$%^&*_\\-+=\\[\\]{}/?.,<>;:'\"`~|()]+$";
    private static final Pattern patronContrasena = Pattern.compile(CONTRASENA_REGEX);

    // ==============================
    // Métodos Privados Auxiliares
    // ==============================

    private static void verificaVacio(char[] paraVerificar) {
        if (paraVerificar == null || paraVerificar.length == 0) {
            throw new IllegalArgumentException("El campo no puede estar vacío.");
        }
    }

    private static void verificaLargo(char[] paraVerificar, Integer largoMin, Integer largoMax) {
        if (paraVerificar == null) {
            throw new IllegalArgumentException("El valor no puede ser nulo.");
        }

        int largo = paraVerificar.length;
        if (largoMin != null && largo < largoMin) {
            throw new IllegalArgumentException("El campo debe tener al menos " + largoMin + " caracteres.");
        }

        if (largoMax != null && largo > largoMax) {
            throw new IllegalArgumentException("El campo no puede tener más de " + largoMax + " caracteres.");
        }
    }

    private static void verificaEspaciosEnBlanco(char[] verificar) {
        for (char c : verificar) {
            if (Character.isWhitespace(c)) {
                throw new IllegalArgumentException("No puede haber espacios en blanco.");
            }
        }
    }

    private static void verificaSoloNumeros(char[] verificar) {
        for (char c : verificar) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("El texto solo puede tener números.");
            }
        }
    }

    private static void verificaEsNumeroConDecimal(char[] verificar) {
        String number = String.valueOf(verificar);
        try {
            Double.parseDouble(number);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Lo ingresado debe ser un número.");
        }
    }

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

    private static BigDecimal convierteDinero(String input) {
        return BigDecimal.valueOf(Double.parseDouble(input)).setScale(2, RoundingMode.HALF_UP);
    }

    private static Double convierteDouble(String input) {
        try {
            Double d = Double.valueOf(input);
            return Math.round(d * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Debe ingresar un número.");
        }
    }

    private static Long convierteLong(String input) {
        return Long.valueOf(input);
    }

    private static void lanzaExcepcionNroNegativo() {
        throw new IllegalArgumentException("El N° ingresado no puede ser nagativo.");
    }


    // ==============================
    // Métodos para campos
    // ==============================

    public static String dni(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("input nulo al convertir a dinero.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) verificaVacio(chars);
        if (chars.length > 0) {
            verificaLargo(chars, 7, 8);
            verificaEspaciosEnBlanco(chars);
            verificaSoloNumeros(chars);
        }
        return input;
    }

    public static BigDecimal dinero(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("input nulo al convertir a dinero.");
        }
        BigDecimal result = BigDecimal.ZERO;
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, null, 15);
            verificaEsNumeroConDecimal(chars);
            verificaEspaciosEnBlanco(chars);
            result = convierteDinero(input);
            verificaNegativo(result);
        }
        return result;
    }

    //provisorio hasta agregar la separación del id de la venta y su código
    public static Long codigoVenta(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("el código de venta es nulo.");
        }
        Long codigo = 0L;
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            codigo = convierteLong(input);
            verificaNegativo(codigo);
        }
        return codigo;
    }

    public static BigDecimal porcentaje(Integer input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("input porcentaje nulo.");
        }
        BigDecimal result = BigDecimal.ZERO;
        char[] chars = input.toString().toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaEspaciosEnBlanco(chars);
            if (input < 0 || input > 100) {
                throw new IllegalArgumentException("porcentaje fuera de rango (0 - 100).");
            }
            verificaNegativo(input);
            result = convierteDinero(String.valueOf(input));
        }
        return result;
    }

    public static String textoGenerico(String input, boolean esObligatorio, Integer largoMin, Integer largoMax) {
        if (input == null) {
            throw new NullPointerException("Input nulo en texto genérico.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, largoMin, largoMax);
        }
        //todo: que ponga en mayúsculas la primera letra
        return input;
    }

    public static void comboBox(String input, boolean esObligatorio, Integer largoMin, Integer largoMax) {
        //los gets de los combos y sus modelos devuelven nulo so no hay
        // nada seleccionado por más que sean editables
        if (input == null) {
            input = "";
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, largoMin, largoMax);
        }
    }

    public static String patente(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("patente es nula.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, 6, 9);
            verificaEspaciosEnBlanco(chars);
            if (!input.matches("^[a-zA-Z0-9]*$")) {
                throw new IllegalArgumentException("Error: la patente está en formato incorrecto.");
            }
        }
        return input.toLowerCase();
    }

    public static void codBarras(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("código de barras nulo.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, 6, 23);
        }
    }

    public static Double cantidadStock(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("cantidad de stock nula.");
        }
        Double cantidad = 0.0;
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, null, 12);
            verificaEspaciosEnBlanco(chars);
            cantidad = convierteDouble(input);
            verificaNegativo(cantidad);
        }
        return cantidad;
    }

    public static void ultimos4(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("últimos 4 es nulo.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, null, 4);
            verificaSoloNumeros(chars);
        }
    }

    public static void referenciaTarjeta(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("referencia de tarjeta nula.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, 6, 20);
            verificaSoloNumeros(chars);
        }
    }

    public static String eMail(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("mail nul al verificar.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) verificaVacio(chars);
        if (chars.length > 0) {
            verificaEspaciosEnBlanco(chars);
            verificaLargo(chars, 5, 30);
            if (!input.contains("@")) throw new IllegalArgumentException("El correo electrónico debe tener '@'.");
        }
        return input;
    }

    public static String nroTel(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("mail nul al verificar.");
        }
        char[] chars = input.toCharArray();
        if (esObligatorio) verificaVacio(chars);
        if (chars.length > 0) {
            verificaEspaciosEnBlanco(chars);
            verificaLargo(chars, 9, 15);
            verificaSoloNumeros(chars);
        }
        return input;
    }

    public static void contrasenia(String input, boolean esObligatorio) {
        if (input == null) {
            throw new NullPointerException("contraseña nula al verificar.");
        }
        char[] chars= input.toCharArray();
        if (esObligatorio) {
            verificaVacio(chars);
        }
        if (chars.length > 0) {
            verificaLargo(chars, 6, 20);
            if (input.trim().length() != input.length()) {
                throw new IllegalArgumentException("No pueden haber espacios en blanco al principio o al final " +
                        "de la contraseña.");
            }
            Matcher matcher = patronContrasena.matcher(input);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Ha ingresado caracteres no válidos en la contraseña.");
            }
        }
    }
}
