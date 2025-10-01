package SPRService.SPRService.util;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Usada cuando hay DatePicker.
 */
public class SafeLocalDateConverter extends StringConverter<LocalDate> {

    private final DateTimeFormatter formatter;

    /**
     * Crea un conversor con un patrón de fecha específico.
     * @param pattern El patrón de formato de fecha (ej. "dd/MM/yyyy").
     */
    public SafeLocalDateConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * Crea un conversor con el formato por defecto "dd/MM/yyyy".
     */
    public SafeLocalDateConverter() {
        this("dd/MM/yyyy");
    }

    /**
     * Convierte un objeto LocalDate a su representación en texto.
     * Si el objeto es null, devuelve una cadena vacía.
     */
    @Override
    public String toString(LocalDate date) {
        if (date == null) {
            return "";
        }
        return formatter.format(date);
    }

    /**
     * Convierte una cadena de texto a un objeto LocalDate.
     * Si el texto es nulo, vacío o no tiene un formato de fecha válido,
     * devuelve null en lugar de lanzar una excepción.
     */
    @Override
    public LocalDate fromString(String string) {
        // Si el texto de entrada es nulo o está vacío, el resultado es una fecha nula.
        if (string == null || string.trim().isEmpty()) {
            return null;
        }

        try {
            // Intenta convertir el texto a una fecha usando el formateador.
            return LocalDate.parse(string, formatter);
        } catch (DateTimeParseException e) {
            // ¡Esta es la clave! Si la conversión falla, capturamos la excepción
            // y simplemente devolvemos null. No se mostrará ningún error en la consola.
            return null;
        }
    }
}
