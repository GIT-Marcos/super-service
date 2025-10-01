package SPRService.SPRService.controllers;

import SPRService.SPRService.viewModels.DetalleVentaVM;
import SPRService.SPRService.navigation.*;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import SPRService.SPRService.viewModels.tablas.PagosVMtabla;
import SPRService.SPRService.components.TablaDetallesVentaController;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetalleVentaController implements Initializable, DataReceiver<VentaRepuesto>,
        ModalController<VentaRepuesto> {

    private VentaRepuesto ventaRepuesto;
    // lo mismo que en el controlador de pago con esto
    private VentaRepuesto ventaParaDevolver;
    private ObservableList<DetalleVentaVM> obsListDetalleVM = FXCollections.observableArrayList();
    private ObservableList<PagosVMtabla> obsListPagoVM = FXCollections.observableArrayList();
    private final Navigator navigator;

    @Inject
    public DetalleVentaController(AppCoordinator appCoordinator) {
        this.navigator = appCoordinator.getMainNavigator();
    }

    @FXML
    private TablaDetallesVentaController tablaDetallesVentaController;
    @FXML
    private TableView<PagosVMtabla> tablaPagos;
    @FXML
    private TableColumn<PagosVMtabla, String> colFecha, colMonto, colMetodo, colTarjeta, colBanco, colDto, colRef,
            colUltimos;
    @FXML
    private Label labelCodVenta, labelFechaVenta, labelMontoTotal, labelEstadoVenta, labelMontoFaltante;
    @FXML
    private Button butAgregarPago;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configColumnas();
        tablaPagos.setItems(this.obsListPagoVM);
    }

    @Override
    public void receiveData(VentaRepuesto data) {
        if (data != null) {
            this.ventaRepuesto = data;
            for (DetalleRetiro d : data.getNotaRetiro().getDetallesRetiroList()) {
                obsListDetalleVM.add(new DetalleVentaVM(d));
            }
            tablaDetallesVentaController.setDetalles(data.getNotaRetiro().getDetallesRetiroList());
            this.obsListPagoVM.clear();
            for (Pago p : data.getPagosList()) {
                this.obsListPagoVM.add(new PagosVMtabla(p));
            }
            if (data.getEstadoVenta().equals(EstadoVentaRepuesto.PENDIENTE_PAGO)) {
                butAgregarPago.setDisable(false);
            } else {
                butAgregarPago.setDisable(true);
            }
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
        if (result.isPresent()) {
            this.receiveData(result.get());
            this.ventaParaDevolver = result.get();
        }
    }

    private void configColumnas() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoPago"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colTarjeta.setCellValueFactory(new PropertyValueFactory<>("tarjetaPago"));
        colBanco.setCellValueFactory(new PropertyValueFactory<>("bancoPago"));
        colDto.setCellValueFactory(new PropertyValueFactory<>("descuentoPago"));
        colRef.setCellValueFactory(new PropertyValueFactory<>("nroRefPago"));
        colUltimos.setCellValueFactory(new PropertyValueFactory<>("ultimos4Pago"));
    }

    private void cargarLabels() {
        labelCodVenta.setText(String.valueOf(this.ventaRepuesto.getId()));
        labelFechaVenta.setText(String.valueOf(this.ventaRepuesto.getFechaVenta()));
        labelMontoTotal.setText("$ " + this.ventaRepuesto.getMontoTotal());
        labelEstadoVenta.setText(this.ventaRepuesto.getEstadoVenta().toString());
        labelMontoFaltante.setText("$ " + this.ventaRepuesto.getMontoFaltante());
    }
}
