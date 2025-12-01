package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.services.ServiceServ;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.apache.commons.mail.EmailException;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
            GeneradorImagenes.exportarJPEG(event, rootPane, "Comparación de ingresos");
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
            String asunto = "Reporte comparativo de ingresos";
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico comparativo de ingresos " +
                    "generado por el sistema.\n\n";

            eMailSender.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

            // --- PASO C: CONFIRMACIÓN Y LIMPIEZA ---
            Notifications.create()
                    .title("Reporte de comparación de ingresos")
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
