package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaOperacionUniversal;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.viewModels.celdas.ItemOperacionViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class DetalleVehiculoController implements Initializable, DataReceiver<Vehiculo> {

    private ObservableList<ItemOperacionViewModel> items = FXCollections.observableArrayList();

    @FXML
    private Label lblPatente, lblColor, lblNroChasis, lblNroMotor, lblMarca, lblModelo, lblAnio, lblCilindrada,
            lblFechaRegistro;
    @FXML
    private ImageView imgMarca;
    @FXML
    private ListView<ItemOperacionViewModel> lvServices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarLista();
    }

    @Override
    public void receiveData(Vehiculo data) {
        if (data != null) {
            cargarLabels(data);
            cargarLista(data);
        }
    }

    @FXML
    private void verDetalle() {

    }

    @FXML
    private void nuevoService() {

    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    private void configurarLista() {
        lvServices.setItems(items);
        lvServices.setCellFactory(f -> new CeldaOperacionUniversal());
        String css = getClass().getResource("/styles/celda-operacion.css").toExternalForm();
        lvServices.getStylesheets().add(css);
    }

    private void cargarLabels(Vehiculo data) {
        lblPatente.setText(data.getPatente());
        lblColor.setText(data.getColor());
        if (!data.getNroChasis().isEmpty()) lblNroChasis.setText(data.getNroChasis());
        if (!data.getNroMotor().isEmpty()) lblNroMotor.setText(data.getNroMotor());
        lblMarca.setText(data.getModeloVehiculo().getMarcaVehiculo().getNombreMarca());
        lblModelo.setText(data.getModeloVehiculo().getNombreModelo());
        lblAnio.setText(data.getModeloVehiculo().getAnio().toString());
        lblCilindrada.setText(data.getModeloVehiculo().getCilindrada() + "cc.");
        lblFechaRegistro.setText(data.getFechaRegistro().toString());
        InputStream stream = getClass().getResourceAsStream(data.getModeloVehiculo().getMarcaVehiculo().getRutaLogo());
        if (stream != null) {
            Image img = new Image(stream);
            imgMarca.setImage(img);
        }
    }

    private void cargarLista(Vehiculo data) {

    }
}
