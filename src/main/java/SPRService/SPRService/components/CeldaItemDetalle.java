package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public abstract class CeldaItemDetalle<T extends ItemDetalleViewModel> extends ListCell<T> {

    protected VBox container;
    protected Label lblTipo;
    protected Label lblSubTotal;

    public CeldaItemDetalle() {
        inicializarUI();
    }

    private void inicializarUI() {
        container = new VBox(5);
        container.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4; -fx-background-radius: 5;");

        lblSubTotal = new Label();
        lblSubTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

        lblTipo = new Label();
        lblTipo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        container.getChildren().addAll(lblTipo, lblSubTotal);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            actualizarContenido(item);
            setGraphic(container);
        }
    }

    // abstracto para personalización específica
    protected abstract void actualizarContenido(T item);

    // común para actualizar datos básicos
    protected void actualizarDatosBasicos(T item) {
        lblTipo.setText(item.getTipo());
        lblSubTotal.setText(item.getTipo());
    }

}
