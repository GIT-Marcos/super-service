package SPRService.SPRService.navigation;

import SPRService.SPRService.controllers.*;
import SPRService.SPRService.controllers.charts.*;
import SPRService.SPRService.controllers.otros.RateController;

public enum Views {

    // Ventanas principales
    INICIO("/views/Inicio.fxml", InicioController.class),
    LOGIN("/views/Login.fxml", LoginController.class),
    MAIN("/views/MainView.fxml", MainController.class),
    DEPOSITO("/views/Deposito.fxml", DepositoController.class),
    VENTAS("/views/Ventas.fxml", VentasController.class),
    NOTAS_RETIRO("/views/NotasRetiro.fxml", NotasRetiroController.class),
    VEHICULOS("/views/Vehiculos.fxml", VehiculosController.class),
    CLIENTES("/views/Clientes.fxml", ClienteController.class),
    SERVICES("/views/Services.fxml", ServicesController.class),
    USUARIOS("/views/Usuarios.fxml", UsuariosController.class),

    // Ventanas NO modales
    CARGAR_SERVICE("/views/CargarService.fxml", CargarServiceController.class),

    // Ventanas modales:
    CARGAR_USUARIO("/views/CargarUsuario.fxml", CrearUsuarioController.class),
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
    AGREGAR_REPUESTO("/views/AgregarRepuesto.fxml", AgregarRepuestoController.class),
    AGREGAR_CLIENTE_SERVICE("/views/AgregarClienteService.fxml", AgregarClienteServiceController.class),
    AGREGAR_VEHICULO_SERVICE("/views/AgregarVehiculoService.fxml", AgregarVehiculoServiceController.class),
    MODIFICAR_SERVICE("/views/ModificarService.fxml", ModificarServiceController.class),
    DETALLE_ORDEN("/views/DetalleOrden.fxml", DetalleOrdenController.class),
    RATE("/views/Rate.fxml", RateController.class),
    VER_PAGOS("/views/VerPagos.fxml", VerPagosController.class),
    CARGAR_VENTA("/views/CargarVenta.fxml", CargarVentaController.class),
    CARGAR_NOTA("/views/CargarNotaRetiro.fxml", CargarNotaController.class),
    MODIFICAR_USUARIO("/views/ModificarUsuario.fxml", ModificarUsuarioController.class),
    OPERACIONES_CLIENTE("/views/OperacionesCliente.fxml", OperacionesClienteController.class),

    // Charts
    CHART_VENTAS_REPUESTOS_MES("/views/ChartTotalVentasMes.fxml", ChartTotalVentasMesController.class),
    CHART_VENTAS_RESPUESTOS_ANIO("/views/ChartTotalVentasAnio.fxml", ChartTotalVentasAnioController.class),
    CHART_INGRESOS_REPUESTO("/views/ChartIngresosRepuesto.fxml", ChartIngresosRepuestoController.class),
    CHART_ANUAL_SERVICE("/views/ChartReportesAnulesService.fxml", ChartReportesAnualesServiceController.class),
    CHART_USO_REPUESTOS("/views/ChartUsoDeRepuestos.fxml", ChartUsoDeRepuestosController.class),
    CHART_COMPARACION_INGRESOS("/views/ChartComparacionIngresos.fxml", ChartComparacionIngresosController.class),
    CHART_MAS_RETIRADOS("/views/ChartRepuestosMasRetirados.fxml", ChartMasRetiradosController.class),
    CHART_VEHICULOS("/views/ChartVehiculos.fxml", ChartVehiculosController.class),
    CHART_INGRESOS_CLIENTES("/views/ChartClientesMasIngresos.fxml", ChartClientesMasIngresosController.class);

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
