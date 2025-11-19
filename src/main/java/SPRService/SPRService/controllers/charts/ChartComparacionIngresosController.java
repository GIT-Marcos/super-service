package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.GeneradorReportes;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartComparacionIngresosController implements Initializable {

    private final ServiceServ serviceServ;
    private ObservableList<PieChart.Data> obsPie = FXCollections.observableArrayList();

    @FXML
    private BorderPane rootPane;
    @FXML
    private DatePicker dpFechaMin, dpFechaMax;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label lblCantService, lblCantVentas, lblCantOperaciones, lblIngresosTotales, lblIngVentas, lblIngService;

    @Inject
    public ChartComparacionIngresosController(ServiceServ serviceServ) {
        this.serviceServ = serviceServ;
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
            Alertas.aviso("Generar reporte", "No se encontraron datos para generar el reporte.");
            return;
        }
        obsPie.add(new PieChart.Data("Ventas: " + dto.pctVenta() + " %", dto.ingVenta().doubleValue()));
        obsPie.add(new PieChart.Data("Services: " + dto.pctService() + " %", dto.ingService().doubleValue()));
        llenarLabels(dto);
    }

    @FXML
    private void exportar(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorReportes.exportarJPEG(event, rootPane, "Comparación de ingresos");
        } else {
            Alertas.aviso("Exportar reporte", "No hay datos para exportar.");
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
