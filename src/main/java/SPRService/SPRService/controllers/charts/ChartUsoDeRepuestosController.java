package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.generadores.GeneradorImagenes;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.apache.commons.mail.EmailException;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ChartUsoDeRepuestosController implements Initializable {

    private final RepuestoServ repuestoServ;
    private final EMailSender eMailSender;
    private ObservableList<PieChart.Data> obsPie = FXCollections.observableArrayList();

    @FXML
    private BorderPane rootPane;
    @FXML
    private PieChart pieChart;
    @FXML
    private DatePicker fechaMin, fechaMax;

    @Inject
    public ChartUsoDeRepuestosController(RepuestoServ repuestoServ, EMailSender eMailSender) {
        this.repuestoServ = repuestoServ;
        this.eMailSender = eMailSender;
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
            Notifications.create()
                    .title("Reporte de uso")
                    .text("No se encontraron notas de retiro entre esas fechas.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
        }
    }

    @FXML
    private void exportar(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte de usos de repuestos");
        } else {
            Notifications.create()
                    .title("Reporte de uso")
                    .text("No hay datos para exportar.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
        }
    }

    @FXML
    private void enviarMail() {
        if (obsPie.isEmpty()) {
            Notifications.create()
                    .title("Reporte de uso")
                    .text("Primero debe generar el reporte para poder enviarlo.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
            return;
        }
        String destinatario = SimpleDialogs.pedirMailParaEnviarReporte();
        try {
            ManejadorInputs.eMail(destinatario, true);
            enviarSnapshotPorCorreo(destinatario);
        } catch (IllegalArgumentException e) {
            Notifications.create()
                    .title("Ingreso de dirección de correo")
                    .text(e.getMessage())
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
        }
    }

    private void enviarSnapshotPorCorreo(String destinatario) {
        try {
            // --- PASO A: TOMAR CAPTURA DEL CHART ---
            File tempFile = GeneradorImagenes.tomarScreenshotTemporalDeVista(rootPane);
            // --- PASO B: ENVIAR MAIL USANDO TU CLASE GENERADOR ---
            String asunto = "Reporte de Uso de Repuestos - " + LocalDate.now();
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de uso de repuestos generado por el sistema.\n\n" +
                    "Rango de fechas: " + fechaMin.getValue() + " al " + fechaMax.getValue();

            eMailSender.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

            // --- PASO C: CONFIRMACIÓN Y LIMPIEZA ---
            Notifications.create()
                    .title("Reporte de usos de repuestos")
                    .text("El reporte se envió correctamente a " + destinatario)
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
            // Opcional: borrar el archivo temporal inmediatamente
            tempFile.deleteOnExit();
        } catch (IOException e) {
            Notifications.create()
                    .title("Error IO")
                    .text("No se pudo generar la imagen temporal: " + e.getMessage())
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showError();
            e.printStackTrace();
        } catch (EmailException e) {
            Notifications.create()
                    .title("Error Mail")
                    .text("Fallo al enviar el correo. Verifique su conexión o configuración: " + e.getMessage())
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showError();
            e.printStackTrace();
        }
    }

    private void configurarCampos() {
        fechaMin.setConverter(new SafeLocalDateConverter());
        fechaMax.setConverter(new SafeLocalDateConverter());

        pieChart.setData(obsPie);
    }
}
