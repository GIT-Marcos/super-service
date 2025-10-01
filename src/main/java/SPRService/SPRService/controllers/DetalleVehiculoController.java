package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.navigation.DataReceiver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class DetalleVehiculoController implements Initializable, DataReceiver<Vehiculo> {

    @FXML
    private Label lblPatente;
    @FXML
    private Label lblColor;
    @FXML
    private Label lblNroChasis;
    @FXML
    private Label lblNroMotor;
    @FXML
    private Label lblMarca;
    @FXML
    private Label lblModelo;
    @FXML
    private Label lblAnio;
    @FXML
    private Label lblCilindrada;
    @FXML
    private Label lblFechaRegistro;
    @FXML
    private ImageView imgMarca;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void receiveData(Vehiculo data) {
        if (data == null) return;
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
}
