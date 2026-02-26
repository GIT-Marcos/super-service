package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaOperacionUniversal;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.navigation.*;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.viewModels.celdas.ItemOperacionViewModel;
import SPRService.SPRService.viewModels.celdas.ItemServiceViewModel;
import SPRService.SPRService.viewModels.celdas.ItemVentaViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class OperacionesClienteController implements Initializable, DataReceiver<Cliente> {

    private final ClienteServ clienteServ;
    private final VentaRepuestoServ ventaServ;
    private final ServiceServ serviceServ;
    private final Navigator navigator;
    private Cliente cliente;
    private final ObservableList<ItemOperacionViewModel> obsList = FXCollections.observableArrayList();
    private final SortedList<ItemOperacionViewModel> sortedList = new SortedList<>(obsList);

    @FXML
    private Label lblTituloCliente, lblCantVentas, lblCantServices;
    @FXML
    private ListView<ItemOperacionViewModel> listViewOperaciones;

    @Inject
    public OperacionesClienteController(ClienteServ clienteServ, VentaRepuestoServ ventaServ, ServiceServ serviceServ, AppCoordinator coordinator) {
        this.clienteServ = clienteServ;
        this.ventaServ = ventaServ;
        this.serviceServ = serviceServ;
        this.navigator = coordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortedList.setComparator(Comparator.comparing(ItemOperacionViewModel::getFecha).reversed());
        listViewOperaciones.setItems(sortedList);

        listViewOperaciones.setCellFactory(c -> new CeldaOperacionUniversal(this::detallesOperacion));
        String css = getClass().getResource("/styles/celda-operacion.css").toExternalForm();
        listViewOperaciones.getStylesheets().add(css);
    }

    @Override
    public void receiveData(Cliente data) {
        if (data != null) {
            this.cliente = data;
            lblTituloCliente.setText(data.getDni() + " | " + data.getNombre() + " " + data.getApellido());

            llenarCampos();
        }
    }

    @FXML
    private void verEstadisticas() {
        clienteServ.obtenerEstadisticasCliente(cliente.getId())
                .ifPresent(dto -> navigator.openModal(Views.STATS_CLIENTE, "Estadísticas de cliente", dto));
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    private void detallesOperacion(ItemOperacionViewModel item) {
        if (item == null) return;

        if (item instanceof ItemVentaViewModel) {
            ventaServ.verDetalle(item.getCodigo())
                    .ifPresent(v -> {
                        Optional<VentaRepuesto> huboCambios = navigator.openModal(Views.DETALLE_VENTA, "Detalles de venta", v);
                        huboCambios.ifPresent(vm -> recargarCliente());
                    });
        } else if (item instanceof ItemServiceViewModel) {
            serviceServ.datosParaModificar(item.getCodigo())
                    .ifPresent(s -> {
                        Optional<Service> huboCambios = navigator.openModal(Views.MODIFICAR_SERVICE, "Service de cliente", s);
                        huboCambios.ifPresent(sm -> recargarCliente());
                    });
        }
    }

    private void recargarCliente() {
        clienteServ.verOperacionesConVehiculos(this.cliente.getId())
                .ifPresent(this::receiveData);
    }

    private void llenarCampos() {
        obsList.clear();
        List<ItemServiceViewModel> servVMs = this.cliente.getServices().stream()
                .map(ItemServiceViewModel::new).toList();
        List<ItemVentaViewModel> venVMs = this.cliente.getVentas().stream()
                .map(ItemVentaViewModel::new).toList();
        obsList.addAll(servVMs);
        obsList.addAll(venVMs);

        lblCantVentas.setText(String.valueOf(venVMs.size()));
        lblCantServices.setText(String.valueOf(servVMs.size()));
    }
}
