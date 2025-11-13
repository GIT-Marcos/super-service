package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class CeldaItemTrabajo extends CeldaItemDetalle<ItemTrabajoViewModel> {

    private Label lblDescripcion;
    private HBox header;

    public CeldaItemTrabajo() {
        super();
        inicializarComponentes();
    }

    public CeldaItemTrabajo(Consumer<? super ItemTrabajoViewModel> onEliminarItem) {
        super(onEliminarItem);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Agregar clase específica
        container.getStyleClass().add("celda-trabajo-container");

        lblDescripcion = new Label();
        lblDescripcion.getStyleClass().add("celda-trabajo-descripcion");

        lblSubTotal.getStyleClass().add("celda-trabajo-sub-total");

        header = new HBox();
        header.getStyleClass().add("celda-trabajo-header");
        header.getChildren().addAll(lblDescripcion);

        container.getChildren().clear();
        container.getChildren().addAll(lblTipo, header, lblSubTotal);
    }

    @Override
    protected void actualizarContenido(ItemTrabajoViewModel item) {
        actualizarDatosBasicos(item);
        lblDescripcion.setText(item.getDescripcionTrabajo());
    }
}