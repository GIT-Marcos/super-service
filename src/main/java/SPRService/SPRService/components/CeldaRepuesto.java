package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.celdas.ItemRepuestoViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CeldaRepuesto extends ListCell<ItemRepuestoViewModel> {

    private HBox container;
    private VBox infoBox;
    private HBox filaSuperior;
    private HBox filaInferior;
    private Label lblCodBarras;
    private Label lblNombreRepuesto;
    private Label lblMarca;
    private Label lblPrecio;
    private Label lblStock;
    private Label lblEstadoStock;
    private Label lblInactivo;

    public CeldaRepuesto() {
        inicializarUI();
    }

    private void inicializarUI() {
        container = new HBox();
        container.getStyleClass().add("celda-repuesto-container");
        container.setAlignment(Pos.CENTER_LEFT);

        infoBox = new VBox();
        infoBox.getStyleClass().add("celda-repuesto-info-box");

        crearFilaSuperior();
        crearFilaInferior();

        infoBox.getChildren().addAll(filaSuperior, filaInferior);

        container.getChildren().add(infoBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
    }

    private void crearFilaSuperior() {
        filaSuperior = new HBox();
        filaSuperior.getStyleClass().add("celda-repuesto-fila-superior");

        lblCodBarras = new Label();
        lblCodBarras.getStyleClass().add("celda-repuesto-codigo");

        lblNombreRepuesto = new Label();
        lblNombreRepuesto.getStyleClass().add("celda-repuesto-nombre");
        HBox.setHgrow(lblNombreRepuesto, Priority.ALWAYS); // ✅ Permitir que crezca

        lblMarca = new Label();
        lblMarca.getStyleClass().add("celda-repuesto-marca");

        lblInactivo = new Label("INACTIVO");
        lblInactivo.getStyleClass().add("celda-repuesto-inactivo");
        lblInactivo.setVisible(false);
        lblInactivo.setManaged(false);

        filaSuperior.getChildren().addAll(
                lblCodBarras,
                lblInactivo,
                lblNombreRepuesto,
                lblMarca
        );
    }

    private void crearFilaInferior() {
        filaInferior = new HBox();
        filaInferior.getStyleClass().add("celda-repuesto-fila-inferior");

        lblStock = new Label();
        lblStock.getStyleClass().add("celda-repuesto-stock");

        lblEstadoStock = new Label();
        lblEstadoStock.getStyleClass().add("celda-repuesto-estado");

        lblPrecio = new Label();
        lblPrecio.getStyleClass().add("celda-repuesto-precio");

        filaInferior.getChildren().addAll(lblStock, lblEstadoStock, lblPrecio);
    }

    @Override
    protected void updateItem(ItemRepuestoViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            actualizarContenido(item);
            setGraphic(container);
        }
    }

    private void actualizarContenido(ItemRepuestoViewModel item) {
        lblCodBarras.setText(item.getCodBarras());

        // Nombre completo con tooltip si es largo
        String nombreCompleto = item.getNombreRepuesto();
        lblNombreRepuesto.setText(nombreCompleto);
        if (nombreCompleto != null && nombreCompleto.length() > 30) {
            lblNombreRepuesto.setTooltip(new Tooltip(nombreCompleto));
        }

        lblMarca.setText(item.getMarcaRepuesto());
        lblPrecio.setText(formatearPrecio(item.getPrecioUnitario()));

        // Fila inferior - Compacta
        lblStock.setText(formatearStockCompacto(
                item.getCantidadExistente(),
                item.getCantidadMinima(),
                item.getUnidadMedida()
        ));

        // Verificar estado del stock
        aplicarEstadoStock(item.getCantidadExistente(), item.getCantidadMinima());

        aplicarEstadoActivo(item.isActivo());
    }

    private void aplicarEstadoActivo(boolean activo) {
        // Limpiar estado previo
        container.getStyleClass().remove("container-inactivo");
        lblNombreRepuesto.getStyleClass().remove("nombre-inactivo");
        lblCodBarras.getStyleClass().remove("codigo-inactivo");

        if (!activo) {
            // Mostrar badge INACTIVO
            lblInactivo.setVisible(true);
            lblInactivo.setManaged(true);

            // Aplicar estilos de inactivo
            container.getStyleClass().add("container-inactivo");
            lblNombreRepuesto.getStyleClass().add("nombre-inactivo");
            lblCodBarras.getStyleClass().add("codigo-inactivo");

            // Tooltip informativo
            container.setOnMouseEntered(e -> {
                Tooltip tooltip = new Tooltip("⚠ Este repuesto está INACTIVO y no puede ser vendido");
                Tooltip.install(container, tooltip);
            });
        } else {
            // Ocultar badge INACTIVO
            lblInactivo.setVisible(false);
            lblInactivo.setManaged(false);
        }
    }

    private String formatearPrecio(BigDecimal precio) {
        if (precio == null) return "$0";
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.of("es", "AR"));
        return formato.format(precio);
    }

    private String formatearStockCompacto(double existente, double minima, String unidad) {
        return String.format("%.0f/%.0f %s", existente, minima, unidad);
    }

    private void aplicarEstadoStock(double existente, double minima) {
        // Limpiar estilos previos
        lblEstadoStock.getStyleClass().removeAll(
                "estado-critico",
                "estado-bajo",
                "estado-normal"
        );
        lblStock.getStyleClass().removeAll(
                "stock-critico",
                "stock-bajo",
                "stock-normal"
        );
        container.getStyleClass().removeAll(
                "container-critico",
                "container-bajo"
        );

        if (existente <= 0) {
            // Stock crítico (agotado)
            lblEstadoStock.setText("AGOTADO");
            lblEstadoStock.getStyleClass().add("estado-critico");
            lblStock.getStyleClass().add("stock-critico");
            container.getStyleClass().add("container-critico");
        } else if (existente < minima) {
            // Stock bajo
            lblEstadoStock.setText("BAJO");
            lblEstadoStock.getStyleClass().add("estado-bajo");
            lblStock.getStyleClass().add("stock-bajo");
            container.getStyleClass().add("container-bajo");
        } else {
            // Stock normal
            lblEstadoStock.setText("OK");
            lblEstadoStock.getStyleClass().add("estado-normal");
            lblStock.getStyleClass().add("stock-normal");
        }
    }
}