package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteIngresosRepuestoDTO;
import SPRService.SPRService.services.VentaRepuestoServ;
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
import org.apache.commons.mail.EmailException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ChartIngresosRepuestoController implements Initializable {

    private final VentaRepuestoServ ventaRepuestoServ;
    private final EMailSender eMailSender;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));

    @FXML
    private BorderPane rootPane;
    @FXML
    private BarChart<Number, String> chart;
    @FXML
    private DatePicker dpFechaMin, dpFechaMax;
    @FXML
    private Spinner<Integer> spinner;

    @Inject
    public ChartIngresosRepuestoController(VentaRepuestoServ ventaRepuestoServ, EMailSender eMailSender) {
        this.ventaRepuestoServ = ventaRepuestoServ;
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 10);
        spinner.setValueFactory(valueFactory);
    }

    @FXML
    private void generar() {
        LocalDate fechaMin;
        LocalDate fechaMax;
        if (dpFechaMin.getValue() == null) dpFechaMin.setValue(LocalDate.now().minusYears(20L));
        if (dpFechaMax.getValue() == null) dpFechaMax.setValue(LocalDate.now());
        fechaMin = dpFechaMin.getValue();
        fechaMax = dpFechaMax.getValue();
        Integer cantidad = spinner.getValue();

        // Validaciones...
        if (fechaMin != null && fechaMax != null && fechaMin.isAfter(fechaMax)) {
            NotificationHelper.mostrarAdvertencia("Rango Incorrecto",
                    "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        try {
            List<ReporteIngresosRepuestoDTO> datos = ventaRepuestoServ.ingresosPorRepuesto(fechaMin, fechaMax, cantidad);

            chart.getData().clear();
            chart.layout();

            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Ingresos Generados ($)");

            // Iteramos al revés para que el TOP 1 quede arriba
            for (int i = datos.size() - 1; i >= 0; i--) {
                ReporteIngresosRepuestoDTO dto = datos.get(i);

                // ESTRATEGIA 1: Poner la cantidad en la etiqueta del eje Y
                // Ej: "Toyota - Filtro (x12)"
                String labelEjeY = String.format("%s - %s (x%d)",
                        dto.marca(),
                        dto.detalle(),
                        dto.cantidadVendida().intValue());

                // Creamos el dato
                XYChart.Data<Number, String> data = new XYChart.Data<>(dto.IngresosGenerados(), labelEjeY);

                // ESTRATEGIA 2: Tooltip (Globo de información al pasar el mouse)
                // Necesitamos un listener porque el 'Node' de la barra se crea un instante después de añadir el dato
                data.nodeProperty().addListener((observable, oldNode, newNode) -> {
                    if (newNode != null) {
                        String textoTooltip = String.format(
                                "Repuesto: %s\nMarca: %s\nUnidades vendidas: %d\nIngresos Totales: %s",
                                dto.detalle(),
                                dto.marca(),
                                dto.cantidadVendida().intValue(),
                                currencyFormat.format(dto.IngresosGenerados())
                        );

                        Tooltip tooltip = new Tooltip(textoTooltip);
                        // Estilo opcional para el tooltip
                        tooltip.setStyle("-fx-font-size: 14px;");
                        tooltip.setShowDelay(javafx.util.Duration.millis(100));
                        Tooltip.install(newNode, tooltip);

                        // Opcional: Cambiar el color del cursor al pasar por encima
                        newNode.setStyle("-fx-cursor: hand;");
                    }
                });

                series.getData().add(data);
            }

            chart.getData().add(series);
            NotificationHelper.mostrarExito("Ingresos por repuesto", "Se ha generado el reporte con éxito.");
        } catch (Exception e) {
            NotificationHelper.mostrarError("Error al generar gráfico",
                    "No se pudieron cargar los ingresos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void exportarJPG(ActionEvent event) {
        GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte_Ingresos_Repuestos");
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

            String asunto = "Reporte de Ingresos por Repuestos";
            String cuerpo = "Estimado,\n\n" +
                    "Adjunto encontrará el gráfico de los repuestos que más ingresos han generado " +
                    "en el periodo seleccionado.\n\n" +
                    "Periodo: " + dpFechaMin.getValue() + " al " + dpFechaMax.getValue() + "\n" +
                    "Top: " + spinner.getValue() + " repuestos.\n\n";

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