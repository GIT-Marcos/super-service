package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.generadores.GeneradorReportes;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.apache.commons.mail.EmailException;
import org.controlsfx.control.Notifications;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChartComparacionIngresosController implements Initializable {

    private final ServiceServ serviceServ;
    private final EMailSender eMailSender;
    private ObservableList<PieChart.Data> obsPie = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private DatePicker dpFechaMin, dpFechaMax;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label lblCantService, lblCantVentas, lblCantOperaciones, lblIngresosTotales, lblIngVentas, lblIngService;

    @Inject
    public ChartComparacionIngresosController(ServiceServ serviceServ, EMailSender eMailSender) {
        this.serviceServ = serviceServ;
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCampos();
    }

    @FXML
    private void generar() {
        limpiarVista();
        ReporteComparacionDTO dto = serviceServ.generarComparacion(dpFechaMin.getValue(), dpFechaMax.getValue());
        if (dto.isEmpty()) {
            Notifications.create()
                    .title("Generar reporte")
                    .text("No se encontraron datos para generar el reporte.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
            return;
        }
        obsPie.add(new PieChart.Data("Ventas: " + dto.pctVenta() + " %", dto.ingVenta().doubleValue()));
        obsPie.add(new PieChart.Data("Services: " + dto.pctService() + " %", dto.ingService().doubleValue()));
        llenarLabels(dto);
    }

    @FXML
    private void exportar(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorReportes.exportarJPEG(event, rootPane, "Comparación de ingresos");
        } else {
            Notifications.create()
                    .title("Exportar reporte")
                    .text("No hay datos para exportar.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
        }
    }

    @FXML
    private void enviarMail() {
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
            if (emailDestino.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$\n")) {
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
            String asunto = "Reporte comparativo de ingresos";
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico comparativo de ingresos " +
                    "generado por el sistema.\n\n";

            eMailSender.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

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

    private void llenarLabels(ReporteComparacionDTO dto) {
        lblCantService.setText(dto.cantService().toString());
        lblCantVentas.setText(dto.cantVenta().toString());
        lblCantOperaciones.setText(dto.totalOperaciones().toString());
        lblIngresosTotales.setText("$ " + dto.totalIngresos());
        lblIngVentas.setText("$ " + dto.ingVenta());
        lblIngService.setText("$ " + dto.ingService());
    }

    private void limpiarVista() {
        obsPie.clear();
        lblCantService.setText("*sin datos");
        lblCantVentas.setText("*sin datos");
        lblCantOperaciones.setText("*sin datos");
        lblIngresosTotales.setText("*sin datos");
        lblIngVentas.setText("*sin datos");
        lblIngService.setText("*sin datos");
    }

    private void configurarCampos() {
        dpFechaMin.setConverter(new SafeLocalDateConverter());
        dpFechaMax.setConverter(new SafeLocalDateConverter());

        pieChart.setData(obsPie);
        String css = getClass().getResource("/styles/pieChartColores.css").toExternalForm();
        ;
        pieChart.getStylesheets().add(css);
    }
}
