package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.GeneradorMail;
import SPRService.SPRService.util.generadores.GeneradorReportes;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.mail.EmailException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Year;
import java.util.*;
import java.util.List;

//TODO: CONTROLADOR IDÉNTICO AL DE VENTA
public class ChartReportesAnualesServiceController implements Initializable {

    private final ServiceServ serviceServ;
    private ObservableList<PieChart.Data> obsPie = FXCollections.observableArrayList();
    private final String[] nombresMesesAbreviados = {
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label lblIngresosTotales, lblPromedioIngresosPorService, lblCantidadDeServicesAnio, lblIngresosPorTrabajos,
            lblIngresosPorRepuestos, lblPorcTrabajos, lblPorcRepuestos;
    @FXML
    private Spinner<Integer> spinnerAnio;
    @FXML
    private AreaChart<String, Number> chart;
    @FXML
    private PieChart pieChart;

    @Inject
    public ChartReportesAnualesServiceController(ServiceServ serviceServ) {
        this.serviceServ = serviceServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configCampos();
    }

    @FXML
    private void generarReporteIngresos() {
        obsPie.clear();
        int nroAnio = spinnerAnio.getValue();
        poblarChartIngresos(serviceServ.totalIngresosAnual(nroAnio));
    }

    @FXML
    private void generarReporteCantidad() {
        obsPie.clear();
        int nroAnio = spinnerAnio.getValue();
        poblarChartCantidad(serviceServ.cantidadDeServicesAnual(nroAnio));
    }

    @FXML
    private void exportarJPG(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorReportes.exportarJPEG(event, rootPane, "Reporte anual service");
        } else {
            Alertas.aviso("Exportar reporte", "No hay datos para exportar.");
        }
    }

    @FXML
    private void enviarMail() {
        // 2. Pedir el correo del destinatario mediante un Dialogo
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enviar Reporte por Correo");
        dialog.setHeaderText("Enviar gráfico actual");
        dialog.setContentText("Ingrese el correo del destinatario:");

        Optional<String> result = dialog.showAndWait();

        // Si el usuario ingresó algo y dio OK
        if (result.isPresent()) {
            String emailDestino = result.get();
            if (emailDestino.isBlank()) {
                Alertas.error("Error", "El correo no puede estar vacío.");
                return;
            }
            if (emailDestino.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$\n")){
                Alertas.error("Error", "El correo está en mal formato.");
                return;
            }

            enviarSnapshotPorCorreo(emailDestino);
        }
    }

    private void enviarSnapshotPorCorreo(String destinatario) {
        try {
            // --- PASO A: TOMAR CAPTURA DEL CHART ---
            // Creamos un archivo temporal (se borra al cerrar la app o manualmente)
            File tempFile = File.createTempFile("reporte_temp_", ".jpg");

            // Tomamos la foto SOLAMENTE del PieChart (o puedes usar el nodo padre)
            WritableImage writableImage = rootPane.snapshot(new SnapshotParameters(), null);

            // Convertimos a formato compatible con guardado
            BufferedImage imageConTransparencia = SwingFXUtils.fromFXImage(writableImage, null);

            // Quitamos transparencia (fondo negro a blanco) para que se vea bien en JPEG
            BufferedImage imageBlanca = new BufferedImage(
                    imageConTransparencia.getWidth(),
                    imageConTransparencia.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = imageBlanca.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imageBlanca.getWidth(), imageBlanca.getHeight());
            graphics.drawImage(imageConTransparencia, 0, 0, null);
            graphics.dispose();

            // Guardamos en el archivo temporal
            ImageIO.write(imageBlanca, "jpg", tempFile);

            // --- PASO B: ENVIAR MAIL USANDO TU CLASE GENERADOR ---
            String asunto = "Reporte de service anual - " + spinnerAnio.getValue();
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de service anual " +
                    "generado por el sistema.\n\n";

            GeneradorMail.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

            // --- PASO C: CONFIRMACIÓN Y LIMPIEZA ---
            Alertas.exito("Envío Exitoso", "El reporte se envió correctamente a " + destinatario);

            // Opcional: borrar el archivo temporal inmediatamente
            tempFile.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
            Alertas.error("Error IO", "No se pudo generar la imagen temporal: " + e.getMessage());
        } catch (EmailException e) {
            e.printStackTrace();
            Alertas.error("Error Mail", "Fallo al enviar el correo. Verifique su conexión o configuración: " + e.getMessage());
        }
    }

    private void poblarLabels() {
        int ano = spinnerAnio.getValue();
        DatosReporteServiceDTO dto = serviceServ.generarDatosAnuales(ano);
        lblIngresosTotales.setText("$ " + dto.ingTotales());
        lblPromedioIngresosPorService.setText(
                "$ " + String.format("%.2f", dto.ingPromedioPorService())
        );
        lblCantidadDeServicesAnio.setText(dto.cantidadDeService().toString());
        lblIngresosPorTrabajos.setText("$ " + dto.ingPorTrabajos());
        lblIngresosPorRepuestos.setText("$ " + dto.ingPorRepuestos());

        BigDecimal ingTotal = dto.ingPorTrabajos().add(dto.ingPorRepuestos());
        BigDecimal cien = new BigDecimal("100");

        // Manejo para evitar división por cero si ingTotal es cero.
        if (ingTotal.compareTo(BigDecimal.ZERO) == 0) {
            // Si no hay ingresos, los porcentajes son 0.0
            double pctTrabajos = 0.0;
            double pctRepuestos = 0.0;

            obsPie.add(new PieChart.Data("Trabajos", pctTrabajos));
            obsPie.add(new PieChart.Data("Repuestos", pctRepuestos));

            lblPorcTrabajos.setText(pctTrabajos + " %");
            lblPorcRepuestos.setText(pctRepuestos + " %");

        } else {
            // Porcentaje de ingresos por trabajos: (Ing. Trabajos / Ing. Total) * 100
            double pctTrabajos = dto.ingPorTrabajos()
                    .divide(ingTotal, 2, RoundingMode.HALF_UP) // 2 decimales para el porcentaje
                    .multiply(cien).doubleValue();

            // Porcentaje de ingresos por repuestos: (Ing. Repuestos / Ing. Total) * 100
            double pctRepuestos = dto.ingPorRepuestos()
                    .divide(ingTotal, 2, RoundingMode.HALF_UP) // 2 decimales para el porcentaje
                    .multiply(cien).doubleValue();

            // 3. Cargar los datos al PieChart
            // Nota: El PieChart usa los valores numéricos directos (0-100 en este caso),
            // y la suma de sus valores define el 100% del círculo.
            obsPie.add(new PieChart.Data("Trabajos", pctTrabajos));
            obsPie.add(new PieChart.Data("Repuestos", pctRepuestos));

            // 4. Mostrar los porcentajes en las etiquetas
            // Utilizamos String.format para asegurar que se muestren 2 decimales
            lblPorcTrabajos.setText("Trabajos: " + String.format("%.2f %%", pctTrabajos));
            lblPorcRepuestos.setText("Repuestos: " + String.format("%.2f %%", pctRepuestos));
        }
    }

    private void poblarChartIngresos(List<ReporteIngresosEnAnioPorMesDTO> dtos) {
        if (!limpiaChartYVerificaDTO(dtos)) return;

        // 1. Crear un mapa para acceder fácilmente a las ventas de cada mes.
        //    La clave es el número del mes (Integer), el valor es el monto (BigDecimal).
        Map<Integer, BigDecimal> ventasPorMes = new HashMap<>();
        for (ReporteIngresosEnAnioPorMesDTO dto : dtos) {
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

    private boolean limpiaChartYVerificaDTO(List<?> dtoList) {
        chart.getData().clear();
        if (dtoList == null) {
            Alertas.aviso("Generación de reporte", "Error al obtener los datos.");
            return false;
        }
        if (dtoList.isEmpty()) {
            Alertas.aviso("Generación de reporte", "No se encontraron registros para esa fecha.");
            restableceLabels();
            return false;
        } else {
            Alertas.exito("Generación de reporte", "Se ha generado el reporte con éxito.");
            poblarLabels();
            return true;
        }
    }

    private void restableceLabels() {
        lblIngresosTotales.setText("*sin datos");
        lblPromedioIngresosPorService.setText("*sin datos");
        lblCantidadDeServicesAnio.setText("*sin datos");
        lblIngresosPorTrabajos.setText("*sin datos");
        lblIngresosPorRepuestos.setText("*sin datos");
        lblPorcTrabajos.setText("% Trabajos");
        lblPorcRepuestos.setText("% Repuestos");
    }

    private void configCampos() {
        spinnerAnio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1990, Year.now().getValue()));
        spinnerAnio.getValueFactory().setValue(Year.now().getValue());

        pieChart.setData(obsPie);
        String css = getClass().getResource("/styles/pieChart.css").toExternalForm();
        pieChart.getStylesheets().add(css);
    }
}
