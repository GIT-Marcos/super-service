package SPRService.SPRService.util.alertas;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alertas {

    public static void error(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }

    public static void exito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }

    public static void aviso(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }

    public static boolean confirmacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        Optional<ButtonType> resultado = alerta.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    /**
     * Para preguntar al usuario como se debe proceder en la cancelación de la venta.
     * @return Un objeto Boolean:
     *         - {@code true} si el usuario presiona "Restablecer stocks".
     *         - {@code false} si el usuario presiona "NO restablecer".
     *         - {@code null} si el usuario presiona "Cancelar" o cierra la ventana.
     */
    public static Boolean confirmacionRestablecerStocks() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Cancelación de venta");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Desea restablecer los " +
                "existentes de stock en los productos vendidos?");

        ButtonType botonRestablecer = new ButtonType("RESTABLECER todos los stocks");
        ButtonType botonNoRestablecer = new ButtonType("NO restablecer stocks");
        ButtonType botonCancelar = new ButtonType("Cancelar operación", ButtonBar.ButtonData.CANCEL_CLOSE);

        alerta.getButtonTypes().setAll(botonRestablecer, botonNoRestablecer, botonCancelar);
        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent()) {
            if (resultado.get() == botonRestablecer) {
                return true;
            } else if (resultado.get() == botonNoRestablecer) {
                return false;
            }
        }
        return null;
    }
}
