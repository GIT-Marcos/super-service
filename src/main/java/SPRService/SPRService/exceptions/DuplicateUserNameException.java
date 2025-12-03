package SPRService.SPRService.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Lanzado cuando se intenta cargar un usuario a bd con un nombre que
 * pertenece a otro ya cargado.
 */
public class DuplicateUserNameException extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateUserNameException() {
        super();
    }

    public DuplicateUserNameException(String message) {
        super(message);
    }

    public DuplicateUserNameException(Throwable cause) {
        super(cause);
    }

    public DuplicateUserNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
