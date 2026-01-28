package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaPago;
import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.navigation.*;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.celdas.ItemPagoViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetalleVentaController implements Initializable, DataReceiver<VentaRepuesto>,
        ModalController<VentaRepuesto> {

    private VentaRepuesto ventaRepuesto;
    private VentaRepuesto ventaParaDevolver;
    private ObservableList<ItemDetalleViewModel> itemsDetalles = FXCollections.observableArrayList();
    private ObservableList<ItemPagoViewModel> itemsPagos = FXCollections.observableArrayList();
    private ObservableList<String> itemsContacto = FXCollections.observableArrayList();
    private final Navigator navigator;

    @Inject
    public DetalleVentaController(AppCoordinator appCoordinator) {
        this.navigator = appCoordinator.getMainNavigator();
    }

    @FXML
    private ListView<ItemDetalleViewModel> listaDetalles;
    @FXML
    private ListView<ItemPagoViewModel> listaPagos;
    @FXML
    private ListView<String> listaContactosCliente;
    @FXML
    private Label labelCodVenta, labelFechaVenta, labelMontoTotal, labelEstadoVenta, labelMontoFaltante,
            labelClienteDni, labelClienteNombre;
    @FXML
    private Button butAgregarPago;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarListas();
    }

    @Override
    public void receiveData(VentaRepuesto data) {
        if (data != null) {
            this.ventaRepuesto = data;
            List<ItemDetalleRetiroViewModel> detalles = data.getNotaRetiro().getDetallesRetiroList().stream()
                    .map(ItemDetalleRetiroViewModel::new).toList();
            itemsDetalles.clear();
            itemsDetalles.addAll(detalles);
            //todo: remplazar la carga de listas observables grandes.
            // Esto notifica por cada iteración. Usar .clear() y .addAll()
//            data.getNotaRetiro().getDetallesRetiroList().forEach(d ->
//                    itemsDetalles.add(new ItemDetalleRetiroViewModel(d)));

            List<ItemPagoViewModel> pagos = data.getPagos().stream()
                    .map(ItemPagoViewModel::new).toList();
            itemsPagos.setAll(pagos);

            cargarDatosCliente();

            butAgregarPago.setDisable(!data.getEstadoVenta().equals(EstadoVentaRepuesto.PENDIENTE_PAGO));
            cargarLabels();
        }
    }

    @Override
    public Optional<VentaRepuesto> getResult() {
        return Optional.ofNullable(this.ventaParaDevolver);
    }

    @FXML
    private void irPago() {
        Optional<VentaRepuesto> result = navigator.openModal(Views.PAGO,
                "Agregar pago", this.ventaRepuesto);
        result.ifPresent(venta -> {
            receiveData(venta);
            this.ventaParaDevolver = this.ventaRepuesto;
        });
    }

    private void configurarListas() {
        listaPagos.setItems(itemsPagos);
        listaDetalles.setItems(itemsDetalles);
        listaContactosCliente.setItems(itemsContacto);

        listaPagos.setCellFactory(c -> new CeldaPago());
        listaDetalles.setCellFactory(new ItemCellFactory().setMostrarBotonEliminar(false));

        String css = getClass().getResource("/styles/celdasDetalles.css").toExternalForm();
        listaDetalles.getStylesheets().add(css);
        String css2 = getClass().getResource("/styles/celdaPago.css").toExternalForm();
        listaPagos.getStylesheets().add(css2);
    }

    private void cargarLabels() {
        labelCodVenta.setText(String.valueOf(this.ventaRepuesto.getId()));
        labelFechaVenta.setText(this.ventaRepuesto.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        labelMontoTotal.setText("$ " + this.ventaRepuesto.getMontoTotal());
        labelEstadoVenta.setText(this.ventaRepuesto.getEstadoVenta().toString());
        labelMontoFaltante.setText("$ " + this.ventaRepuesto.getMontoFaltante());
    }

    private void cargarDatosCliente() {
        itemsContacto.clear();
        if (this.ventaRepuesto.getCliente() != null) {
            itemsContacto.addAll(this.ventaRepuesto.getCliente().getContactosCliente().getEmailSet());
            itemsContacto.addAll(this.ventaRepuesto.getCliente().getContactosCliente().getNroTelefonoSet());
            labelClienteDni.setText(this.ventaRepuesto.getCliente().getDni());
            labelClienteNombre.setText(this.ventaRepuesto.getCliente().getNombre() + " " + this.ventaRepuesto.getCliente().getApellido());
        }
    }
}
