package SPRService.SPRService.components;

import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.viewModels.celdas.ItemOperacionViewModel;
import SPRService.SPRService.viewModels.celdas.ItemServiceViewModel;
import SPRService.SPRService.viewModels.celdas.ItemVentaViewModel;
import javafx.scene.control.Label;

public class CeldaOperacionUniversal extends CeldaItemOperacion<ItemOperacionViewModel> {

    // Componentes adicionales que el Padre no tiene
    private Label lblPatente;
    private Label lblEstado;

    public CeldaOperacionUniversal() {
        super(); // 1. El padre arma la estructura básica
        inicializarComponentesExclusivos();
    }

    private void inicializarComponentesExclusivos() {
        // Inicializamos Patente (Panel Izquierdo)
        lblPatente = new Label();
        lblPatente.getStyleClass().add("celda-label-patente");
        // leftContainer viene heredado del padre
        leftContainer.getChildren().add(lblPatente);

        // Inicializamos Estado (Panel Derecho)
        lblEstado = new Label();
        lblEstado.getStyleClass().add("celda-estado-base");
        // rightContainer viene heredado del padre
        rightContainer.getChildren().add(lblEstado);
    }

    @Override
    protected void actualizarContenido(ItemOperacionViewModel item) {
        // 1. Limpieza IMPORTANTE (por el reciclaje de celdas de JavaFX)
        limpiarEstilos();

        // 2. Bifurcación de lógica según el tipo
        if (item instanceof ItemVentaViewModel) {
            configurarComoVenta((ItemVentaViewModel) item);
        } else if (item instanceof ItemServiceViewModel) {
            configurarComoService((ItemServiceViewModel) item);
        }
    }

    // --- LÓGICA ESPECÍFICA DE VENTA ---
    private void configurarComoVenta(ItemVentaViewModel venta) {
        // Venta NO tiene patente -> ocultar
        setVisible(lblPatente, false);

        // Lógica de Estado
        EstadoVentaRepuesto estado = venta.getEstado();
        if (estado != null) {
            lblEstado.setText(estado.toString()); // Usa el "nombreEstado" del Enum
            setVisible(lblEstado, true);
            aplicarColorVenta(estado); // <--- LÓGICA DE ESTADOS AQUÍ
        } else {
            setVisible(lblEstado, false);
        }
    }

    // --- LÓGICA ESPECÍFICA DE SERVICE ---
    private void configurarComoService(ItemServiceViewModel service) {
        // Service SÍ tiene patente -> mostrar
        String patente = service.getPatente();
        if (patente != null && !patente.isBlank()) {
            lblPatente.setText("Patente: " + patente.toUpperCase());
            setVisible(lblPatente, true);
        } else {
            setVisible(lblPatente, false);
        }

        // Lógica de Estado
        EstadoService estado = service.getEstado();
        if (estado != null) {
            lblEstado.setText(estado.toString()); // Usa el "nombreEstado" del Enum
            setVisible(lblEstado, true);
            aplicarColorService(estado); // <--- LÓGICA DE ESTADOS AQUÍ
        } else {
            setVisible(lblEstado, false);
        }
    }

    // --- LÓGICA DE COLORES (CSS) ---

    private void aplicarColorVenta(EstadoVentaRepuesto estado) {
        switch (estado) {
            case PRESUPUESTANDO:
                lblEstado.getStyleClass().add("estado-proceso"); break;
            case ACEPTADO:
            case PENDIENTE_PAGO:
                lblEstado.getStyleClass().add("estado-pendiente"); break;
            case PAGADO:
                lblEstado.getStyleClass().add("estado-finalizado"); break;
            case CANCELADO:
                lblEstado.getStyleClass().add("estado-cancelado"); break;
            default:
                lblEstado.getStyleClass().add("estado-pendiente");
        }
    }

    private void aplicarColorService(EstadoService estado) {
        switch (estado) {
            // Grupo Pendientes / Pausa
            case PENDIENTE:
            case EN_ESPERA:
            case PAUSADO:
                lblEstado.getStyleClass().add("estado-pendiente"); break;

            // Grupo Acción
            case TRABAJANDO:
                lblEstado.getStyleClass().add("estado-proceso"); break;

            // Grupo Alerta
            case PAGO_PENDIENTE:
                lblEstado.getStyleClass().add("estado-alerta"); break;

            // Grupo Final
            case FINALIZADO:
            case PAGADO:
                lblEstado.getStyleClass().add("estado-finalizado"); break;

            // Grupo Cancelado
            case CANCELADO:
                lblEstado.getStyleClass().add("estado-cancelado"); break;

            default:
                lblEstado.getStyleClass().add("estado-pendiente");
        }
    }

    // --- Utilitarios ---

    private void limpiarEstilos() {
        // Elimina todas las clases de estado posibles para no acumularlas
        lblEstado.getStyleClass().removeAll(
                "estado-pendiente", "estado-proceso",
                "estado-finalizado", "estado-cancelado", "estado-alerta"
        );
    }

    private void setVisible(Label lbl, boolean visible) {
        lbl.setVisible(visible);
        lbl.setManaged(visible); // Hace que el nodo desaparezca del layout si no es visible
    }
}