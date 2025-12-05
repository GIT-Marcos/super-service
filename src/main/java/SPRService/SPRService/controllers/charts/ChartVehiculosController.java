package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.services.VehiculoServ;
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

public class ChartVehiculosController implements Initializable {

    private final VehiculoServ vehiculoServ;
    private final EMailSender eMailSender;

    @FXML
    private BorderPane rootPane;

    @FXML
    private BarChart<Number, String> chart;
    @FXML
    private DatePicker dpFechaMin, dpFechaMax;
    @FXML
    private Spinner<Integer> spinner;

    @Inject
    public ChartVehiculosController(VehiculoServ vehiculoServ, EMailSender eMailSender) {
        this.vehiculoServ = vehiculoServ;
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configurar Spinner (1 a 20, defecto 10)
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 10);
        spinner.setValueFactory(valueFactory);

        // 2. Configurar Fechas (Por defecto último año, ya que los vehículos tienen menos rotación que repuestos)
        dpFechaMax.setValue(LocalDate.now());
        dpFechaMin.setValue(LocalDate.now().minusYears(1));

        // 3. Generar inicial
        generar();
    }

    @FXML
    private void generar() {
        LocalDate fechaMin = dpFechaMin.getValue();
        LocalDate fechaMax = dpFechaMax.getValue();
        Integer cantidad = spinner.getValue();

        if (fechaMin != null && fechaMax != null && fechaMin.isAfter(fechaMax)) {
            NotificationHelper.mostrarAdvertencia("Rango Incorrecto",
                    "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        try {
            // Llamada al servicio
            List<ModelosMasRegistradosDTO> datos = vehiculoServ.generarReporteModelosMasRegistrados(cantidad, fechaMin, fechaMax);

            chart.getData().clear();
            chart.layout(); // Forzar refresco layout

            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Cantidad Registrada");

            // Iteramos AL REVÉS para que el TOP 1 quede visualmente ARRIBA en el gráfico horizontal
            for (int i = datos.size() - 1; i >= 0; i--) {
                ModelosMasRegistradosDTO dto = datos.get(i);

                // Construimos la etiqueta para el eje Y
                // Ej: "Toyota Corolla 2010 (x5)"
                String labelEjeY = String.format("%s %s %s (x%d)",
                        dto.nombreMarca(),
                        dto.nombreModelo(),
                        dto.anioModelo(),
                        dto.cantidadRetirada());

                XYChart.Data<Number, String> data = new XYChart.Data<>(dto.cantidadRetirada(), labelEjeY);

                // Configuración del Tooltip para ver más detalles al pasar el mouse
                data.nodeProperty().addListener((observable, oldNode, newNode) -> {
                    if (newNode != null) {
                        String textoTooltip = String.format(
                                "Marca: %s\nModelo: %s\nAño: %s\nCilindrada: %s\nCantidad Total: %d",
                                dto.nombreMarca(),
                                dto.nombreModelo(),
                                dto.anioModelo(),
                                dto.cilindrada(),
                                dto.cantidadRetirada()
                        );

                        Tooltip tooltip = new Tooltip(textoTooltip);
                        tooltip.setStyle("-fx-font-size: 13px;");
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(newNode, tooltip);

                        // Cursor mano para indicar interactividad
                        newNode.setStyle("-fx-cursor: hand;");
                    }
                });

                series.getData().add(data);
            }

            chart.getData().add(series);

        } catch (Exception e) {
            NotificationHelper.mostrarError("Error al generar gráfico",
                    "No se pudieron cargar los datos de vehículos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void exportarJPG(ActionEvent event) {
        GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte_Vehiculos_Mas_Registrados");
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

            String asunto = "Reporte de Modelos de Vehículos más Registrados";
            String cuerpo = "Estimado,\n\n" +
                    "Adjunto encontrará el gráfico de los modelos de vehículos con mayor cantidad de registros/ingresos " +
                    "en el periodo seleccionado.\n\n" +
                    "Periodo: " + dpFechaMin.getValue() + " al " + dpFechaMax.getValue() + "\n" +
                    "Top: " + spinner.getValue() + " modelos.\n\n";

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
