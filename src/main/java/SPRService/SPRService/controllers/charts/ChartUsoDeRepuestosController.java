package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.GeneradorMail;
import SPRService.SPRService.util.generadores.GeneradorReportes;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import org.apache.commons.mail.EmailException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChartUsoDeRepuestosController implements Initializable {

    private final RepuestoServ repuestoServ;
    private ObservableList<PieChart.Data> obsPie = FXCollections.observableArrayList();

    @FXML
    private BorderPane rootPane;
    @FXML
    private PieChart pieChart;
    @FXML
    private DatePicker fechaMin, fechaMax;

    @Inject
    public ChartUsoDeRepuestosController(RepuestoServ repuestoServ) {
        this.repuestoServ = repuestoServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCampos();
    }

    @FXML
    private void generar() {
        if (fechaMin.getValue() == null) fechaMin.setValue(LocalDate.of(1900, 1, 1));
        if (fechaMax.getValue() == null) fechaMax.setValue(LocalDate.now());
        ReporteUsoDeRepuestosDTO dto = repuestoServ.usoDeRepuestos(fechaMin.getValue(), fechaMax.getValue());
        obsPie.clear();
        pieChart.setTitle("Uso de repuestos");
        if (dto.paraService() != 0 || dto.paraVenta() != 0) {
            String titulo = "Uso de repuestos entre: " + fechaMin.getValue() + " y " + fechaMax.getValue() +
                    " - Total de usos: " + dto.total();
            pieChart.setTitle(titulo);
            obsPie.add(new PieChart.Data("Ventas: " + dto.paraVenta() + " - " + dto.pctParaVenta() + " %",
                    dto.paraVenta()));
            obsPie.add(new PieChart.Data("Service: " + dto.paraService() + " - " + dto.pctParaService() + " %",
                    dto.paraService()));
        } else {
            Alertas.aviso("Reporte de uso", "No se encontraron notas de retiro entre esas fechas.");
        }
    }

    @FXML
    private void exportar(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorReportes.exportarJPEG(event, rootPane, "Reporte de usos de repuestos");
        } else {
            Alertas.aviso("Exportar reporte", "No hay datos para exportar.");
        }
    }

    @FXML
    private void enviarMail() {
        // 1. Validar que haya datos en el gráfico
        if (obsPie.isEmpty()) {
            Alertas.aviso("Enviar Reporte", "Primero debe generar el reporte para poder enviarlo.");
            return;
        }

        // 2. Pedir el correo del destinatario mediante un Dialogo
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enviar Reporte por Correo");
        dialog.setHeaderText("Enviar gráfico actual");
        dialog.setContentText("Ingrese el correo del destinatario:");

        Optional<String> result = dialog.showAndWait();

        // Si el usuario ingresó algo y dio OK
        if (result.isPresent()) {
            String emailDestino = result.get();
            if (emailDestino.isBlank()) {
                Alertas.error("Error", "El correo no puede estar vacío.");
                return;
            }
            if (emailDestino.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$\n")){
                Alertas.error("Error", "El correo está en mal formato.");
                return;
            }

            enviarSnapshotPorCorreo(emailDestino);
        }
    }

    private void enviarSnapshotPorCorreo(String destinatario) {
        try {
            // --- PASO A: TOMAR CAPTURA DEL CHART ---
            // Creamos un archivo temporal (se borra al cerrar la app o manualmente)
            File tempFile = File.createTempFile("reporte_temp_", ".jpg");

            // Tomamos la foto SOLAMENTE del PieChart (o puedes usar el nodo padre)
            WritableImage writableImage = rootPane.snapshot(new SnapshotParameters(), null);

            // Convertimos a formato compatible con guardado
            BufferedImage imageConTransparencia = SwingFXUtils.fromFXImage(writableImage, null);

            // Quitamos transparencia (fondo negro a blanco) para que se vea bien en JPEG
            BufferedImage imageBlanca = new BufferedImage(
                    imageConTransparencia.getWidth(),
                    imageConTransparencia.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = imageBlanca.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imageBlanca.getWidth(), imageBlanca.getHeight());
            graphics.drawImage(imageConTransparencia, 0, 0, null);
            graphics.dispose();

            // Guardamos en el archivo temporal
            ImageIO.write(imageBlanca, "jpg", tempFile);

            // --- PASO B: ENVIAR MAIL USANDO TU CLASE GENERADOR ---
            String asunto = "Reporte de Uso de Repuestos - " + LocalDate.now();
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de uso de repuestos generado por el sistema.\n\n" +
                    "Rango de fechas: " + fechaMin.getValue() + " al " + fechaMax.getValue();

            GeneradorMail.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

            // --- PASO C: CONFIRMACIÓN Y LIMPIEZA ---
            Alertas.exito("Envío Exitoso", "El reporte se envió correctamente a " + destinatario);

            // Opcional: borrar el archivo temporal inmediatamente
            tempFile.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
            Alertas.error("Error IO", "No se pudo generar la imagen temporal: " + e.getMessage());
        } catch (EmailException e) {
            e.printStackTrace();
            Alertas.error("Error Mail", "Fallo al enviar el correo. Verifique su conexión o configuración: " + e.getMessage());
        }
    }

    private void configurarCampos() {
        fechaMin.setConverter(new SafeLocalDateConverter());
        fechaMax.setConverter(new SafeLocalDateConverter());

        pieChart.setData(obsPie);
    }
}
