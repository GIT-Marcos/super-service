package SPRService.SPRService.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Pattern;

public class ManejadorInputs {

    // Patrón para contraseñas. Permite un conjunto específico de caracteres.
    private static final Pattern PATRON_CONTRASENA = Pattern.compile(
            "^[a-zA-Z0-9!@#$%^&*()_\\-+=`{}\\[\\]/?.,<>;:'\"~|]+$"
    );
    private static final Pattern PATRON_NUMERICO = Pattern.compile(
            "^\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?$|^\\d+(\\.\\d{1,2})?$");
    private static final Pattern PATRON_NROS_DNI = Pattern.compile("^\\d+$");
    private static final Pattern PATRON_PATENTE = Pattern.compile("^[a-zA-Z0-9]*$");

    // Para evitar que la clase sea instanciada
    private ManejadorInputs() {
    }

    // ==============================
    // Métodos Públicos de Validación
    // ==============================

    public static String textoGenerico(String input, boolean esObligatorio, String nombreCampo, Integer largoMax) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El campo '" + nombreCampo + "' es obligatorio.");
            }
            return trimmedInput; // Devuelve vacío si es opcional y está vacío.
        }
        if (largoMax != null && trimmedInput.length() > largoMax) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede tener más de "
                    + largoMax + " caracteres.");
        }
        return capitalize(trimmedInput);
    }

    public static BigDecimal dinero(String input, boolean esObligatorio, boolean esNegativoPermitido) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El campo de dinero es obligatorio.");
            }
            return BigDecimal.ZERO;
        }
        // Reemplaza la coma por el punto para compatibilidad de formatos.
        String parsableInput = trimmedInput.replace(',', '.');
        if (!PATRON_NUMERICO.matcher(parsableInput).matches()) {
            throw new IllegalArgumentException("El valor '" + input + "' no es un número válido.");
        }

        try {
            // ¡CORRECCIÓN CRÍTICA! Usar new BigDecimal(String) para evitar errores de precisión.
            BigDecimal valor = new BigDecimal(parsableInput);
            if (!esNegativoPermitido && valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El valor no puede ser negativo.");
            }
            // Redondea a 2 decimales.
            return valor.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de número inválido: " + input);
        }
    }

    public static Double cantidadStock(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("La cantidad de stock es obligatoria.");
            }
            return 0.0;
        }

        String parsableInput = trimmedInput.replace(',', '.');
        try {
            double valor = Double.parseDouble(parsableInput);
            if (valor < 0) {
                throw new IllegalArgumentException("La cantidad no puede ser negativa.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de cantidad inválido: " + input);
        }
    }

    public static String codBarras(String input, boolean esObligatorio) {
        if (input == null || input.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El código de barras es obligatorio.");
            }
            return "";
        }

        if (input.contains(" ")) {
            throw new IllegalArgumentException("El código de barras no puede contener espacios.");
        }
        if (input.length() < 6 || input.length() > 23) {
            throw new IllegalArgumentException("El código de barras debe tener entre 6 y 23 caracteres.");
        }
        return input;
    }

    public static String contrasenia(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (input.strip().length() != input.length()) {
            throw new IllegalArgumentException("La contraseña no puede tener espacios al principio o al final.");
        }
        if (input.length() < 6 || input.length() > 20) {
            throw new IllegalArgumentException("La contraseña debe tener entre 6 y 20 caracteres.");
        }
        if (!PATRON_CONTRASENA.matcher(input).matches()) {
            throw new IllegalArgumentException("La contraseña contiene caracteres no válidos.");
        }
        return input;
    }

    public static String eMail(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El correo electrónico es obligatorio.");
            }
            return "";
        }
        if (trimmedInput.contains(" ")) {
            throw new IllegalArgumentException("El correo electrónico no puede contener espacios.");
        }
        if (!trimmedInput.contains("@") || !trimmedInput.contains(".")) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido.");
        }
        return trimmedInput;
    }

    public static String dni(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El DNI es obligatorio.");
            }
            return "";
        }
        if (trimmedInput.contains(" ")) throw new IllegalArgumentException("El DNI no puede contener espacios.");
        if (trimmedInput.length() < 7 || trimmedInput.length() > 8)
            throw new IllegalArgumentException("El DNI debe tener desde 7 hasta 8 caracteres.");
        if (!PATRON_NROS_DNI.matcher(trimmedInput).matches())
            throw new IllegalArgumentException("El DNI debe tener sólo números.");
        return input;
    }

    // provisorio hasta agregar la separación del id de la venta y su código
    //todo: quitar esto
    public static Long codigoVenta(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El código de venta es obligatorio.");
            }
            return 0L;
        }
        if (!PATRON_NUMERICO.matcher(trimmedInput).matches())
            throw new IllegalArgumentException("Código de venta en mal formato.");
        return Long.valueOf(input);
    }

    public static BigDecimal porcentaje(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El campo descuento es obligatorio.");
            }
            return BigDecimal.ZERO;
        }
        if (!PATRON_NROS_DNI.matcher(trimmedInput).matches())
            throw new IllegalArgumentException("El campo descuento está en mal formato.");
        BigDecimal porcentaje = new BigDecimal(trimmedInput);
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("El campo descuento está fuera de rango (0 - 100).");
        return porcentaje.setScale(2, RoundingMode.HALF_UP);
    }

    public static String marcaTarjetaYBanco(String input, boolean esObligatorio, Integer largoMin, Integer largoMax) {
        //los gets de los combos y sus modelos devuelven nulo so no hay
        // nada seleccionado por más que sean editables
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El campo es obligatorio.");
            }
            return "";
        }
        if (trimmedInput.length() < 2 || trimmedInput.length() > 30)
            throw new IllegalArgumentException("El campo está fuera de rango (2 - 30).");
        return trimmedInput;
    }

    public static String patente(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("La patente es obligatoria.");
            }
            return "";
        }
        if (trimmedInput.contains(" "))
            throw new IllegalArgumentException("La patente no debe contener espacios.");
        if (trimmedInput.length() < 6 || trimmedInput.length() > 10)
            throw new IllegalArgumentException("La patente debe tener entre 6 y 10 caracteres.");
        if (!PATRON_PATENTE.matcher(trimmedInput).matches())
            throw new IllegalArgumentException("La patente sólo debe contener números enteros y letras.");
        return trimmedInput.toUpperCase(Locale.ROOT);
    }

    public static String ultimos4(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("Si se quiere pagar con tarjeta, los últimos 4 dígitos del " +
                        "número de la tarjeta son obligatorios.");
            }
            return "";
        }
        if (trimmedInput.contains(" "))
            throw new IllegalArgumentException("No puede haber espacios en blanco en los últimos 4 números de la tarjeta.");
        if (trimmedInput.length() != 4 || !PATRON_NROS_DNI.matcher(trimmedInput).matches())
            throw new IllegalArgumentException("Últimos 4 número de la tarjeta en mal formato.");
        return trimmedInput;
    }

    public static String referenciaTarjeta(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("Si se quiere pagar con tarjeta, la referencia " +
                        "del pago es obligatorio.");
            }
            return "";
        }
        if (trimmedInput.contains(" "))
            throw new IllegalArgumentException("La referencia de pago no puede tener espacios en blanco.");
        if (trimmedInput.length() < 6 || trimmedInput.length() > 20)
            throw new IllegalArgumentException("La referencia de pago debe tener ente 6 y 20 caracteres.");
        if (!PATRON_NROS_DNI.matcher(trimmedInput).matches())
            throw new IllegalArgumentException("La referencai de pago sólo puede contener números enteros.");
        return trimmedInput;
    }

    public static String nroTel(String input, boolean esObligatorio) {
        String trimmedInput = (input == null) ? "" : input.strip();
        if (trimmedInput.isBlank()) {
            if (esObligatorio) {
                throw new IllegalArgumentException("El número de teléfono es obligatorio.");
            }
            return "";
        }
        if (trimmedInput.contains(" "))
            throw new IllegalArgumentException("El número de teléfono no puede contener espacios en blanco.");
        if (trimmedInput.length() < 9 || trimmedInput.length() > 15)
            throw new IllegalArgumentException("El número de teléfono debe tener entre 9 y 15 caracteres.");
        if (!PATRON_NROS_DNI.matcher(trimmedInput).matches())
            throw new IllegalArgumentException("El número de teléfono sólo puede contener números enteros.");
        return trimmedInput;
    }

    // ==============================
    // Métodos Privados Auxiliares
    // ==============================

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
