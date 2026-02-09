package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.services.ServiceServ;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.mail.EmailException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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

        LocalDate fechaMin;
        LocalDate fechaMax;
        if (dpFechaMin.getValue() == null) dpFechaMin.setValue(LocalDate.now().minusYears(20L));
        if (dpFechaMax.getValue() == null) dpFechaMax.setValue(LocalDate.now());
        fechaMin = dpFechaMin.getValue();
        fechaMax = dpFechaMax.getValue();

        ReporteComparacionDTO dto = serviceServ.generarComparacion(fechaMin, fechaMax);
        if (dto.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("generar reporte",
                    "No se encontraron datos para generar el reporte.");
            return;
        }
        obsPie.add(new PieChart.Data("Ventas: " + dto.pctVenta() + " %", dto.ingVenta().doubleValue()));
        obsPie.add(new PieChart.Data("Services: " + dto.pctService() + " %", dto.ingService().doubleValue()));
        llenarLabels(dto);
    }

    @FXML
    private void exportar(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorImagenes.exportarJPEG(event, rootPane, "Comparación de ingresos");
        } else {
            NotificationHelper.mostrarAdvertencia("Exportar reporte", "No hay datos para exportar.");
        }
    }

    @FXML
    private void enviarMail() {
        String destinatario = SimpleDialogs.pedirMailParaEnviarReporte();
        try {
            ManejadorInputs.eMail(destinatario, true);
            enviarSnapshotPorCorreo(destinatario);
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Ingreso de dirección de correo", e.getMessage());
        }
    }

    private void enviarSnapshotPorCorreo(String destinatario) {
        try {
            // --- PASO A: TOMAR CAPTURA DEL CHART ---
            File tempFile = GeneradorImagenes.tomarScreenshotTemporalDeVista(rootPane);
            // --- PASO B: ENVIAR MAIL USANDO TU CLASE GENERADOR ---
            String asunto = "Reporte comparativo de ingresos";
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico comparativo de ingresos " +
                    "generado por el sistema.\n\n";

            eMailSender.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

            NotificationHelper.mostrarExito("Reporte enviado",
                    "El reporte se envió correctamente a " + destinatario);

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
        pieChart.getStylesheets().add(css);
    }
}
