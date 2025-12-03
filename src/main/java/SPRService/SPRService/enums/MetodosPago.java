package SPRService.SPRService.enums;

public enum MetodosPago {
    EFECTIVO("Efectivo"),
    TARJETA_CREDITO("Tarjeta de crédito"),
    TARJETA_DEBITO("Tarjeta de débito"),
    TRANSFERENCIA("Transferencia"),
    MERCADO_PAGO("Mercado pago");

    private final String metodo;

    MetodosPago(String metodo) {
        this.metodo = metodo;
    }

    @Override
    public String toString() {
        return metodo;
    }
}
