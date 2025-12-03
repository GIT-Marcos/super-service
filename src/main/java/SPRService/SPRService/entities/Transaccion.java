package SPRService.SPRService.entities;

import java.math.BigDecimal;

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
     * @param pago El objeto Pago a asociar.
     */
    void asociarPago(Pago pago);

    /**
     * Devuelve true si la transacción ya ha sido persistida (útil para el flagAgregarPago).
     * @return true si el ID no es nulo.
     */
    default boolean yaPersistida() {
        return getId() != null;
    }

}
