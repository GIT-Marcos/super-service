package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.VentaRepuestoServ;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import SPRService.SPRService.viewModels.tablas.VentaRepuestoVMtabla;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SessionManager;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.GeneradorPDF;
import SPRService.SPRService.util.generadores.GeneradorReportes;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class VentasController implements Initializable {

    private ObservableList<VentaRepuestoVMtabla> obsListVentasVM = FXCollections.observableArrayList();
    private final VentaRepuestoServ ventaRepuestoServ;
    private final Navigator navigator;

    @Inject
    public VentasController(VentaRepuestoServ ventaRepuestoServ, AppCoordinator appCoordinator) {
        this.ventaRepuestoServ = ventaRepuestoServ;
        this.navigator = appCoordinator.getMainNavigator();
    }

    @FXML
    private TextField tfBuscar, tfMontoMin, tfMontoMax;
    @FXML
    private CheckBox checkPagado, checkPendiente, checkCancelado;
    @FXML
    private ComboBox<String> comboOrdenarPor, comboTipoOrden;
    @FXML
    private TableView<VentaRepuestoVMtabla> tablaVentas;
    @FXML
    private TableColumn<VentaRepuestoVMtabla, Long> colCodVenta, colEstadoVenta, colFechaVenta, colMontoVenta;
    @FXML
    private DatePicker dateFechaMin, dateFechaMax;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarCombos();
        dateFechaMin.setConverter(new SafeLocalDateConverter());
        dateFechaMin.setPromptText("dd/MM/yyyy");
        dateFechaMax.setConverter(new SafeLocalDateConverter());
        dateFechaMax.setPromptText("dd/MM/yyyy");

        configColumnas();
        tablaVentas.setItems(obsListVentasVM);
        llenarTabla(ventaRepuestoServ.verTodas());
    }

    @FXML
    private void buscarConFiltros() {
        LocalDate fechaMin = dateFechaMin.getValue();
        LocalDate fechaMax = dateFechaMax.getValue();
        Long codigo;
        int tipoOrden;
        BigDecimal montoMin;
        BigDecimal montoMax;
        List<EstadoVentaRepuesto> estados;
        try {
            codigo = ManejadorInputs.codigoVenta(tfBuscar.getText().strip(), false);
            montoMin = ManejadorInputs.dinero(tfMontoMin.getText().strip(), false);
            montoMax = ManejadorInputs.dinero(tfMontoMax.getText().strip(), false);
            tipoOrden = comboTipoOrden.getSelectionModel().getSelectedIndex();
            estados = tomarEstados();
        } catch (NullPointerException | IllegalArgumentException npe) {
            Alertas.aviso("Buscar ventas", npe.getMessage());
            return;
        }
        if (estados.isEmpty()) {
            obsListVentasVM.clear();
            return;
        }

        llenarTabla(ventaRepuestoServ.buscarVentas(codigo, estados, montoMin, montoMax,
                tomaOrdenPor(), tipoOrden, fechaMin, fechaMax));
    }

    @FXML
    private void todasVentas() {
        llenarTabla(ventaRepuestoServ.verTodas());
    }

    @FXML
    private void ventasHoy() {
        llenarTabla(ventaRepuestoServ.verVentasHoy());
    }

    @FXML
    private void verDetalles() {
        VentaRepuesto ventaParaDetalles;
        VentaRepuestoVMtabla vrvm = tablaVentas.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            Alertas.aviso("Detalles de venta", "Debe seleccionar una venta para ver sus detalles.");
            return;
        }
        ventaParaDetalles = vrvm.getVentaRepuesto();

        Optional<VentaRepuesto> result = navigator.openModal(Views.DETALLE_VENTA, "Detalles de venta",
                        ventaParaDetalles);
        if (result.isPresent()) vrvm.actualizarDesdeEntidad(result.get());
    }

    @FXML
    private void imprimirFactura(ActionEvent event) {
        VentaRepuesto ventaParaImpresion;
        VentaRepuestoVMtabla vrvm = tablaVentas.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            Alertas.aviso("Impresión de venta", "Debe seleccionar una venta para imprimir su factura.");
            return;
        } else if (vrvm.getVentaRepuesto().getEstadoVenta() == EstadoVentaRepuesto.CANCELADO) {
            Alertas.aviso("Impresión de venta", "Esta venta ya está cancelada y no es posible imprimir " +
                    "su factura.");
            return;
        }
        ventaParaImpresion = vrvm.getVentaRepuesto();

        File file = SimpleDialogs.selectorRuta(event, "Seleccione donde guardar la factura", "factura.pdf",
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        if (file == null) {
            return;
        }
        try {
            GeneradorPDF.generaPDFVenta(ventaParaImpresion, file);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Alertas.error("Impresión de factura", "Ha ocurrido un error inesperado el imprimir la " +
                    "factura.");
        }
    }

    @FXML
    private void totalVentasAnual(ActionEvent event) {
        Integer fecha = SimpleDialogs.selectorFechaReporte("Generar reporte", "Ingrese el número del año para el reporte. \n" +
                "Ej: 2025", "Año: ");
        if (fecha == null) {
            return;
        }
        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para la generación del reporte",
                "reporte total ventas año " + fecha + ".jpg",
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) {
            return;
        }
        Map<String, BigDecimal> datos = ventaRepuestoServ.reporteTotalVentasPorMeses(fecha);
        GeneradorReportes.totalVentasAnual(file, datos);
    }

    @FXML
    private void cantidadVentasAnual(ActionEvent event) {
        Integer fecha = SimpleDialogs.selectorFechaReporte("Generar reporte", "Ingrese el número del año para el reporte. \n" +
                "Ej: 2025", "Año: ");
        if (fecha == null) {
            return;
        }
        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para la generación del reporte",
                "reporte cantidad de ventas año " + fecha + ".jpg",
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) {
            return;
        }
        Map<String, Long> datos = ventaRepuestoServ.reporteCantidadVentasPorMeses(fecha);
        GeneradorReportes.cantidadVentasAnual(file, datos);
    }

    @FXML
    private void cancelarVenta() {
        VentaRepuesto ventaParaCancelar;
        VentaRepuestoVMtabla vrvm = tablaVentas.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            Alertas.aviso("Cancelación de venta", "Debe seleccionar una venta para cancelarla.");
            return;
        } else if (vrvm.getVentaRepuesto().getEstadoVenta() == EstadoVentaRepuesto.CANCELADO) {
            Alertas.aviso("Cancelación de venta", "Esta venta ya está cancelada.");
            return;
        }
        ventaParaCancelar = vrvm.getVentaRepuesto();

        Usuario usuarioCancelador = SessionManager.getUsuarioSesion();
        if (usuarioCancelador == null) {
            Alertas.error("Cancelación de venta", "No hay usuario en la sesión activa.");
            return;
        }

        String motivo = SimpleDialogs.motivoBorrado();
        if (motivo == null) return;

        boolean confirmacion = Alertas.confirmacion("Cancelación de venta", "Esta acción es " +
                "irreversible.\n ¿Confirmar el borrado de venta?");
        if (!confirmacion) return;

        Boolean restablecerStock = Alertas.confirmacionRestablecerStocks();
        if (restablecerStock == null) return;

        try {
            ventaParaCancelar = ventaRepuestoServ.cancelarVenta(ventaParaCancelar, restablecerStock, motivo,
                    usuarioCancelador);
            vrvm.actualizarDesdeEntidad(ventaParaCancelar);
            Alertas.exito("Cancelación de venta", "Se ha cancelado la venta con éxito.");
        } catch (RuntimeException e) {
            Alertas.aviso("Cancelación de venta", e.getMessage());
            throw e;
        }
    }

    private List<EstadoVentaRepuesto> tomarEstados() {
        List<EstadoVentaRepuesto> list = new ArrayList<>();
        if (checkPagado.isSelected()) {
            list.add(EstadoVentaRepuesto.PAGADO);
        }
        if (checkPendiente.isSelected()) {
            list.add(EstadoVentaRepuesto.PENDIENTE_PAGO);
        }
        if (checkCancelado.isSelected()) {
            list.add(EstadoVentaRepuesto.CANCELADO);
        }
        return list;
    }

    private void configColumnas() {
        colCodVenta.setCellValueFactory(new PropertyValueFactory<>("codVenta"));
        colEstadoVenta.setCellValueFactory(new PropertyValueFactory<>("estadoVenta"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        colMontoVenta.setCellValueFactory(new PropertyValueFactory<>("montoVenta"));
    }

    private void llenarTabla(List<VentaRepuesto> ventas) {
        obsListVentasVM.clear();
        for (VentaRepuesto v : ventas) {
            obsListVentasVM.add(new VentaRepuestoVMtabla(v));
        }
    }

    private void llenarCombos() {
        ObservableList<String> listaTipoOrden = FXCollections.observableArrayList();
        listaTipoOrden.add("Ascendente");
        listaTipoOrden.add("Descendente");
        comboTipoOrden.setItems(listaTipoOrden);
        comboTipoOrden.getSelectionModel().select(0);
        ObservableList<String> listaOrdenPor = FXCollections.observableArrayList();
        listaOrdenPor.add("Código");
        listaOrdenPor.add("Monto");
        listaOrdenPor.add("Fecha");
        comboOrdenarPor.setItems(listaOrdenPor);
        comboOrdenarPor.getSelectionModel().select(0);
    }

    private String tomaOrdenPor() {
        int ordenarPor = comboOrdenarPor.getSelectionModel().getSelectedIndex();
        //los valores q toma son el del atributo de Repuesto.class
        return switch (ordenarPor) {
            case 0 -> "id";
            case 1 -> "montoTotal";
            case 2 -> "fechaVenta";
            default -> "id";
        };
    }

}
