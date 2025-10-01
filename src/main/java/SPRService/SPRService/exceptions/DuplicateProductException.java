package SPRService.SPRService.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Lanzada a nivel de negocio cuando un producto (autoparte en este caso) que se quiere cargar a db
 * y ya existe uno con el mismo código de barras.
 */
public class DuplicateProductException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateProductException(String message) {
        super(message);
    }

    public DuplicateProductException(Throwable cause) {
        super(cause);
    }

    public DuplicateProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
