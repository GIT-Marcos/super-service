package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CeldaItemTrabajo extends CeldaItemDetalle<ItemTrabajoViewModel> {

    private Label lblDescripcion;

    public CeldaItemTrabajo() {
        super();
        lblDescripcion = new Label();

        HBox header = new HBox(10);
        header.getChildren().addAll(lblTipo, lblDescripcion);

        container.getChildren().clear();
        container.getChildren().addAll(header, lblSubTotal, lblDescripcion);
    }

    @Override
    protected void actualizarContenido(ItemTrabajoViewModel item) {
        actualizarDatosBasicos(item);

        lblDescripcion.setText(item.getDescripcionTrabajo());

//        lblEstado.setText(item.isDisponible() ? "●" : "○");
//        lblEstado.setStyle("-fx-font-size: 16px; -fx-text-fill: " +
//                (item.isDisponible() ? "#4CAF50" : "#999") + ";");

        // Estilo específico para servicios
        container.setStyle(container.getStyle() + "-fx-border-color: #FF9800; -fx-border-width: 0 0 0 4;");
    }

}
