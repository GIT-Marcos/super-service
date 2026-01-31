package SPRService.SPRService.entities;

import java.math.BigDecimal;
import java.util.Set;

public interface Transaccion {

    /**
     * Obtiene el ID de la transacción.
     * @return El ID.
     */
    Long getId();

    /**
     * Obtiene el monto restante que el cliente debe pagar.
     * @return El monto faltante.
     */
    BigDecimal getMontoFaltante();

    /**
     * Asocia un pago a la transacción y actualiza el monto faltante/estado.
     * Recalcular montos después de usar.
     * @param pago El objeto Pago a asociar.
     */
    void asociarPago(Pago pago);

    /**
     * Recalcula el total y lo pagado de la transacción.
     * Usar siempre al agregar pagos o modificar la transacción.
     */
    void recalcularMontos();

    /**
     * Trae todos los pagos asociados a la transacción.
     */
    Set<Pago> traerPagos();

    /**
     * Devuelve true si la transacción ya ha sido persistida (útil para el flagAgregarPago).
     * @return true si el ID no es nulo.
     */
    default boolean yaPersistida() {
        return getId() != null;
    }

}
