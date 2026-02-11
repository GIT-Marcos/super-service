package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDatoContactoViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class CeldaDatoContacto extends ListCell<ItemDatoContactoViewModel> {

    private final HBox container;
    private final Label lblIcono;
    private final Label lblTipo;
    private final Label lblValor;
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

        btnEliminar = new Button("✕");
        btnEliminar.getStyleClass().add("btn-eliminar-cell");
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
        container.getChildren().addAll(lblIcono, lblTipo, lblValor, spacer, btnEliminar);
        container.getStyleClass().add("dato-contacto-cell");

        // Ocultar botón por defecto, mostrar en hover
        btnEliminar.setVisible(false);
        container.setOnMouseEntered(e -> btnEliminar.setVisible(true));
        container.setOnMouseExited(e -> btnEliminar.setVisible(false));
    }

    @Override
    protected void updateItem(ItemDatoContactoViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Configurar ícono y texto
            lblIcono.setText(item.getTipo().getIcono());
            lblTipo.setText(item.getTipo().getNombre() + ":");
            lblValor.setText(item.getValor());

            // Limpiar estilos anteriores y aplicar nuevo
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


