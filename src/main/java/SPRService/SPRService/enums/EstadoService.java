package SPRService.SPRService.enums;

public enum EstadoService {

    CANCELADO("Cancelado"),
    PENDIENTE("Pendiente"),
    EN_ESPERA("En espera"),
    TRABAJANDO("Trabajando"),
    PAUSADO("Pausado"),
    FINALIZADO("Finalizado"),
    PAGO_PENDIENTE("Pago pendiente"),
    PAGADO("Pagado");

    private final String nombreEstado;

    EstadoService(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    @Override
    public String toString() {
        return nombreEstado;
    }
}
