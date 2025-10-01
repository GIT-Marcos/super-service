package SPRService.SPRService.navigation;

import SPRService.SPRService.controllers.*;

public enum Views {

    //Views ventanas
    LOGIN("/views/Login.fxml", LoginController.class),
    MAIN("/views/MainView.fxml", MainController.class),
    DEPOSITO("/views/Deposito.fxml", DepositoController.class),
    NUEVA_VENTA("/views/NuevaVenta.fxml", NuevaVentaController.class),
    VENTAS("/views/Ventas.fxml", VentasController.class),
    NOTAS_RETIRO("/views/NotasRetiro.fxml", NotasRetiroController.class),
    VEHICULOS("/views/Vehiculos.fxml", VehiculosController.class),
    CLIENTES("/views/Clientes.fxml", ClienteController.class),

    // Views modales:
    CREAR_USUARIO("/views/CrearUsuario.fxml", CrearUsuarioController.class),
    GUARDAR_REPUESTO("/views/CargarRepuesto.fxml", CargarRepuestoController.class),
    PAGO("/views/Pago.fxml", PagoController.class),
    SELECTOR_FECHA_REPORTE("/views/SelectorFechasReporte.fxml", SelectorFechasReporteController.class),
    DETALLE_VENTA("/views/DetalleVenta.fxml", DetalleVentaController.class),
    DETALLE_NOTA_RETIRO("/views/DetalleNotaRetiro.fxml", DetalleNotaRetiroController.class),
    DATOS_VEHICULO("/views/StepDatosVehiculo.fxml", StepDatosVehiculoController.class),
    MARCA_VEHICULO("/views/StepDatosMarca.fxml", StepDatosMarcaController.class),
    MODELO_VEHICULO("/views/StepDatosModelo.fxml", StepDatosModeloController.class),
    DETALLE_VEHICULO("/views/DetallesVehiculo.fxml", DetalleVehiculoController.class),
    CARGAR_VEHICULO("/views/CargarVehiculo.fxml", WizardCargarVehiculoController.class),
    CARGAR_CLIENTE("/views/CargarCliente.fxml", CargarClienteController.class);

    private final String fxmlPath;
    private final Class<?> controllerClass;

    Views(String fxmlPath, Class<?> controllerClass) {
        this.fxmlPath = fxmlPath;
        this.controllerClass = controllerClass;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

}
