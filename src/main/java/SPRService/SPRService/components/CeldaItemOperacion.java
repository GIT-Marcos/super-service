package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemOperacionViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public abstract class CeldaItemOperacion<T extends ItemOperacionViewModel> extends ListCell<T> {

    protected HBox mainContainer;
    protected VBox leftContainer;
    protected VBox rightContainer;

    // Datos básicos de ItemOperacionViewModel
    protected Label lblTitulo; // Tipo + Código
    protected Label lblFecha;
    protected Label lblTotal;
    protected Label lblFaltante;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CeldaItemOperacion() {
        inicializarUI();
    }

    private void inicializarUI() {
        // 1. Contenedor principal horizontal
        mainContainer = new HBox();
        mainContainer.getStyleClass().add("celda-operacion-container");
        mainContainer.setAlignment(Pos.CENTER_LEFT);
        mainContainer.setSpacing(10);
        mainContainer.setPadding(new Insets(5, 10, 5, 10));

        // 2. Contenedor Izquierdo (Título y Fecha)
        leftContainer = new VBox();
        leftContainer.setAlignment(Pos.CENTER_LEFT);
        leftContainer.setSpacing(3);

        lblTitulo = new Label();
        lblTitulo.getStyleClass().add("celda-op-titulo");

        lblFecha = new Label();
        lblFecha.getStyleClass().add("celda-op-fecha");

        leftContainer.getChildren().addAll(lblTitulo, lblFecha);

        // 3. Espaciador central (Empuja el contenido derecho hacia el final)
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 4. Contenedor Derecho (Montos)
        rightContainer = new VBox();
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        rightContainer.setSpacing(3);

        lblTotal = new Label();
        lblTotal.getStyleClass().add("celda-op-total");

        lblFaltante = new Label();
        lblFaltante.getStyleClass().add("celda-op-faltante");

        rightContainer.getChildren().addAll(lblTotal, lblFaltante);

        // Agregar al contenedor principal
        mainContainer.getChildren().addAll(leftContainer, spacer, rightContainer);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            actualizarDatosBasicos(item);
            actualizarContenido(item); // Abstracto para detalles específicos de los hijos
            setGraphic(mainContainer);
        }
    }

    /**
     * Actualiza los elementos UI comunes definidos en ItemOperacionViewModel
     */
    protected void actualizarDatosBasicos(T item) {
        // Título: "Venta #1234" o "Service #555"
        lblTitulo.setText(item.getTipo() + " #" + item.getCodigo());

        // Fecha formateada
        if (item.getFecha() != null) {
            lblFecha.setText(item.getFecha().format(DATE_FORMATTER));
        } else {
            lblFecha.setText("--/--/----");
        }

        // Total
        lblTotal.setText("Total: $" + (item.getTotal() != null ? item.getTotal().toString() : "0.00"));

        // Faltante (Solo se muestra si es mayor a 0)
        BigDecimal faltante = item.getFaltante();
        if (faltante != null && faltante.compareTo(BigDecimal.ZERO) > 0) {
            lblFaltante.setText("Resta: $" + faltante.toString());
            lblFaltante.setVisible(true);
            lblFaltante.setManaged(true);
        } else {
            lblFaltante.setVisible(false);
            lblFaltante.setManaged(false); // Para que no ocupe espacio si no hay deuda
        }
    }

    /**
     * Abstracto para que las clases hijas (ItemVenta, ItemService)
     * agreguen lógica extra (como mostrar Patente o Estado).
     */
    protected abstract void actualizarContenido(T item);
}