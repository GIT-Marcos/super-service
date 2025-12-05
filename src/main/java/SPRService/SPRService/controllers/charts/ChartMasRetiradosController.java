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
import javafx.scene.layout.BorderPane;
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
    @FXML
    private BarChart<String, Number> chart; // Cambiado a Number para ser mas flexible (Long/Integer)
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
        // 1. Configurar el Spinner (Min: 1, Max: 15, Valor Inicial: 5)
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 5);
        spinner.setValueFactory(valueFactory);

        // 2. Configurar fechas por defecto (ej: Último mes o Año actual)
        dpFechaMax.setValue(LocalDate.now());
        dpFechaMin.setValue(LocalDate.now().minusMonths(3)); // Últimos 3 meses por defecto

        // 3. Cargar datos iniciales
        generar();
    }

    @FXML
    private void generar() {
        // 1. Obtener parámetros de la vista
        LocalDate fechaMin = dpFechaMin.getValue();
        LocalDate fechaMax = dpFechaMax.getValue();
        Integer cantidad = spinner.getValue();

        // 2. Validar fechas básicas
        if (fechaMin != null && fechaMax != null && fechaMin.isAfter(fechaMax)) {
            NotificationHelper.mostrarAdvertencia("Rango de Fechas",
                    "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        try {
            // 3. Llamar al servicio
            List<RepuestoRetiradoReporteDTO> datos = repuestoServ.repuestosMasRetiradosParaVenta(cantidad, fechaMin, fechaMax);

            // 4. Limpiar el gráfico anterior
            chart.getData().clear();
            chart.layout(); // Forzar refresco del layout

            // 5. Crear la serie de datos
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Cantidad de Retiros");

            for (RepuestoRetiradoReporteDTO dto : datos) {
                // Construimos una etiqueta legible para el eje X
                // Ej: "Fram - Filtro Aceite" o solo el detalle si prefieres
                String etiqueta = dto.detalle();

                // Agregamos el dato (Etiqueta, Cantidad)
                series.getData().add(new XYChart.Data<>(etiqueta, dto.cantidad()));
            }

            // 6. Agregar la serie al gráfico
            chart.getData().add(series);

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
        // Si el usuario cancela el dialogo, destinatario suele ser null o vacío
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
            // --- PASO A: TOMAR CAPTURA DEL CHART ---
            File tempFile = GeneradorImagenes.tomarScreenshotTemporalDeVista(rootPane);

            // --- PASO B: ENVIAR MAIL ---
            String asunto = "Reporte de repuestos más retirados";
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de los repuestos más retirados " +
                    "para venta en el periodo seleccionado.\n\n" +
                    "Periodo: " + dpFechaMin.getValue() + " al " + dpFechaMax.getValue();

            eMailSender.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

            // --- PASO C: CONFIRMACIÓN Y LIMPIEZA ---
            NotificationHelper.mostrarExito("Reporte enviado",
                    "El reporte se envió correctamente a " + destinatario);

            tempFile.deleteOnExit();
        } catch (IOException e) {
            NotificationHelper.mostrarError("Error de IO",
                    "No se pudo generar la imagen temporal: " + e.getMessage());
            e.printStackTrace();
        } catch (EmailException e) {
            NotificationHelper.mostrarError("Error Mail",
                    "Fallo al enviar el correo. Verifique su conexión o configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}