package SPRService.SPRService.components;

import SPRService.SPRService.util.ServicioNavegador;
import SPRService.SPRService.viewModels.celdas.ItemDatoContactoViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDatoContactoViewModel.TipoContacto;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

public class CeldaDatoContacto extends ListCell<ItemDatoContactoViewModel> {

    private final HBox container;
    private final HBox botonesBox;
    private final Label lblIcono;
    private final Label lblTipo;
    private final Label lblValor;
    private final Button btnAbrir;
    private final Button btnWhatsApp;
    private final Button btnCopiar;
    private final Button btnEliminar;
    private final Region spacer;

    public CeldaDatoContacto() {
        super();

        // === Etiquetas ===
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

        // === Botón Abrir (email/teléfono) ===
        btnAbrir = new Button();
        btnAbrir.getStyleClass().add("btn-accion-cell");
        btnAbrir.setOnAction(e -> abrirContacto());

        // === Botón WhatsApp (solo teléfonos) ===
        btnWhatsApp = new Button("💬");
        btnWhatsApp.getStyleClass().addAll("btn-accion-cell", "btn-whatsapp");
        btnWhatsApp.setTooltip(new Tooltip("Abrir en WhatsApp"));
        btnWhatsApp.setOnAction(e -> abrirWhatsApp());

        // === Botón Copiar ===
        btnCopiar = new Button("📋");
        btnCopiar.getStyleClass().add("btn-accion-cell");
        btnCopiar.setTooltip(new Tooltip("Copiar al portapapeles"));
        btnCopiar.setOnAction(e -> copiarAlPortapapeles());

        // === Botón Eliminar ===
        btnEliminar = new Button("✕");
        btnEliminar.getStyleClass().add("btn-eliminar-cell");
        btnEliminar.setTooltip(new Tooltip("Eliminar"));
        btnEliminar.setOnAction(e -> {
            ItemDatoContactoViewModel item = getItem();
            if (item != null) {
                getListView().getItems().remove(item);
            }
        });

        // === Contenedor de botones ===
        botonesBox = new HBox(4);
        botonesBox.setAlignment(Pos.CENTER_RIGHT);

        // === Contenedor principal ===
        container = new HBox(8);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));
        container.getChildren().addAll(lblIcono, lblTipo, lblValor, spacer, botonesBox);
        container.getStyleClass().add("dato-contacto-cell");

        // Ocultar botones por defecto
        botonesBox.setVisible(false);
        container.setOnMouseEntered(e -> botonesBox.setVisible(true));
        container.setOnMouseExited(e -> botonesBox.setVisible(false));
    }

    // ==================== ACCIONES ====================

    private void abrirContacto() {
        ItemDatoContactoViewModel item = getItem();
        if (item == null) return;

        if (item.getTipo() == TipoContacto.EMAIL) {
            ServicioNavegador.abrirEmail(item.getValor());
        } else {
            ServicioNavegador.abrirTelefono(item.getValor());
        }
    }

    private void abrirWhatsApp() {
        ItemDatoContactoViewModel item = getItem();
        if (item == null) return;

        ServicioNavegador.abrirWhatsApp(item.getValor());
    }

    private void copiarAlPortapapeles() {
        ItemDatoContactoViewModel item = getItem();
        if (item == null) return;

        ClipboardContent content = new ClipboardContent();
        content.putString(item.getValor());
        Clipboard.getSystemClipboard().setContent(content);

        String textoOriginal = btnCopiar.getText();
        btnCopiar.setText("✔");
        btnCopiar.setDisable(true);

        PauseTransition pausa = new PauseTransition(Duration.seconds(1));
        pausa.setOnFinished(ev -> {
            btnCopiar.setText(textoOriginal);
            btnCopiar.setDisable(false);
        });
        pausa.play();
    }

    // ==================== ACTUALIZACIÓN ====================

    @Override
    protected void updateItem(ItemDatoContactoViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        lblIcono.setText(item.getTipo().getIcono());
        lblTipo.setText(item.getTipo().getNombre() + ":");
        lblValor.setText(item.getValor());

        // ★ Configurar botones según el tipo
        botonesBox.getChildren().clear();

        if (item.getTipo() == TipoContacto.EMAIL) {
            btnAbrir.setText("✉");
            btnAbrir.setTooltip(new Tooltip("Enviar correo a " + item.getValor()));
            botonesBox.getChildren().addAll(btnAbrir, btnCopiar, btnEliminar);
        } else {
            btnAbrir.setText("📞");
            btnAbrir.setTooltip(new Tooltip("Llamar a " + item.getValor()));
            botonesBox.getChildren().addAll(btnAbrir, btnWhatsApp, btnCopiar, btnEliminar);
        }

        // Estilo según tipo
        container.getStyleClass().removeAll("email-cell", "telefono-cell");

        if (item.getTipo() == TipoContacto.EMAIL) {
            container.getStyleClass().add("email-cell");
        } else {
            container.getStyleClass().add("telefono-cell");
        }

        setGraphic(container);
    }
}