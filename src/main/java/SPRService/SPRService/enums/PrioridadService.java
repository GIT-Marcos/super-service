package SPRService.SPRService.enums;

public enum PrioridadService {

    MUY_ALTA("Muy alta"),
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja"),
    MUY_BAJA("Muy baja");

    private final String prioridad;

    PrioridadService (String prioridad) {
        this.prioridad = prioridad;
    }

    @Override
    public String toString() {
        return "PrioridadService{" +
                "prioridad='" + prioridad + '\'' +
                '}';
    }
}
