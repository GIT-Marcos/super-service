package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class CeldaItemDetalleRetiro extends CeldaItemDetalle<ItemDetalleRetiroViewModel> {

    private Label lblCodBarras;
    private Label lblNombreRepuesto;
    private Label lblmarcaRepuesto;
    private Label lblPrecioUni;
    private Label lblCantidad;
    private HBox header;
    private HBox center;

    public CeldaItemDetalleRetiro() {
        super();
        inicializarComponentes();
    }

    public CeldaItemDetalleRetiro(Consumer<? super ItemDetalleRetiroViewModel> onEliminarItem) {
        super(onEliminarItem);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Agregar clase específica al container
        container.getStyleClass().add("celda-retiro-container");

        // Crear labels con estilos CSS
        lblCodBarras = new Label();
        lblCodBarras.getStyleClass().add("celda-retiro-badge");

        lblNombreRepuesto = new Label();
        lblNombreRepuesto.getStyleClass().add("celda-retiro-badge");

        lblmarcaRepuesto = new Label();
        lblmarcaRepuesto.getStyleClass().add("celda-retiro-badge");

        lblPrecioUni = new Label();
        lblPrecioUni.getStyleClass().add("celda-retiro-badge");

        lblCantidad = new Label();
        lblCantidad.getStyleClass().add("celda-retiro-badge");

        lblSubTotal.getStyleClass().add("celda-retiro-sub-total");

        // Crear header
        header = new HBox();
        header.getStyleClass().add("celda-retiro-header");
        header.getChildren().addAll(lblCodBarras, lblNombreRepuesto);

        center = new HBox();
        center.getStyleClass().add("celda-retiro-center");
        center.getChildren().addAll(lblmarcaRepuesto, lblPrecioUni, lblCantidad);

        // Reorganizar container
        container.getChildren().clear();
        container.getChildren().addAll(lblTipo, header, center, lblSubTotal);
    }

    @Override
    protected void actualizarContenido(ItemDetalleRetiroViewModel item) {
        actualizarDatosBasicos(item);

        lblCodBarras.setText("Cod. barras: " + item.getCodBarras());
        lblNombreRepuesto.setText("Repuesto: " + item.getNombreRepuesto());
        lblmarcaRepuesto.setText("Marca: " + item.getMarcaRepuesto());
        lblPrecioUni.setText("Precio: $" + item.getPrecioUnitario());
        lblCantidad.setText("Retirado: " + item.cantidadProperty().get());

        // Aplicar estilo condicional basado en stock
        aplicarEstiloStock(item);
    }

    private void aplicarEstiloStock(ItemDetalleRetiroViewModel item) {
        // Ejemplo: cambiar estilo si hay poco stock
        // if (item.getStock() < 5) {
        //     lblCantidad.getStyleClass().removeAll("celda-retiro-badge");
        //     lblCantidad.getStyleClass().add("celda-retiro-badge-warning");
        // }
    }
}