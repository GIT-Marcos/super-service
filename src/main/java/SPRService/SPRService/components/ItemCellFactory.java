package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ItemCellFactory implements Callback<ListView<ItemDetalleViewModel>, ListCell<ItemDetalleViewModel>> {

    @Override
    public ListCell<ItemDetalleViewModel> call(ListView<ItemDetalleViewModel> param) {
        return new ListCell<ItemDetalleViewModel>() {

            private CeldaItemDetalleRetiro celdaDetalleRetiro = new CeldaItemDetalleRetiro();
            private CeldaItemTrabajo celdaTrabajo = new CeldaItemTrabajo();

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
}
