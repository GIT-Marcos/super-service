package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.function.Consumer;

public class ItemCellFactory implements Callback<ListView<ItemDetalleViewModel>, ListCell<ItemDetalleViewModel>> {

    private Consumer<ItemDetalleViewModel> onEliminarItem;
    private boolean mostrarBotonEliminar = true;

    public ItemCellFactory() {
    }

    public ItemCellFactory(Consumer<ItemDetalleViewModel> onEliminarItem) {
        this.onEliminarItem = onEliminarItem;
    }

    @Override
    public ListCell<ItemDetalleViewModel> call(ListView<ItemDetalleViewModel> param) {
        return new ListCell<ItemDetalleViewModel>() {

            // Las celdas concretas ahora deben ser instancias de CeldaItemDetalle
            // para poder acceder a setBotonEliminarVisible.
            private final CeldaItemDetalleRetiro celdaDetalleRetiro = new CeldaItemDetalleRetiro(onEliminarItem);
            private final CeldaItemTrabajo celdaTrabajo = new CeldaItemTrabajo(onEliminarItem);

            @Override
            protected void updateItem(ItemDetalleViewModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // *** APLICAR LA VISIBILIDAD A AMBAS CELDAS ANTES DE USARLAS ***
                    celdaDetalleRetiro.setBotonEliminarVisible(mostrarBotonEliminar);
                    celdaTrabajo.setBotonEliminarVisible(mostrarBotonEliminar);
                    // *************************************************************

                    if (item instanceof ItemDetalleRetiroViewModel) {
                        celdaDetalleRetiro.updateItem((ItemDetalleRetiroViewModel) item, false);
                        setGraphic(celdaDetalleRetiro.getGraphic());
                    } else if (item instanceof ItemTrabajoViewModel) {
                        celdaTrabajo.updateItem((ItemTrabajoViewModel) item, false);
                        setGraphic(celdaTrabajo.getGraphic());
                    }
                }
            }
        };
    }

    public void setOnEliminarItem(Consumer<ItemDetalleViewModel> onEliminarItem) {
        this.onEliminarItem = onEliminarItem;
    }

    public ItemCellFactory setMostrarBotonEliminar(boolean mostrarBotonEliminar) {
        this.mostrarBotonEliminar = mostrarBotonEliminar;
        return this; // Permite el encadenamiento de métodos
    }
}