package SPRService.SPRService.controllers;

import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.GeneradorTXT;
import SPRService.SPRService.util.generadores.Impresor;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class CargarVentaController implements Initializable {

    private final Navigator navigator;
    private List<DetalleRetiro> detallesCargados = new ArrayList<>();
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();
    private Cliente cliente;

    @FXML
    private ListView<ItemDetalleViewModel> lista;
    @FXML
    private Label lblTotal, lblDniCliente, lblNombreCliente;
    @FXML
    private CheckBox chkGuardarRuta, chkImprimir, chConsumidorFinal;

    @Inject
    public CargarVentaController(AppCoordinator coordinator) {
        this.navigator = coordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lista.setItems(items);
        lista.setCellFactory(new ItemCellFactory(this::eliminarItem));
        String css = getClass().getResource("/styles/celdasDetalles.css").toExternalForm();
        lista.getStylesheets().add(css);
    }

    @FXML
    public void limpiarLista() {
        detallesCargados.clear();
        items.clear();
        calcularTotal();
    }

    @FXML
    private void seleccionarCliente() {
        Optional<Cliente> result = navigator.openModal(Views.AGREGAR_CLIENTE_SERVICE,
                "Agregar cliente", null);
        result.ifPresent(r -> {
            this.cliente = r;
            lblDniCliente.setText(r.getDni());
            lblNombreCliente.setText(r.getNombre() + " " + r.getApellido());
            chConsumidorFinal.setSelected(false);
        });
    }

    @FXML
    private void checkConsumidorFinal() {
        this.cliente = null;
        lblNombreCliente.setText("---");
        lblDniCliente.setText("---");
    }

    @FXML
    public void abrirModalAgregarProducto() {
        Optional<DetalleRetiro> result = navigator.openModal(Views.AGREGAR_REPUESTO, "Agregar repuesto a venta",
                detallesCargados);
        result.ifPresent(detalle -> {
            detallesCargados.add(detalle);
            items.add(new ItemDetalleRetiroViewModel(detalle));
            calcularTotal();
        });
    }

    @FXML
    public void abrirModalPago(ActionEvent event) {
        if (detallesCargados.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Cargar venta",
                    "Debe agregar productos a la venta para poder seguir con el pago.");
            return;
        }
        if (!chConsumidorFinal.isSelected() && this.cliente == null) {
            NotificationHelper.mostrarAdvertencia("Sin cliente",
                    "Si no es consumidor final se debe agregar un cliente.");
            return;
        }

        if (SimpleDialogs.confirmacion("Cargar venta", "¿Desea crear un archivo de texto de la nota de retiro?")) {
            guardarNota(event);
        }

        NotaRetiro nota = new NotaRetiro(null, NotaRetiro.TipoUsoRetiro.VENTA, detallesCargados);
        VentaRepuesto venta = new VentaRepuesto(nota);
        venta.asociarCliente(this.cliente);
        Optional<VentaRepuesto> result = navigator.openModal(Views.PAGO, "Pagar", venta);
        result.ifPresent(v -> {
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        });
    }

    private void guardarNota(ActionEvent event) {
        File file;
        if (chkGuardarRuta.isSelected()) {
            file = new File("C:\\Users\\Usuario\\Desktop\\nota retiro.txt");
        } else {
            file = SimpleDialogs.selectorRuta(event, "Seleccione donde quiere guardar la nota",
                    "nota retiro.txt",
                    new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        }
        if (file == null) return;
        GeneradorTXT.generaNotaRetiro(detallesCargados, file);

        if (chkImprimir.isSelected())
            Impresor.imprimirConSistema(file);
    }

    private void eliminarItem(ItemDetalleViewModel item) {
        items.remove(item);
        detallesCargados.remove(((ItemDetalleRetiroViewModel) item).getDetalleRetiro());
        calcularTotal();
    }

    private void calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleRetiro d : detallesCargados) {
            total = total.add(d.getSubTotal());
        }
        lblTotal.setText("$ " + total);
    }
}
