package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteCantidadEnAnioDTO;
import SPRService.SPRService.DTOs.ReporteIngresosEnAnioPorMesDTO;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.GeneradorReportes;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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

    private void poblarLabels() {
        int ano = spinnerAnio.getValue();
        DatosReporteServiceDTO dto = serviceServ.generarDatosAnuales(ano);
        lblIngresosTotales.setText("$ " + dto.ingTotales());
        lblPromedioIngresosPorService.setText("$ " + dto.ingPromedioPorService());
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
