package SPRService.SPRService.components;

import SPRService.SPRService.entities.Vehiculo;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.InputStream;

public class CeldaVehiculo extends ListCell<Vehiculo> {

    private HBox container;
    private ImageView imgLogo;
    private VBox infoBox;
    private HBox filaSuperior;
    private HBox filaInferior;

    private Label lblMarca;
    private Label lblModelo;
    private Label lblAnio;
    private Label lblCilindrada;
    private Label lblPatente;
    private Label lblColor;

    public CeldaVehiculo() {
        inicializarUI();
    }

    private void inicializarUI() {
        container = new HBox();
        container.getStyleClass().add("celda-vehiculo-container");
        container.setAlignment(Pos.CENTER_LEFT);

        imgLogo = new ImageView();
        imgLogo.getStyleClass().add("celda-vehiculo-logo");
        imgLogo.setFitWidth(60);
        imgLogo.setFitHeight(60);
        imgLogo.setPreserveRatio(true);

        infoBox = new VBox();
        infoBox.getStyleClass().add("celda-vehiculo-info-box");

        crearFilaSuperior();
        crearFilaInferior();
        infoBox.getChildren().addAll(filaSuperior, filaInferior);
        container.getChildren().addAll(imgLogo, infoBox);
    }

    private void crearFilaSuperior() {
        filaSuperior = new HBox();
        filaSuperior.getStyleClass().add("celda-vehiculo-fila-superior");

        lblMarca = new Label();
        lblMarca.getStyleClass().add("celda-vehiculo-marca");

        lblModelo = new Label();
        lblModelo.getStyleClass().add("celda-vehiculo-modelo");

        lblAnio = new Label();
        lblAnio.getStyleClass().add("celda-vehiculo-anio");

        lblCilindrada = new Label();
        lblCilindrada.getStyleClass().add("celda-vehiculo-cilindrada");

        filaSuperior.getChildren().addAll(lblMarca, lblModelo, lblAnio, lblCilindrada);
    }

    private void crearFilaInferior() {
        filaInferior = new HBox();
        filaInferior.getStyleClass().add("celda-vehiculo-fila-inferior");

        lblPatente = new Label();
        lblPatente.getStyleClass().add("celda-vehiculo-patente");

        lblColor = new Label();
        lblColor.getStyleClass().add("celda-vehiculo-color");

        filaInferior.getChildren().addAll(lblPatente, lblColor);
    }

    @Override
    protected void updateItem(Vehiculo vehiculo, boolean empty) {
        super.updateItem(vehiculo, empty);

        if (empty || vehiculo == null) {
            setGraphic(null);
        } else {
            actualizarContenido(vehiculo);
            setGraphic(container);
        }
    }

    private void actualizarContenido(Vehiculo vehiculo) {
        // Logo
        cargarLogo(vehiculo.getModeloVehiculo().getMarcaVehiculo().getRutaLogo());

        // Fila superior
        lblMarca.setText(vehiculo.getModeloVehiculo().getMarcaVehiculo().getNombreMarca());
        lblModelo.setText(vehiculo.getModeloVehiculo().getNombreModelo());
        lblAnio.setText(String.valueOf(vehiculo.getModeloVehiculo().getAnio()));
        lblCilindrada.setText(formatearCilindrada(vehiculo.getModeloVehiculo().getCilindrada()));

        // Fila inferior
        lblPatente.setText(vehiculo.getPatente());
        lblColor.setText(vehiculo.getColor());
    }

    private void cargarLogo(String rutaLogo) {
        try {
            InputStream stream = getClass().getResourceAsStream(rutaLogo);
            if (stream != null) {
                Image logo = new Image(stream);
                imgLogo.setImage(logo);
            } else {
                InputStream defaultStream = getClass().getResourceAsStream("/imgs/logos/car.png");
                if (defaultStream != null) {
                    imgLogo.setImage(new Image(defaultStream));
                }
            }
        } catch (Exception e) {
            imgLogo.setImage(null);
            System.err.println("Error cargando logo: " + rutaLogo);
        }
    }

    private String formatearCilindrada(Double cilindrada) {
        if (cilindrada == null) return "N/A";

        if (cilindrada >= 1000) {
            return String.format("%.1fL", cilindrada / 1000);
        } else {
            return String.format("%.0fcc", cilindrada);
        }
    }
}