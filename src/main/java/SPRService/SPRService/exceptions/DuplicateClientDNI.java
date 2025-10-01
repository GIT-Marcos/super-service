package SPRService.SPRService.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Lanzada cuando se intenta guardar un cliente y ya existe otro con el mismo DNI en base de datos.
 */
public class DuplicateClientDNI extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateClientDNI(String message) {
        super(message);
    }

    public DuplicateClientDNI(Throwable cause) {
        super(cause);
    }

    public DuplicateClientDNI(String message, Throwable cause) {
        super(message, cause);
    }
}
