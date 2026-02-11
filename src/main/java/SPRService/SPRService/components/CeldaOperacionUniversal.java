package SPRService.SPRService.components;

import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.viewModels.celdas.ItemOperacionViewModel;
import SPRService.SPRService.viewModels.celdas.ItemServiceViewModel;
import SPRService.SPRService.viewModels.celdas.ItemVentaViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class CeldaOperacionUniversal extends CeldaItemOperacion<ItemOperacionViewModel> {

    // Componentes adicionales que el Padre no tiene
    private Label lblPatente;
    private Label lblEstado;

    private Button btnVerDetalles;

    // 2. La acción a ejecutar (Callback)
    private final Consumer<ItemOperacionViewModel> onVerDetallesAction;

    // 3. Modificamos el constructor para recibir la acción
    public CeldaOperacionUniversal(Consumer<ItemOperacionViewModel> onVerDetallesAction) {
        super();
        this.onVerDetallesAction = onVerDetallesAction;
        inicializarComponentesExclusivos();
    }

    private void inicializarComponentesExclusivos() {
        // --- Labels existentes ---
        lblPatente = new Label();
        lblPatente.getStyleClass().add("celda-label-patente");
        leftContainer.getChildren().add(lblPatente);

        lblEstado = new Label();
        lblEstado.getStyleClass().add("celda-estado-base");
        rightContainer.getChildren().add(lblEstado);

        // --- 4. Configuración del Botón ---
        btnVerDetalles = new Button("Ver"); // O usa un icono
        btnVerDetalles.getStyleClass().add("btn-ver-detalles");

        // Añadimos el botón al contenedor principal (mainContainer es protected en el padre)
        // Lo agregamos al final para que quede a la derecha de los montos
        mainContainer.getChildren().add(btnVerDetalles);
    }

    @Override
    protected void updateItem(ItemOperacionViewModel item, boolean empty) {
        // Llamamos al super para que haga la lógica básica (textos, fechas, null check)
        super.updateItem(item, empty);

        if (empty || item == null) {
            // El padre ya pone setGraphic(null), pero aseguramos que el botón no tenga acción
            btnVerDetalles.setOnAction(null);
        } else {
            // 5. Asignamos la acción al botón pasando el Item actual
            btnVerDetalles.setOnAction(event -> {
                if (onVerDetallesAction != null) {
                    onVerDetallesAction.accept(item);
                }
            });

            // Estilos específicos (tu lógica existente)
            actualizarContenido(item);
        }
    }

    @Override
    protected void actualizarContenido(ItemOperacionViewModel item) {
        limpiarEstilos();

        if (item instanceof ItemVentaViewModel) {
            configurarComoVenta((ItemVentaViewModel) item);
            btnVerDetalles.setText("📝 Detalles");
        } else if (item instanceof ItemServiceViewModel) {
            configurarComoService((ItemServiceViewModel) item);
            btnVerDetalles.setText("📝 Detalles");
        }
    }

    // --- LÓGICA ESPECÍFICA DE VENTA ---
    private void configurarComoVenta(ItemVentaViewModel venta) {
        setVisible(lblPatente, false);

        // --- ESTILO DIFERENCIAL ---
        // Asumiendo que 'lblTitulo' es protected en la clase padre
        lblTitulo.getStyleClass().add("tipo-venta-titulo");
        // Opcional: Agregar borde al contenedor principal
        getStyleClass().add("tipo-venta-borde");

        EstadoVentaRepuesto estado = venta.getEstado();
        if (estado != null) {
            lblEstado.setText(estado.toString());
            setVisible(lblEstado, true);
            aplicarColorVenta(estado);
        } else {
            setVisible(lblEstado, false);
        }
    }

    // --- LÓGICA ESPECÍFICA DE SERVICE ---
    private void configurarComoService(ItemServiceViewModel service) {
        String patente = service.getPatente();
        if (patente != null && !patente.isBlank()) {
            lblPatente.setText("Patente: " + patente.toUpperCase());
            setVisible(lblPatente, true);
        } else {
            setVisible(lblPatente, false);
        }

        // --- ESTILO DIFERENCIAL ---
        lblTitulo.getStyleClass().add("tipo-service-titulo");
        // Opcional: Agregar borde al contenedor principal
        getStyleClass().add("tipo-service-borde");

        EstadoService estado = service.getEstado();
        if (estado != null) {
            lblEstado.setText(estado.toString());
            setVisible(lblEstado, true);
            aplicarColorService(estado);
        } else {
            setVisible(lblEstado, false);
        }
    }

    // --- LÓGICA DE COLORES (CSS) ---

    private void aplicarColorVenta(EstadoVentaRepuesto estado) {
        switch (estado) {
            case PRESUPUESTANDO -> lblEstado.getStyleClass().add("estado-proceso");
            case ACEPTADO, PENDIENTE_PAGO -> lblEstado.getStyleClass().add("estado-pendiente");
            case PAGADO -> lblEstado.getStyleClass().add("estado-finalizado");
            case CANCELADO -> lblEstado.getStyleClass().add("estado-cancelado");
            default -> lblEstado.getStyleClass().add("estado-pendiente");
        }
    }

    private void aplicarColorService(EstadoService estado) {
        switch (estado) {
            // Grupo Pendientes / Pausa
            case PENDIENTE, EN_ESPERA, PAUSADO -> lblEstado.getStyleClass().add("estado-pendiente");
            // Grupo Acción
            case TRABAJANDO -> lblEstado.getStyleClass().add("estado-proceso");
            // Grupo Alerta
            case PAGO_PENDIENTE -> lblEstado.getStyleClass().add("estado-alerta");
            // Grupo Final
            case FINALIZADO, PAGADO -> lblEstado.getStyleClass().add("estado-finalizado");
            // Grupo Cancelado
            case CANCELADO -> lblEstado.getStyleClass().add("estado-cancelado");
            default -> lblEstado.getStyleClass().add("estado-pendiente");
        }
    }

    // --- Utilitarios ---

    private void limpiarEstilos() {
        // Limpia los estados
        lblEstado.getStyleClass().removeAll(
                "estado-pendiente", "estado-proceso",
                "estado-finalizado", "estado-cancelado", "estado-alerta"
        );

        // Limpia los estilos de TIPO (Venta vs Service) del Título
        // (Nota: lblTitulo debe ser accesible desde el padre)
        if (lblTitulo != null) {
            lblTitulo.getStyleClass().removeAll("tipo-venta-titulo", "tipo-service-titulo");
        }

        // Limpia los estilos de borde de la celda misma
        getStyleClass().removeAll("tipo-venta-borde", "tipo-service-borde");
    }

    private void setVisible(Label lbl, boolean visible) {
        lbl.setVisible(visible);
        lbl.setManaged(visible);
    }
}