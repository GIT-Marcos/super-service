package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.GeneradorImagenes;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import org.apache.commons.mail.EmailException;

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
        if (fechaMin.getValue() == null) fechaMin.setValue(LocalDate.now().minusYears(20L));
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
            NotificationHelper.mostrarAdvertencia("Reporte de uso",
                    "No se encontraron notas de retiro entre esas fechas.");
        }
    }

    @FXML
    private void exportar(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte de usos de repuestos");
        } else {
            NotificationHelper.mostrarAdvertencia("Reporte de uso",
                    "No hay datos para exportar.");
        }
    }

    @FXML
    private void enviarMail() {
        if (obsPie.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Reporte de uso",
                    "No hay datos para enviar.");
            return;
        }
        String destinatario = SimpleDialogs.pedirMailParaEnviarReporte();
        try {
            ManejadorInputs.eMail(destinatario, true);
            enviarSnapshotPorCorreo(destinatario);
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarError("Reporte de uso",
                    e.getMessage());
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
            NotificationHelper.mostrarExito("Reporte enviado",
                    "El reporte se envió correctamente a " + destinatario);
            // Opcional: borrar el archivo temporal inmediatamente
            tempFile.deleteOnExit();
        } catch (IOException e) {
            NotificationHelper.mostrarError("Error de IO",
                    "No se pudo generar la imagen temporal: " + e.getMessage());
            e.printStackTrace();
        } catch (EmailException e) {
            NotificationHelper.mostrarError("Error Mail",
                    "Fallo al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarCampos() {
        fechaMin.setConverter(new SafeLocalDateConverter());
        fechaMax.setConverter(new SafeLocalDateConverter());

        pieChart.setData(obsPie);
    }
}
