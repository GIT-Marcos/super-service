package SPRService.SPRService.util.alertas;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationHelper {

    // Configuración por defecto
    private static final Duration DURACION_DEFAULT = Duration.seconds(5);
    private static final Pos POSICION_DEFAULT = Pos.BOTTOM_RIGHT;

    /**
     * Muestra una notificación de INFORMACIÓN / ÉXITO (Color azul/estándar).
     * Puede ser invocada desde cualquier hilo.
     */
    public static void mostrarExito(String titulo, String mensaje) {
        mostrar(titulo, mensaje, TipoNotificacion.INFORMACION);
    }

    /**
     * Muestra una notificación de ADVERTENCIA (Color amarillo/naranja).
     */
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        mostrar(titulo, mensaje, TipoNotificacion.ADVERTENCIA);
    }

    /**
     * Muestra una notificación de ERROR (Color rojo).
     */
    public static void mostrarError(String titulo, String mensaje) {
        mostrar(titulo, mensaje, TipoNotificacion.ERROR);
    }

    /**
     * Genérico privado para construir la notificación.
     * Incluye protección Platform.runLater para evitar errores de hilos.
     */
    private static void mostrar(String titulo, String mensaje, TipoNotificacion tipo) {
        Platform.runLater(() -> {
            Notifications notification = Notifications.create()
                    .title(titulo)
                    .text(mensaje)
                    .hideAfter(DURACION_DEFAULT)
                    .position(POSICION_DEFAULT);
            switch (tipo) {
                case INFORMACION:
                    notification.showInformation();
                    break;
                case ADVERTENCIA:
                    notification.showWarning();
                    break;
                case ERROR:
                    notification.showError();
                    break;
                case CONFIRMACION: // Rara vez usado en toasts, pero disponible
                    notification.showConfirm();
                    break;
            }
        });
    }

    // Enum interno para organizar los tipos
    private enum TipoNotificacion {
        INFORMACION, ADVERTENCIA, ERROR, CONFIRMACION
    }

    /**
     * Avanzado para notificaciones personalizadas (con acción al hacer clic).
     */
    public static void mostrarConAccion(String titulo, String mensaje, Runnable accionAlClic) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(titulo)
                    .text(mensaje)
                    .hideAfter(Duration.seconds(8)) // Un poco más de tiempo
                    .position(POSICION_DEFAULT)
                    .onAction(e -> accionAlClic.run())
                    .showInformation();
        });
    }
}