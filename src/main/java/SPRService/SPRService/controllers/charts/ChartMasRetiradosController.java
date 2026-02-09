package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.GeneradorImagenes;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.apache.commons.mail.EmailException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ChartMasRetiradosController implements Initializable {

    private final RepuestoServ repuestoServ;
    private final EMailSender eMailSender;

    @FXML
    private BorderPane rootPane;

    // CAMBIO: Invertido a <Number, String> para grafico horizontal
    @FXML
    private BarChart<Number, String> chart;

    @FXML
    private DatePicker dpFechaMin, dpFechaMax;
    @FXML
    private Spinner<Integer> spinner;


    @Inject
    public ChartMasRetiradosController(RepuestoServ repuestoServ, EMailSender eMailSender) {
        this.repuestoServ = repuestoServ;
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configurar el Spinner
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 10);
        spinner.setValueFactory(valueFactory);
    }

    @FXML
    private void generar() {
        // 1. Obtener parámetros
        LocalDate fechaMin;
        LocalDate fechaMax;
        if (dpFechaMin.getValue() == null) dpFechaMin.setValue(LocalDate.now().minusYears(20L));
        if (dpFechaMax.getValue() == null) dpFechaMax.setValue(LocalDate.now());
        fechaMin = dpFechaMin.getValue();
        fechaMax = dpFechaMax.getValue();
        Integer cantidad = spinner.getValue();

        // 2. Validar
        if (fechaMin != null && fechaMax != null && fechaMin.isAfter(fechaMax)) {
            NotificationHelper.mostrarAdvertencia("Rango de Fechas",
                    "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        try {
            // 3. Llamar al servicio
            List<RepuestoRetiradoReporteDTO> datos = repuestoServ.repuestosMasRetiradosParaVenta(cantidad, fechaMin, fechaMax);

            // 4. Limpiar gráfico
            chart.getData().clear();
            chart.layout();

            // 5. Crear la serie <Number, String>
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Cantidad de Retiros");

            // CAMBIO: Iterar al revés para que el Top 1 quede arriba visualmente
            for (int i = datos.size() - 1; i >= 0; i--) {
                RepuestoRetiradoReporteDTO dto = datos.get(i);

                // Formato etiqueta: "Fram Filtro (x15)"
                String etiqueta = String.format("%s %s (x%d)",
                        dto.marca(), dto.detalle(), dto.cantidad());

                // XYChart.Data(ValorX, ValorY) -> (Cantidad, Nombre)
                XYChart.Data<Number, String> data = new XYChart.Data<>(dto.cantidad(), etiqueta);

                // Opcional: Agregar Tooltip para detalles
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        Tooltip t = new Tooltip(
                                "Repuesto: " + dto.detalle() +
                                        "\nMarca: " + dto.marca() +
                                        "\nRetirado: " + dto.cantidad() + " veces"
                        );
                        t.setStyle("-fx-font-size: 13px;");
                        t.setShowDelay(Duration.millis(100));
                        Tooltip.install(newNode, t);
                        newNode.setStyle("-fx-cursor: hand;");
                    }
                });

                series.getData().add(data);
            }

            // 6. Agregar la serie al gráfico
            chart.getData().add(series);
            NotificationHelper.mostrarExito("Ingresos por repuesto", "Se ha generado el reporte con éxito.");
        } catch (Exception e) {
            NotificationHelper.mostrarError("Error al generar gráfico",
                    "Hubo un problema cargando los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void exportarJPG(ActionEvent event) {
        GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte repuestos mas retirados");
    }

    @FXML
    private void enviarMail() {
        String destinatario = SimpleDialogs.pedirMailParaEnviarReporte();
        if (destinatario == null || destinatario.isBlank()) return;

        try {
            ManejadorInputs.eMail(destinatario, true);
            enviarSnapshotPorCorreo(destinatario);
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Ingreso de dirección de correo", e.getMessage());
        }
    }

    private void enviarSnapshotPorCorreo(String destinatario) {
        try {
            File tempFile = GeneradorImagenes.tomarScreenshotTemporalDeVista(rootPane);

            String asunto = "Reporte de repuestos más retirados";
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de los repuestos más retirados " +
                    "para venta en el periodo seleccionado.\n\n" +
                    "Periodo: " + dpFechaMin.getValue() + " al " + dpFechaMax.getValue();

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
}