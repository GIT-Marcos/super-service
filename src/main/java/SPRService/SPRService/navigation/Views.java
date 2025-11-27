package SPRService.SPRService.navigation;

import SPRService.SPRService.controllers.*;
import SPRService.SPRService.controllers.charts.*;
import SPRService.SPRService.controllers.otros.RateController;

public enum Views {

    // Ventanas principales
    LOGIN("/views/Login.fxml", LoginController.class),
    MAIN("/views/MainView.fxml", MainController.class),
    DEPOSITO("/views/Deposito.fxml", DepositoController.class),
    VENTAS("/views/Ventas.fxml", VentasController.class),
    NOTAS_RETIRO("/views/NotasRetiro.fxml", NotasRetiroController.class),
    VEHICULOS("/views/Vehiculos.fxml", VehiculosController.class),
    CLIENTES("/views/Clientes.fxml", ClienteController.class),
    SERVICES("/views/Services.fxml", ServicesController.class),

    // Ventanas NO modales
    CARGAR_SERVICE("/views/CargarService.fxml", CargarServiceController.class),

    // Ventanas modales:
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
    CARGAR_CLIENTE("/views/CargarCliente.fxml", CargarClienteController.class),
    STEP_CLIENTE("/views/StepCliente.fxml", StepClienteController.class),
    AGREGAR_REPUESTO("/views/AgregarRepuesto.fxml", AgregarRepuestoController.class),
    AGREGAR_CLIENTE_SERVICE("/views/AgregarClienteService.fxml", AgregarClienteServiceController.class),
    AGREGAR_VEHICULO_SERVICE("/views/AgregarVehiculoService.fxml", AgregarVehiculoServiceController.class),
    MODIFICAR_SERVICE("/views/ModificarService.fxml", ModificarServiceController.class),
    DETALLE_ORDEN("/views/DetalleOrden.fxml", DetalleOrdenController.class),
    RATE("/views/Rate.fxml", RateController.class),
    VER_PAGOS("/views/VerPagos.fxml", VerPagosController.class),
    CARGAR_VENTA("/views/CargarVenta.fxml", CargarVentaController.class),

    // Charts
    CHART_VENTAS_REPUESTOS_MES("/views/ChartTotalVentasMes.fxml", ChartTotalVentasMesController.class),
    CHART_VENTAS_RESPUESTOS_ANIO("/views/ChartTotalVentasAnio.fxml", ChartTotalVentasAnioController.class),
    CHART_ANUAL_SERVICE("/views/ChartReportesAnulesService.fxml", ChartReportesAnualesServiceController.class),
    CHART_USO_REPUESTOS("/views/ChartUsoDeRepuestos.fxml", ChartUsoDeRepuestosController.class),
    CHART_COMPARACION_INGRESOS("/views/ChartComparacionIngresos.fxml", ChartComparacionIngresosController.class);

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
