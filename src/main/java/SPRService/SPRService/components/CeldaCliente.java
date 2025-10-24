package SPRService.SPRService.components;

import SPRService.SPRService.entities.Cliente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CeldaCliente extends ListCell<Cliente> {

    private final BorderPane mainPane;
    private final Label dniLabel;
    private final Label nombreCompletoLabel;
    private final Label idLabel;

    public CeldaCliente() {
        super();

        // Cargar el CSS directamente en la celda
        this.getStylesheets().add(
                getClass().getResource("/styles/celdaCliente.css").toExternalForm()
        );

        // --- LADO IZQUIERDO: DNI ---
        dniLabel = new Label();
        dniLabel.getStyleClass().add("dni-label");
        dniLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 11));

        // Usamos un StackPane para centrar correctamente el texto rotado
        StackPane dniContainer = new StackPane(dniLabel);
        dniContainer.setMinWidth(40); // Ancho para la columna del DNI
        dniContainer.setAlignment(Pos.CENTER);

        // --- CENTRO: Nombre completo del cliente ---
        nombreCompletoLabel = new Label();
        nombreCompletoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nombreCompletoLabel.getStyleClass().add("nombre-completo-cliente");

        HBox infoBox = new HBox(5);
        infoBox.getChildren().add(nombreCompletoLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // --- LADO DERECHO: ID del cliente (opcional) ---
        idLabel = new Label();
        idLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        idLabel.setTextFill(Color.web("#757575"));
        idLabel.getStyleClass().add("id-cliente");

        // --- Panel Principal ---
        mainPane = new BorderPane();
        mainPane.setLeft(dniContainer);
        mainPane.setCenter(infoBox);
        mainPane.setRight(idLabel);

        mainPane.setPadding(new Insets(8));
        BorderPane.setMargin(dniContainer, new Insets(0, 10, 0, 0));
        BorderPane.setMargin(infoBox, new Insets(5, 5, 5, 5));
        BorderPane.setMargin(idLabel, new Insets(5, 5, 5, 5));

        mainPane.getStyleClass().add("card-pane");
    }

    @Override
    protected void updateItem(Cliente item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            // Actualizar DNI
            dniLabel.setText(formatDNI(item.getDni()));

            // Actualizar nombre completo (nombre + apellido)
            String nombreCompleto = item.getNombre() + " " + item.getApellido();
            nombreCompletoLabel.setText(nombreCompleto);

            // Si el cliente tiene ID, mostrarlo
            if (item.getId() != null) {
                idLabel.setText("#" + item.getId());
            } else {
                idLabel.setText("");
            }

            setGraphic(mainPane);
        }
    }

    /**
     * Formatea el DNI con puntos de miles
     */
    private String formatDNI(String dni) {
        if (dni == null || dni.isEmpty()) return "";

        // Si el DNI es numérico, formatearlo con puntos
        try {
            long dniNum = Long.parseLong(dni);
            return String.format("%,d", dniNum).replace(",", ".");
        } catch (NumberFormatException e) {
            return dni; // Si no es numérico, devolver tal cual
        }
    }
}
