package SPRService.SPRService.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Lanzada a nivel de negocio cuando se intente registrar un vehículo con una patente que ya
 * pertenece a otro.
 */
public class DuplicateVehicleException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateVehicleException() {
        super();
    }

    public DuplicateVehicleException(String message) {
        super(message);
    }

    public DuplicateVehicleException(Throwable cause) {
        super(cause);
    }

    public DuplicateVehicleException(String message, Throwable cause) {
        super(message, cause);
    }
}
