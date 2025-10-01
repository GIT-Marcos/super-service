package SPRService.SPRService.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Lanzado cuando se intenta cargar un usuario a bd con un nombre que
 * pertenece a otro ya cargado.
 */
public class DuplicateUserException extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateUserException() {
        super();
    }

    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(Throwable cause) {
        super(cause);
    }

    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
