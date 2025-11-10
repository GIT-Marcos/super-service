package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public abstract class CeldaItemDetalle<T extends ItemDetalleViewModel> extends ListCell<T> {

    protected HBox mainContainer;
    protected VBox container;
    protected Label lblTipo;
    protected Label lblSubTotal;
    protected Button btnEliminar;

    private Consumer<? super T> onEliminarItem;

    public CeldaItemDetalle() {
        inicializarUI();
    }

    public CeldaItemDetalle(Consumer<? super T> onEliminarItem) {  // ← Cambio aquí
        this.onEliminarItem = onEliminarItem;
        inicializarUI();
    }

    private void inicializarUI() {
        // Contenedor principal horizontal
        mainContainer = new HBox();
        mainContainer.getStyleClass().add("celda-item-container");
        mainContainer.setAlignment(Pos.CENTER_LEFT);
        mainContainer.setSpacing(10);

        // Contenedor de información (izquierda)
        container = new VBox();
        container.setSpacing(5);
        container.setPadding(new Insets(0, 0, 0, 5));

        lblSubTotal = new Label();
        lblSubTotal.getStyleClass().add("celda-label-subtotal");

        lblTipo = new Label();
        lblTipo.getStyleClass().add("celda-label-tipo");

        container.getChildren().addAll(lblTipo, lblSubTotal);

        // Espaciador para empujar el botón a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botón eliminar
        btnEliminar = new Button("✕");
        btnEliminar.getStyleClass().add("btn-eliminar-item");
        btnEliminar.setOnAction(e -> {
            T item = getItem();
            if (item != null && onEliminarItem != null) {
                onEliminarItem.accept(item);
            }
        });

        mainContainer.getChildren().addAll(container, spacer, btnEliminar);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            actualizarContenido(item);
            setGraphic(mainContainer);
        }
    }

    protected abstract void actualizarContenido(T item);

    protected void actualizarDatosBasicos(T item) {
        lblTipo.setText(item.getTipo());
        lblSubTotal.setText("Sub-total: $" + item.getSubTotal().toString());
    }

    public void setOnEliminarItem(Consumer<T> onEliminarItem) {
        this.onEliminarItem = onEliminarItem;
    }
}