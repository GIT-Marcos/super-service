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

    public ItemCellFactory() {
    }

    public ItemCellFactory(Consumer<ItemDetalleViewModel> onEliminarItem) {
        this.onEliminarItem = onEliminarItem;
    }

    @Override
    public ListCell<ItemDetalleViewModel> call(ListView<ItemDetalleViewModel> param) {
        return new ListCell<ItemDetalleViewModel>() {

            private final CeldaItemDetalleRetiro celdaDetalleRetiro = new CeldaItemDetalleRetiro(onEliminarItem);
            private final CeldaItemTrabajo celdaTrabajo = new CeldaItemTrabajo(onEliminarItem);

            @Override
            protected void updateItem(ItemDetalleViewModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Polimorfismo: seleccionar celda según tipo
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
}