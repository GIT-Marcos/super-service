package SPRService.SPRService.util;

import SPRService.SPRService.util.alertas.NotificationHelper;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class SimpleDialogs {

    public static boolean confirmacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        ButtonType btnSi = new ButtonType("Sí", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnSi, btnNo);

        Optional<ButtonType> resultado = alerta.showAndWait();
        return resultado.isPresent() && resultado.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
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

        ButtonType botonRestablecer = new ButtonType("RESTABLECER TODOS los stocks");
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

    public static String nombreMarcaRepuesto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Crear nueva marca de repuestos");
        dialog.setHeaderText("Indique el nombre de la nueva marca de repuestos que desea cargar en el sistema.");
        dialog.setContentText("Nombre: ");

        Optional<String> opt = dialog.showAndWait();
        if (opt.isEmpty()) {
            return null;
        }
        try {
            return ManejadorInputs.textoGenerico(opt.get(), true, "Nombre de marca",
                    100);
        } catch (NullPointerException | IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Crear marca de repuestos", e.getMessage());
            return null;
        }
    }

    public static String nombreUbicacion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Crear nueva ubicación");
        dialog.setHeaderText("Indique el nombre de la nueva ubicación que desea cargar en el sistema.");
        dialog.setContentText("Nombre: ");

        Optional<String> opt = dialog.showAndWait();
        if (opt.isEmpty()) {
            return null;
        }
        try {
            return ManejadorInputs.textoGenerico(opt.get(), true, "Nombre de ubicación",
                    100);
        } catch (NullPointerException | IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Crear nueva ubicación", e.getMessage());
            return null;
        }
    }

    public static File selectorRuta(ActionEvent event, String titulo, String nombreDefecto,
                                    FileChooser.ExtensionFilter extensiones) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        File file;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(titulo);
        fileChooser.setInitialFileName(nombreDefecto);
        fileChooser.getExtensionFilters().add(extensiones);
        file = fileChooser.showSaveDialog(s);
        return file;
    }

    public static Double inputStock() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar stock");
        dialog.setHeaderText("Indique la cantidad de stock a agregar.");
        dialog.setContentText("Cantidad: ");

        Optional<String> opt = dialog.showAndWait();
        //si se cierra la ventana
        if (opt.isEmpty()) {
            return null;
        }
        String input = opt.get().strip();
        try {
            return ManejadorInputs.cantidadStock(input, true);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarAdvertencia("Ingresar stock", e.getMessage());
            return null;
        }
    }

    public static String motivoBorrado() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Motivo");
        dialog.setHeaderText("Ingrese el motivo por el cual se cancela la venta.");
        dialog.setContentText("Motivo: ");

        Optional<String> opt = dialog.showAndWait();
        if (opt.isEmpty()) {
            return null;
        }

        try {
            return ManejadorInputs.textoGenerico(opt.get(), true, "Motivo",
                    50);
        } catch (NullPointerException | IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Motivo de cancelación", e.getMessage());
            return null;
        }
    }

    public static String pedirMailParaEnviarReporte() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enviar Reporte por Correo");
        dialog.setHeaderText("Enviar gráfico actual");
        dialog.setContentText("Ingrese el correo del destinatario:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static String pedirMailParaRecuperarContrasenia() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enviar Correo de recuperación");
        dialog.setContentText("Ingrese su dirección de correo para recuperar la contraseña:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
