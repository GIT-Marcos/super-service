package SPRService.SPRService.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import SPRService.SPRService.util.alertas.Alertas;

import java.io.File;
import java.util.Optional;

public class SimpleDialogs {

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

    //esto es auxiliar hasta implementación de JasperReports
    public static Integer selectorFechaReporte(String titulo, String header, String content) {
        Integer fecha;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> opt = dialog.showAndWait();
        //si se cierra la ventana
        if (opt.isEmpty()) {
            return null;
        }
        String input = opt.get().trim();
        try {
            fecha = Integer.valueOf(input);
        } catch (NumberFormatException e) {
            Alertas.aviso("Generar reporte", "Formato no válido");
            return null;
        }
        return fecha;
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
        String input = opt.get().trim();
        try {
            return ManejadorInputs.cantidadStock(input, true);
        } catch (RuntimeException e) {
            Alertas.error("Error de formato", e.getMessage());
            return null;
        }
    }

    public static Integer inputEntero() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Generar reporte");
        dialog.setHeaderText("Indique la cantidad de elementos a contemplar en el reporte.");
        dialog.setContentText("Cantidad: ");

        Optional<String> opt = dialog.showAndWait();
        //si se cierra la ventana
        if (opt.isEmpty()) {
            return null;
        }
        String input = opt.get().trim();
        try {
            if (input.isBlank()) {
                Alertas.aviso("Genera reporte", "Para continuar debe ingresar una cantidad.");
                return null;
            }
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            Alertas.error("Generar reporte", "Cantidad ingresada en mal formato");
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
            return ManejadorInputs.textoGenerico(opt.get().strip(), true, 3, 50);
        } catch (NullPointerException | IllegalArgumentException e) {
            Alertas.aviso("Cancelación de venta", e.getMessage());
            return null;
        }
    }

}
