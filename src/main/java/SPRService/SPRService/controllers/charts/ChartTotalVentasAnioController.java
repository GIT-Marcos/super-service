package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.generadores.GeneradorImagenes;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
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
import java.util.*;
import java.util.List;

public class ChartTotalVentasAnioController implements Initializable {

    private final VentaRepuestoServ ventaRepuestoServ;
    private final EMailSender eMailSender;
    private final String[] nombresMesesAbreviados = {
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    @FXML
    private BorderPane rootPane;
    @FXML
    private Spinner<Integer> spinnerAnio;
    @FXML
    private AreaChart<String, Number> chart;
    @FXML
    private Label lblIngresosTotales, lblTitulo, lblCantidadDeVentasAnio, lblPromedioIngresosPorVenta;

    @Inject
    public ChartTotalVentasAnioController(VentaRepuestoServ ventaRepuestoServ, EMailSender eMailSender) {
        this.ventaRepuestoServ = ventaRepuestoServ;
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configCampos();
    }

    @FXML
    private void generarReporteIngresos() {
        int nroAnio = spinnerAnio.getValue();
        this.poblarChartIngresos(ventaRepuestoServ.reporteTotalVentasEnAnio(nroAnio));
    }

    @FXML
    private void generarReporteCantidad() {
        int nroAnio = spinnerAnio.getValue();
        poblarChartCantidad(ventaRepuestoServ.reporteCantidadVentasEnAnio(nroAnio));
    }

    @FXML
    private void exportarJPG(ActionEvent event) {
        GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte ingresos de ventas en año");
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
            String asunto = "Reporte de ventas anuales - " + spinnerAnio.getValue();
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de ventas anual " +
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

    private void llenarInfoDelAnio() {
        int anio = spinnerAnio.getValue();
        lblTitulo.setText("Año: " + anio);
        lblCantidadDeVentasAnio.setText(ventaRepuestoServ.cantidadDeVentasEnAnio(anio) + " ventas");
        lblIngresosTotales.setText("$ " + ventaRepuestoServ.ingresosDeVentasEnAnio(anio));
        lblPromedioIngresosPorVenta.setText("$ " + ventaRepuestoServ.ingresosPromedioPorVentaEnAnio(anio));
    }

    private void poblarChartCantidad(List<ReporteCantidadEnAnioDTO> ventasDTO) {
        if (!limpiaChartYVerificaDTO(ventasDTO)) return;

        // 1. Crear un mapa para acceder fácilmente a las ventas de cada mes.
        //    La clave es el número del mes (Integer), el valor es el monto (BigDecimal).
        Map<Integer, Long> ventasPorMes = new HashMap<>();
        for (ReporteCantidadEnAnioDTO dto : ventasDTO) {
            ventasPorMes.put(dto.nroMes(), dto.cantidadVentas());
        }

        // 3. Crear la serie de datos, iterando por los 12 meses del año.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("CANTIDAD de ventas por mes en " + spinnerAnio.getValue());

        for (int mes = 1; mes <= 12; mes++) {
            // Obtenemos el monto del mapa. Si no existe, usamos cero.
            Long monto = ventasPorMes.getOrDefault(mes, 0L);
            // Usamos el array de abreviaturas para la etiqueta del eje X.
            String etiquetaMes = nombresMesesAbreviados[mes - 1]; // mes 1 -> índice 0
            // Añadimos el dato al gráfico.
            series.getData().add(new XYChart.Data<>(etiquetaMes, monto));
        }

        // 4. Añadir la serie completa al gráfico.
        chart.getData().add(series);
    }

    private void poblarChartIngresos(List<ReporteIngresosEnAnioPorMesDTO> ventasDTO) {
        if (!limpiaChartYVerificaDTO(ventasDTO)) return;

        // 1. Crear un mapa para acceder fácilmente a las ventas de cada mes.
        //    La clave es el número del mes (Integer), el valor es el monto (BigDecimal).
        Map<Integer, BigDecimal> ventasPorMes = new HashMap<>();
        for (ReporteIngresosEnAnioPorMesDTO dto : ventasDTO) {
            ventasPorMes.put(dto.nroMes(), dto.totalVendido());
        }

        // 3. Crear la serie de datos, iterando por los 12 meses del año.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total INGRESOS de ventas por mes en " + spinnerAnio.getValue());

        for (int mes = 1; mes <= 12; mes++) {
            // Obtenemos el monto del mapa. Si no existe, usamos cero.
            BigDecimal monto = ventasPorMes.getOrDefault(mes, BigDecimal.ZERO);
            // Usamos el array de abreviaturas para la etiqueta del eje X.
            String etiquetaMes = nombresMesesAbreviados[mes - 1]; // mes 1 -> índice 0
            // Añadimos el dato al gráfico.
            series.getData().add(new XYChart.Data<>(etiquetaMes, monto));
        }

        // 4. Añadir la serie completa al gráfico.
        chart.getData().add(series);
    }

    private boolean limpiaChartYVerificaDTO(List<?> dtoList) {
        chart.getData().clear();
        if (dtoList == null) {
            Notifications.create()
                    .title("Generación de reporte")
                    .text("Error al obtener los datos.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showError();
            return false;
        }
        if (dtoList.isEmpty()) {
            Notifications.create()
                    .title("Generación de reporte")
                    .text("No se encontraron registros para esa fecha.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showWarning();
            return false;
        } else {
            Notifications.create()
                    .title("Generación de reporte")
                    .text("Se ha generado el reporte con éxito.")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .showInformation();
            llenarInfoDelAnio();
            return true;
        }
    }

    private void configCampos() {
        spinnerAnio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1990, Year.now().getValue()));
        spinnerAnio.getValueFactory().setValue(Year.now().getValue());
    }
}
