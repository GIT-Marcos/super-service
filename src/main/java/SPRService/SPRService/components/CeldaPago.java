package SPRService.SPRService.components;

import SPRService.SPRService.enums.MetodosPago;
import SPRService.SPRService.viewModels.celdas.ItemPagoViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CeldaPago extends ListCell<ItemPagoViewModel> {

    private HBox container;
    private VBox infoBox;
    private HBox filaSuperior;
    private HBox filaInferior;

    // Fila superior
    private Label lblMetodoPago;
    private Label lblMonto;
    private Label lblFecha;
    private Label lblEstado;

    // Fila inferior
    private Label lblDni;
    private Label lblReferencia;
    private Label lblDetallesPago;
    private Label lblDescuento;

    public CeldaPago() {
        inicializarUI();
    }

    private void inicializarUI() {
        // Container principal
        container = new HBox();
        container.getStyleClass().add("celda-pago-container");
        container.setAlignment(Pos.CENTER_LEFT);

        // VBox para info (2 filas)
        infoBox = new VBox();
        infoBox.getStyleClass().add("celda-pago-info-box");

        // Crear filas
        crearFilaSuperior();
        crearFilaInferior();

        // Agregar filas al VBox
        infoBox.getChildren().addAll(filaSuperior, filaInferior);

        // Agregar info al container
        container.getChildren().add(infoBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
    }

    private void crearFilaSuperior() {
        filaSuperior = new HBox();
        filaSuperior.getStyleClass().add("celda-pago-fila-superior");

        lblMetodoPago = new Label();
        lblMetodoPago.getStyleClass().add("celda-pago-metodo");

        lblMonto = new Label();
        lblMonto.getStyleClass().add("celda-pago-monto");
        HBox.setHgrow(lblMonto, Priority.ALWAYS);

        lblFecha = new Label();
        lblFecha.getStyleClass().add("celda-pago-fecha");

        lblEstado = new Label();
        lblEstado.getStyleClass().add("celda-pago-estado");

        filaSuperior.getChildren().addAll(
                lblMetodoPago,
                lblMonto,
                lblFecha,
                lblEstado
        );
    }

    private void crearFilaInferior() {
        filaInferior = new HBox();
        filaInferior.getStyleClass().add("celda-pago-fila-inferior");

        lblDni = new Label();
        lblDni.getStyleClass().add("celda-pago-dni");

        lblReferencia = new Label();
        lblReferencia.getStyleClass().add("celda-pago-referencia");

        lblDetallesPago = new Label();
        lblDetallesPago.getStyleClass().add("celda-pago-detalles");

        lblDescuento = new Label();
        lblDescuento.getStyleClass().add("celda-pago-descuento");

        filaInferior.getChildren().addAll(
                lblDni,
                lblReferencia,
                lblDetallesPago,
                lblDescuento
        );
    }

    @Override
    protected void updateItem(ItemPagoViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            actualizarContenido(item);
            setGraphic(container);
        }
    }

    private void actualizarContenido(ItemPagoViewModel item) {
        // Fila superior
        lblMetodoPago.setText(obtenerIconoMetodo(item.getMetodoPago()) + " " +
                item.getMetodoPago().toString());
        lblMonto.setText(formatearMonto(item.getMontoPagado()));
        lblFecha.setText(formatearFecha(item.getFechaPago()));

        // Estado
        aplicarEstado(item.getActivo());

        // Fila inferior
        lblDni.setText(item.getDni() != null ? "DNI: " + item.getDni() : "");

        lblReferencia.setText(item.getReferencia() != null ?
                "Ref: " + item.getReferencia() : "");

        // Detalles según forma de pago
        lblDetallesPago.setText(obtenerDetallesPago(item));

        // Descuento
        if (item.getDescuento() != null && item.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            lblDescuento.setText("Desc: " + formatearMonto(item.getDescuento()));
            lblDescuento.setVisible(true);
            lblDescuento.setManaged(true);
        } else {
            lblDescuento.setVisible(false);
            lblDescuento.setManaged(false);
        }

        // Aplicar estilo según forma de pago
        aplicarEstiloMetodoPago(item.getMetodoPago());
    }

    private String obtenerIconoMetodo(MetodosPago metodo) {
        if (metodo == null) return "💳";

        return switch (metodo) {
            case EFECTIVO -> "💵";
            case TARJETA_DEBITO, TARJETA_CREDITO -> "💳";
            case TRANSFERENCIA -> "🏦";
            case MERCADO_PAGO -> "📱";
            default -> "💰";
        };
    }

    private String formatearMonto(BigDecimal monto) {
        if (monto == null) return "$0";
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.of("es", "AR"));
        return formato.format(monto);
    }

    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    private String obtenerDetallesPago(ItemPagoViewModel item) {
        StringBuilder detalles = new StringBuilder();

        if (item.getMarcaTarjeta() != null) {
            detalles.append(item.getMarcaTarjeta());
        }

        if (item.getUltimos4() != null) {
            if (!detalles.isEmpty()) detalles.append(" ");
            detalles.append("****").append(item.getUltimos4());
        }

        if (item.getBanco() != null) {
            if (!detalles.isEmpty()) detalles.append(" • ");
            detalles.append(item.getBanco());
        }

        return detalles.toString();
    }

    private void aplicarEstado(Boolean activo) {
        // Limpiar estilos previos
        lblEstado.getStyleClass().removeAll("estado-activo", "estado-cancelado");
        container.getStyleClass().removeAll("container-cancelado");

        if (activo != null && activo) {
            lblEstado.setText("✓ Activo");
            lblEstado.getStyleClass().add("estado-activo");
        } else {
            lblEstado.setText("✗ Cancelado");
            lblEstado.getStyleClass().add("estado-cancelado");
            container.getStyleClass().add("container-cancelado");
        }
    }

    private void aplicarEstiloMetodoPago(MetodosPago metodo) {
        // Limpiar estilos previos
        lblMetodoPago.getStyleClass().removeAll(
                "metodo-efectivo",
                "metodo-tarjeta-debito",
                "metodo-tarjeta-credito",
                "metodo-transferencia",
                "metodo-mercadopago"
        );

        if (metodo == null) return;

        switch (metodo) {
            case EFECTIVO:
                lblMetodoPago.getStyleClass().add("metodo-efectivo");
                break;
            case TARJETA_DEBITO:
                lblMetodoPago.getStyleClass().add("metodo-tarjeta-debito");
                break;
            case TARJETA_CREDITO:
                lblMetodoPago.getStyleClass().add("metodo-tarjeta-credito");
                break;
            case TRANSFERENCIA:
                lblMetodoPago.getStyleClass().add("metodo-transferencia");
                break;
            case MERCADO_PAGO:
                lblMetodoPago.getStyleClass().add("metodo-mercadopago");
                break;
        }
    }
}