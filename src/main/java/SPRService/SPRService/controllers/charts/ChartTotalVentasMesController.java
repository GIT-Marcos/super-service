package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.apache.commons.mail.EmailException;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.ResourceBundle;

public class ChartTotalVentasMesController implements Initializable {

    private final VentaRepuestoServ ventaRepuestoServ;
    private final EMailSender eMailSender;

    @FXML
    private BorderPane rootPane;
    @FXML
    private BarChart<String, Number> chart;
    @FXML
    private ComboBox<String> comboMeses;
    @FXML
    private Spinner<Integer> spinnerAnio;

    @Inject
    public ChartTotalVentasMesController(VentaRepuestoServ ventaRepuestoServ, EMailSender eMailSender) {
        this.ventaRepuestoServ = ventaRepuestoServ;
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configCampos();
    }

    @FXML
    private void generar() {
        int nroMes = comboMeses.getSelectionModel().getSelectedIndex() + 1;
        Integer nroAnio = spinnerAnio.getValue();
        poblarChart(ventaRepuestoServ.reporteTotalVentasEnMes(nroAnio, nroMes));
    }

    @FXML
    private void exportarJPG(ActionEvent event) {
        GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte ingresos ventas en mes");
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
            String asunto = "Reporte de ventas mensuales - " + spinnerAnio.getValue();
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de ventas mensual " +
                    "generado por el sistema.\n\n";

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

    private void poblarChart(List<VentaRepuestosEnMesDTO> ventasDTO) {
        chart.getData().clear();

        if (ventasDTO == null) {
            Notifications.create()
                    .title("Generación de reporte")
                    .text("Error al obtener los datos.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showError();
            return;
        }
        if (ventasDTO.isEmpty()) {
            Notifications.create()
                    .title("Generación de reporte")
                    .text("No se encontraron registros para esa fecha.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
            return;
        } else {
            Notifications.create()
                    .title("Generación de reporte")
                    .text("Se ha generado el reporte con éxito.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showInformation();
        }

        // 1. Determinar el rango de días del mes.
        int anio = spinnerAnio.getValue();
        int mes = comboMeses.getSelectionModel().getSelectedIndex() + 1;
        int diasDelMes = YearMonth.of(anio, mes).lengthOfMonth();

        // 2. Preparar la serie y el puntero para la lista de DTOs.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total de ventas diarias en " + comboMeses.getValue());

        int dtoIndex = 0; // Puntero para la lista de DTOs.

        // 3. Iterar por TODOS los días del mes.
        for (int diaActual = 1; diaActual <= diasDelMes; diaActual++) {
            BigDecimal montoDelDia = BigDecimal.ZERO;

            // Comprobar si todavía hay DTOs por procesar y si el DTO actual corresponde al día de hoy.
            if (dtoIndex < ventasDTO.size() && ventasDTO.get(dtoIndex).dia() == diaActual) {
                // Coincide: tomamos el monto y avanzamos el puntero.
                montoDelDia = ventasDTO.get(dtoIndex).totalVendido();
                dtoIndex++;
            }

            // Añadimos el dato al gráfico (ya sea el monto real o cero).
            series.getData().add(new XYChart.Data<>(String.valueOf(diaActual), montoDelDia));
        }

        // 4. Añadir la serie completa al gráfico.
        chart.getData().add(series);
    }

    private void configCampos() {
        ObservableList<String> meses = FXCollections.observableArrayList(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );
        comboMeses.setItems(meses);
        comboMeses.getSelectionModel().select(0);

        spinnerAnio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1990, Year.now().getValue()));
        spinnerAnio.getValueFactory().setValue(Year.now().getValue());
    }
}
