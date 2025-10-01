package SPRService.SPRService.components;

import SPRService.SPRService.entities.MarcaVehiculo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public class CeldaMarcaVehiculo extends ListCell<MarcaVehiculo> {

    private final HBox contentBox;
    private final ImageView logoImageView;
    private final Label nombreLabel;

    public CeldaMarcaVehiculo() {
        super();

        setPadding(Insets.EMPTY);
        // Inicializar los componentes gráficos una sola vez
        logoImageView = new ImageView();
        logoImageView.setFitWidth(70);
        logoImageView.setFitHeight(70);
        logoImageView.setPreserveRatio(true); // Mantiene la proporción del logo
        nombreLabel = new Label();
        nombreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Usar HBox para alinear logo a la izquierda y texto a la derecha
        contentBox = new HBox(15); // 15px de espacio entre logo y texto
        contentBox.setAlignment(Pos.CENTER_LEFT); // Alinea los elementos verticalmente al centro
        contentBox.setPadding(new Insets(0, 10, 0, 10)); // Margen interior
        contentBox.getChildren().addAll(logoImageView, nombreLabel);
    }

    @Override
    protected void updateItem(MarcaVehiculo marcaVehiculo, boolean empty) {
        super.updateItem(marcaVehiculo, empty);

        if (empty || marcaVehiculo == null) {
            setGraphic(null);
        } else {
            nombreLabel.setText(marcaVehiculo.getNombreMarca());
            try {
                InputStream stream = getClass().getResourceAsStream(marcaVehiculo.getRutaLogo());
                if (stream != null) {
                    Image logo = new Image(stream);
                    logoImageView.setImage(logo);
                } else {
                    // Si no se encuentra el logo poner un placeholder o dejarlo vacío
                    logoImageView.setImage(null);
                }
            } catch (Exception e) {
                logoImageView.setImage(null);
            }
            setGraphic(contentBox);
        }
    }
}
