package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.VentaRepuestosCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnAnioDTO;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ChartTotalVentasAnioController implements Initializable {

    private final VentaRepuestoServ ventaRepuestoServ;
    private final String[] nombresMesesAbreviados = {
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    @FXML
    private Spinner<Integer> spinnerAnio;
    @FXML
    private AreaChart<String, Number> chart;
    @FXML
    private Label lblIngresosTotales, lblTitulo;

    @Inject
    public ChartTotalVentasAnioController(VentaRepuestoServ ventaRepuestoServ) {
        this.ventaRepuestoServ = ventaRepuestoServ;
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
        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para exportar el reporte",
                "Reporte total ventas año " + spinnerAnio.getValue(),
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) return;

        try {
            WritableImage writableImage = chart.snapshot(new SnapshotParameters(), null);
            BufferedImage imageConTransparencia = SwingFXUtils.fromFXImage(writableImage, null);
            BufferedImage imageSinTransparencia = new BufferedImage(
                    imageConTransparencia.getWidth(),
                    imageConTransparencia.getHeight(),
                    BufferedImage.TYPE_INT_RGB); // Clave: RGB significa sin alfa

            Graphics2D graphics = imageSinTransparencia.createGraphics();
            graphics.setColor(Color.WHITE); // Establecer el color de fondo
            graphics.fillRect(0, 0, imageSinTransparencia.getWidth(), imageSinTransparencia.getHeight()); // Rellenar
            graphics.drawImage(imageConTransparencia, 0, 0, null);
            graphics.dispose(); // Liberar recursos gráficos
            boolean exito = ImageIO.write(imageSinTransparencia, "jpg", file);

            Alertas.exito("Exportar reporte", "Reporte exportado con éxito.");
        } catch (IOException ex) {
            System.err.println("Error al guardar la imagen del gráfico.");
            ex.printStackTrace();
            Alertas.error("Exportar reporte", "Ha ocurrido un error al exportar el reporte.");
        }
    }



    private void poblarChartCantidad(List<VentaRepuestosCantidadEnAnioDTO> ventasDTO) {
        if (!limpiaChartYVerificaDTO(ventasDTO)) return;

        // 1. Crear un mapa para acceder fácilmente a las ventas de cada mes.
        //    La clave es el número del mes (Integer), el valor es el monto (BigDecimal).
        Map<Integer, Long> ventasPorMes = new HashMap<>();
        for (VentaRepuestosCantidadEnAnioDTO dto : ventasDTO) {
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

    private void poblarChartIngresos(List<VentaRepuestosEnAnioDTO> ventasDTO) {
        if (!limpiaChartYVerificaDTO(ventasDTO)) return;

        // 1. Crear un mapa para acceder fácilmente a las ventas de cada mes.
        //    La clave es el número del mes (Integer), el valor es el monto (BigDecimal).
        Map<Integer, BigDecimal> ventasPorMes = new HashMap<>();
        for (VentaRepuestosEnAnioDTO dto : ventasDTO) {
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

    private <D> boolean limpiaChartYVerificaDTO(List<D> dtoList) {
        chart.getData().clear();
        if (dtoList == null) {
            Alertas.aviso("Generación de reporte", "Error al obtener los datos.");
            return false;
        }
        if (dtoList.isEmpty()) {
            Alertas.aviso("Generación de reporte", "No se encontraron registros para esa fecha.");
            return false;
        } else {
            Alertas.exito("Generación de reporte", "Se ha generado el reporte con éxito.");
            return true;
        }
    }

    private void configCampos() {
        spinnerAnio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1990, Year.now().getValue()));
        spinnerAnio.getValueFactory().setValue(Year.now().getValue());
    }
}
