package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CeldaItemDetalleRetiro extends CeldaItemDetalle<ItemDetalleRetiroViewModel> {

    private Label lblCodBarras;
    private Label lblNombreRepuesto;
    private Label lblPrecioUni;
    private Label lblCantidad;

    public CeldaItemDetalleRetiro() {
        super();
        lblCodBarras = new Label();
        lblCodBarras.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-padding: 2 8; -fx-background-radius: 3;");
        lblNombreRepuesto = new Label();
        lblNombreRepuesto.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-padding: 2 8; -fx-background-radius: 3;");
        lblPrecioUni = new Label();
        lblPrecioUni.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-padding: 2 8; -fx-background-radius: 3;");
        lblCantidad = new Label();
        lblCantidad.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-padding: 2 8; -fx-background-radius: 3;");

        HBox header = new HBox(10);
        header.getChildren().addAll(lblCodBarras, lblNombreRepuesto, lblPrecioUni, lblCantidad);

        container.getChildren().clear();
        container.getChildren().addAll(header, lblCodBarras, lblNombreRepuesto, lblPrecioUni, lblCantidad);
    }

    @Override
    protected void actualizarContenido(ItemDetalleRetiroViewModel item) {
        actualizarDatosBasicos(item);

        lblCodBarras.setText("Cod barras: " + item.getCodBarras());
        lblNombreRepuesto.setText("Cantidad: " + item.cantidadProperty());
        lblPrecioUni.setText("Precio uni: " + item.getPrecioUnitario());
        lblCantidad.setText("Cantidad: " + item.cantidadProperty());

        // Cambiar color según stock
//        if (item.getStock() < 5) {
//            lblStock.setStyle(lblStock.getStyle() + "-fx-background-color: #f44336;");
//        } else {
//            lblStock.setStyle(lblStock.getStyle() + "-fx-background-color: #4CAF50;");
//        }

        // Agregar icono de producto
//        container.setStyle(container.getStyle() + "-fx-border-color: #2196F3; -fx-border-width: 0 0 0 4;");
    }

}
