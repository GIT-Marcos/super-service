package SPRService.SPRService.components;

import SPRService.SPRService.entities.DetalleRetiro;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class CeldaDetalleRetiro extends ListCell<DetalleRetiro> {

    private final BorderPane mainPane;
    private final Label barcodeLabel;
    private final Label nombreLabel;
    private final Label marcaLabel;
    private final Label cantidadLabel;
    private final Label unidadLabel;

    public CeldaDetalleRetiro() {
        super();

        // --- LADO IZQUIERDO: Código de Barras ---
        barcodeLabel = new Label();
        // Le añadimos una clase CSS para darle el estilo de rotación y fuente
        barcodeLabel.getStyleClass().add("barcode-label");

        // Usamos un StackPane para centrar correctamente el texto rotado
        StackPane barcodeContainer = new StackPane(barcodeLabel);
        barcodeContainer.setMinWidth(40); // Ancho para la columna del código
        barcodeContainer.setAlignment(Pos.CENTER);

        // --- CENTRO: Información del producto ---
        nombreLabel = new Label();
        nombreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nombreLabel.getStyleClass().add("nombre-producto");

        marcaLabel = new Label();
        marcaLabel.setTextFill(Color.GRAY);
        marcaLabel.setFont(Font.font(null, FontWeight.SEMI_BOLD, FontPosture.ITALIC, 12));
        marcaLabel.getStyleClass().add("marca-producto");

        VBox infoBox = new VBox(5, nombreLabel, marcaLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // --- LADO DERECHO: Cantidad ---
        cantidadLabel = new Label();
        cantidadLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        cantidadLabel.setTextFill(Color.web("#005b96"));
        cantidadLabel.getStyleClass().add("cantidad-label");

        unidadLabel = new Label();
        unidadLabel.setTextFill(Color.DARKSLATEGRAY);
        unidadLabel.getStyleClass().add("unidad-label");

        VBox cantidadBox = new VBox(0, cantidadLabel, unidadLabel);
        cantidadBox.setAlignment(Pos.CENTER_RIGHT);

        // --- Panel Principal ---
        mainPane = new BorderPane();
        // CAMBIO: Usamos el nuevo contenedor para el label
        mainPane.setLeft(barcodeContainer);
        mainPane.setCenter(infoBox);
        mainPane.setRight(cantidadBox);

        mainPane.setPadding(new Insets(2));
        BorderPane.setMargin(barcodeContainer, new Insets(0, 10, 0, 0));
        BorderPane.setMargin(infoBox, new Insets(5, 5, 5, 5));

        mainPane.getStyleClass().add("card-pane");
    }

    @Override
    protected void updateItem(DetalleRetiro item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            barcodeLabel.setText(String.valueOf(item.getRepuesto().getCodBarra()));
            nombreLabel.setText(item.getRepuesto().getDetalle());
            marcaLabel.setText(item.getRepuesto().getMarca());
            cantidadLabel.setText(String.valueOf(item.getCantidadRetirada()));
            unidadLabel.setText(item.getRepuesto().getStock().getUnidadMedida());

            setGraphic(mainPane);
        }
    }

}
