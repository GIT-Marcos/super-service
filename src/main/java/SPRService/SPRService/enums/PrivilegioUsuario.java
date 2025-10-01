package SPRService.SPRService.enums;

public enum PrivilegioUsuario {
    GERENCIAL("Gerencial"),
    JEFE_TALLER("Jefe Taller"),
    JEFE_VENTAS("Jefe Ventas"),
    JEFE_RECEPCION("Jefe Recepción"),
    JEFE_DEPOSITO("Jefe Depósito"),
    OPERATIVO_TALLER("Operativo Taller"),
    OPERATIVO_VENTAS("Operativo Ventas"),
    OPERATIVO_RECEPCION("Operativo Recepción"),
    OPERATIVO_DEPOSITO("Operativo Depósito");

    private final String nombreRol;

    PrivilegioUsuario(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    // Para mostrar un nombre más amigable en el ComboBox
    @Override
    public String toString() {
        return nombreRol;
    }
}
