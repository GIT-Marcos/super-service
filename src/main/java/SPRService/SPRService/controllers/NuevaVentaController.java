package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.RepuestoServ;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import SPRService.SPRService.viewModels.tablas.RepuestoRowViewModel;
import SPRService.SPRService.components.TablaDetallesVentaController;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.GeneradorNotaRetiroTXT;
import SPRService.SPRService.util.generadores.Impresor;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class NuevaVentaController implements Initializable {

    private RepuestoServ repuestoServ;
    private ObservableList<RepuestoRowViewModel> obsListRepuestoVM = FXCollections.observableArrayList();
    private final Navigator navigator;

    @Inject
    public NuevaVentaController(AppCoordinator appCoordinator, RepuestoServ repuestoServ) {
        this.navigator = appCoordinator.getMainNavigator();
        this.repuestoServ = repuestoServ;
    }

    @FXML
    private TablaDetallesVentaController tablaDetallesVentaController;
    @FXML
    private TableView<RepuestoRowViewModel> tableRepuestos;
    @FXML
    private TableColumn<RepuestoRowViewModel, String> colCodBarra, colNombre, colMarca, colPrecio;
    @FXML
    private TextField tfCodBarra, tfProducto, tfMarca;
    @FXML
    private CheckBox checkStockNormal, checkStockBajo, checkRutaPredeterminada, checkImprimirNota;
    @FXML
    private Button btnPagar;
    @FXML
    private Label labelTotal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableRepuestos.setItems(this.obsListRepuestoVM);
        configColumnas();
        seteaEstiloTabla();

        llenarTablaRepuestos(repuestoServ.verTodos());
    }

    @FXML
    private void buscarConFiltros() {
        List<Repuesto> list = repuestoServ.buscarConCriteria(tfCodBarra.getText().strip(), tfProducto.getText().strip(),
                tfMarca.getText().strip(), checkStockNormal.isSelected(), checkStockBajo.isSelected(),
                null, null);
        llenarTablaRepuestos(list);
    }

    @FXML
    private void todosRepuestos() {
        llenarTablaRepuestos(repuestoServ.verTodos());
    }

    @FXML
    private void limpiarLista() {
        tablaDetallesVentaController.clearTable();
        btnPagar.setDisable(true);
        actualizarLabelTotal();
        todosRepuestos();
    }

    @FXML
    private void agregarRepuesto() {
        List<DetalleRetiro> detallesParaVenta = tablaDetallesVentaController.getDetalles();
        RepuestoRowViewModel rrvm = tableRepuestos.getSelectionModel().getSelectedItem();
        if (rrvm == null) {
            Alertas.aviso("Agregar repuesto",
                    "Debe seleccionar un repuesto de la tabla para agregarlo a la venta.");
            return;
        }

        for (DetalleRetiro dr : detallesParaVenta) {
            if (dr.getRepuesto().equals(rrvm.getRepuestoOriginal())){
                Alertas.aviso("Agregar repuesto", "Ya se ha añadido ese repuesto a la venta.");
                return;
            }
        }

        //abrir dialog para cantidad
        Double cantidad = SimpleDialogs.inputStock();
        if (cantidad == null) return;

        Repuesto repuesto = rrvm.getRepuestoOriginal();
        Double existenteStock = repuesto.getStock().getCantidadExistente();
        if (cantidad > existenteStock) {
            Alertas.error("Nueva venta", "La cantidad que se intenta vender " + cantidad + "\n" +
                    "es mayor que el existente " + repuesto.getStock().getCantidadExistente());
            return;
        }
        Double stockNuevo = existenteStock - cantidad;
        if (cantidad > stockNuevo) {
            boolean confir = Alertas.confirmacion("Nueva venta", "El stock luego de la venta quedará por " +
                    "debajo del mínimo tolerado.\n" +
                    "La cantidad de stock de " + repuesto.getDetalle() + " quedará por de bajo del mínimo establecido " +
                    "(" + repuesto.getStock().getCantMinima() + " " + repuesto.getStock().getUnidadMedida() + ").\n" +
                    "¿Continuar con la venta?");
            if (!confir) {
                return;
            }
        }
        DetalleRetiro detalleRetiro = new DetalleRetiro(null, cantidad, repuesto);
        tablaDetallesVentaController.addDetalle(detalleRetiro);
        btnPagar.setDisable(true);
        actualizarLabelTotal();
    }

    @FXML
    private void emitirNotaRetiro(ActionEvent event) {
        if (tablaDetallesVentaController.getDetalles().isEmpty()) {
            Alertas.aviso("Emisión nota de retiro", "Debe cargar productos para la venta.");
            return;
        }

        File file;
        if (checkRutaPredeterminada.isSelected()) {
            file = new File("C:\\Users\\Usuario\\Desktop\\nota retiro.txt");
        } else {
            file = SimpleDialogs.selectorRuta(event, "Seleccione donde guardar la nora de retiro",
                    "nota retiro.txt",
                    new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        }
        if (file == null) {
            return;
        }

        try {
            GeneradorNotaRetiroTXT.generaNotaRetiro(tablaDetallesVentaController.getDetalles(), file);
            if (checkImprimirNota.isSelected()) {
                Impresor.imprimirConSistema(file);
            }
            btnPagar.setDisable(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void pagar() {
        NotaRetiro notaRetiro = new NotaRetiro(null, tablaDetallesVentaController.getDetalles());
        VentaRepuesto ventaRepuesto = new VentaRepuesto(null, notaRetiro, new ArrayList<>());
        Optional<VentaRepuesto> optional = navigator.openModal(
                Views.PAGO, "Pago", ventaRepuesto);
        if (optional.isPresent()) {
            limpiarLista();
            todosRepuestos();
        }
    }

    private void actualizarLabelTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleRetiro d : tablaDetallesVentaController.getDetalles()) {
            total = total.add(d.getSubTotal());
        }
        labelTotal.setText("TOTAL: $ " + total);
    }

    private void seteaEstiloTabla() {
        tableRepuestos.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(RepuestoRowViewModel item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else if (item.getCantidad() <= item.getCantidadMinima()) {
                    setStyle("-fx-background-color: lightcoral;");
                } else {
                    //limpieza de estilo
                    setStyle("");
                }
            }
        });
    }

    private void configColumnas() {
        colCodBarra.setCellValueFactory(new PropertyValueFactory<>("coBarra"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    private void llenarTablaRepuestos(List<Repuesto> repuestos) {
        obsListRepuestoVM.clear();
        for (Repuesto r : repuestos) {
            obsListRepuestoVM.add(new RepuestoRowViewModel(r));
        }
    }

}
