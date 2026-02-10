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
    private final Label estadoLabel; // Nuevo label para mostrar estado

    public CeldaCliente() {
        super();

        // --- LADO IZQUIERDO: DNI ---
        dniLabel = new Label();
        dniLabel.getStyleClass().add("dni-label");
        dniLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 11));

        // Usamos un StackPane para centrar correctamente el texto rotado
        StackPane dniContainer = new StackPane(dniLabel);
        dniContainer.setMinWidth(40);
        dniContainer.setAlignment(Pos.CENTER);

        // --- CENTRO: Nombre completo del cliente ---
        nombreCompletoLabel = new Label();
        nombreCompletoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nombreCompletoLabel.getStyleClass().add("nombre-completo-cliente");

        // Label de estado (opcional, para mostrar texto "INACTIVO")
        estadoLabel = new Label();
        estadoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        estadoLabel.getStyleClass().add("estado-label");

        HBox infoBox = new HBox(10);
        infoBox.getChildren().addAll(nombreCompletoLabel, estadoLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // --- LADO DERECHO: ID del cliente ---
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
            // Limpiar clases de estado
            mainPane.getStyleClass().removeAll("cliente-activo", "cliente-inactivo");
        } else {
            // Actualizar DNI
            dniLabel.setText(formatDNI(item.getDni()));

            // Actualizar nombre completo
            String nombreCompleto = item.getNombre() + " " + item.getApellido();
            nombreCompletoLabel.setText(nombreCompleto);

            // Mostrar ID
            if (item.getId() != null) {
                idLabel.setText("#" + item.getId());
            } else {
                idLabel.setText("");
            }

            // --- MANEJO DEL ESTADO ACTIVO/INACTIVO ---
            // Limpiar clases previas
            mainPane.getStyleClass().removeAll("cliente-activo", "cliente-inactivo");
            estadoLabel.getStyleClass().removeAll("estado-activo", "estado-inactivo");

            // Aplicar clase según estado
            if (item.getActivo() != null && item.getActivo()) {
                mainPane.getStyleClass().add("cliente-activo");
                estadoLabel.setText(""); // No mostrar texto para activos
                estadoLabel.getStyleClass().add("estado-activo");
            } else {
                mainPane.getStyleClass().add("cliente-inactivo");
                estadoLabel.setText("INACTIVO");
                estadoLabel.getStyleClass().add("estado-inactivo");
            }

            setGraphic(mainPane);
        }
    }

    /**
     * Formatea el DNI con puntos de miles
     */
    private String formatDNI(String dni) {
        if (dni == null || dni.isEmpty()) return "";

        try {
            long dniNum = Long.parseLong(dni);
            return String.format("%,d", dniNum).replace(",", ".");
        } catch (NumberFormatException e) {
            return dni;
        }
    }
}