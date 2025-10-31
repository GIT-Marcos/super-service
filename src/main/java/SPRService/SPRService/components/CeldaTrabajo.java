package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.TrabajoViewModelRepuesto;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.Consumer;

public class CeldaTrabajo extends ListCell<TrabajoViewModelRepuesto> {

    private final HBox hbox;
    private final Label descriptionLabel;
    private final Label priceLabel;
    private final Label extraLabel;
    private final Button deleteButton;
    private final Consumer<TrabajoViewModelRepuesto> onDelete;

    public CeldaTrabajo(Consumer<TrabajoViewModelRepuesto> onDelete) {
        super();

        this.onDelete = onDelete;

        descriptionLabel = new Label();
        priceLabel = new Label();
        extraLabel = new Label();
        deleteButton = new Button("X");
        deleteButton.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        priceLabel.setPrefWidth(80);
        extraLabel.setPrefWidth(120);
        HBox.setHgrow(descriptionLabel, Priority.ALWAYS);

        hbox.getChildren().addAll(descriptionLabel, priceLabel, extraLabel, deleteButton);
    }

    @Override
    protected void updateItem(TrabajoViewModelRepuesto item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            descriptionLabel.setText(item.getCeldaDescripcion());
            priceLabel.setText(item.getSubTotal().toString());
            extraLabel.setText(item.getCeldaExtra());

            deleteButton.setOnAction(event -> {
                getListView().getItems().remove(item);
                if (onDelete != null) {
                    onDelete.accept(item);
                }
            });

            setGraphic(hbox);
        }
    }
}
