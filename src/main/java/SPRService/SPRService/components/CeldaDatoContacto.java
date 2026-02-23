package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDatoContactoViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class CeldaDatoContacto extends ListCell<ItemDatoContactoViewModel> {

    private final HBox container;
    private final Label lblIcono;
    private final Label lblTipo;
    private final Label lblValor;
    private final Button btnCopiar;
    private final Button btnEliminar;
    private final Region spacer;

    public CeldaDatoContacto() {
        super();

        // Crear componentes
        lblIcono = new Label();
        lblIcono.getStyleClass().add("cell-icon");
        lblIcono.setMinWidth(25);
        lblIcono.setAlignment(Pos.CENTER);

        lblTipo = new Label();
        lblTipo.getStyleClass().add("cell-tipo");
        lblTipo.setMinWidth(70);

        lblValor = new Label();
        lblValor.getStyleClass().add("cell-valor");

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- Botón copiar ---
        btnCopiar = new Button("📋");
        btnCopiar.getStyleClass().add("btn-copiar-cell");
        btnCopiar.setTooltip(new Tooltip("Copiar al portapapeles"));
        btnCopiar.setOnAction(e -> {
            ItemDatoContactoViewModel item = getItem();
            if (item != null) {
                ClipboardContent content = new ClipboardContent();
                content.putString(item.getValor());
                Clipboard.getSystemClipboard().setContent(content);

                // Feedback visual temporal
                String textoOriginal = btnCopiar.getText();
                btnCopiar.setText("✔");
                btnCopiar.setDisable(true);

                javafx.animation.PauseTransition pausa =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                pausa.setOnFinished(ev -> {
                    btnCopiar.setText(textoOriginal);
                    btnCopiar.setDisable(false);
                });
                pausa.play();
            }
        });

        // --- Botón eliminar ---
        btnEliminar = new Button("✕");
        btnEliminar.getStyleClass().add("btn-eliminar-cell");
        btnEliminar.setTooltip(new Tooltip("Eliminar"));
        btnEliminar.setOnAction(e -> {
            ItemDatoContactoViewModel item = getItem();
            if (item != null) {
                getListView().getItems().remove(item);
            }
        });

        // Contenedor principal
        container = new HBox(8);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));
        container.getChildren().addAll(lblIcono, lblTipo, lblValor, spacer, btnCopiar, btnEliminar);
        container.getStyleClass().add("dato-contacto-cell");

        // Ocultar botones por defecto, mostrar en hover
        btnCopiar.setVisible(false);
        btnEliminar.setVisible(false);
        container.setOnMouseEntered(e -> {
            btnCopiar.setVisible(true);
            btnEliminar.setVisible(true);
        });
        container.setOnMouseExited(e -> {
            btnCopiar.setVisible(false);
            btnEliminar.setVisible(false);
        });
    }

    @Override
    protected void updateItem(ItemDatoContactoViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            lblIcono.setText(item.getTipo().getIcono());
            lblTipo.setText(item.getTipo().getNombre() + ":");
            lblValor.setText(item.getValor());

            container.getStyleClass().removeAll("email-cell", "telefono-cell");

            if (item.getTipo() == ItemDatoContactoViewModel.TipoContacto.EMAIL) {
                container.getStyleClass().add("email-cell");
            } else {
                container.getStyleClass().add("telefono-cell");
            }

            setGraphic(container);
        }
    }
}