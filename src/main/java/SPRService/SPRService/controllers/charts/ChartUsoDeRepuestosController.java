package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.services.RepuestoServ;
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
import javafx.scene.layout.BorderPane;

import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ChartUsoDeRepuestosController implements Initializable {

    private final RepuestoServ repuestoServ;
    private ObservableList<PieChart.Data> obsPie = FXCollections.observableArrayList();

    @FXML
    private BorderPane rootPane;
    @FXML
    private PieChart pieChart;
    @FXML
    private DatePicker fechaMin, fechaMax;

    @Inject
    public ChartUsoDeRepuestosController(RepuestoServ repuestoServ) {
        this.repuestoServ = repuestoServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCampos();
    }

    @FXML
    private void generar() {
        if (fechaMin.getValue() == null) fechaMin.setValue(LocalDate.of(1900, 1, 1));
        if (fechaMax.getValue() == null) fechaMax.setValue(LocalDate.now());
        ReporteUsoDeRepuestosDTO dto = repuestoServ.usoDeRepuestos(fechaMin.getValue(), fechaMax.getValue());
        obsPie.clear();
        pieChart.setTitle("Uso de repuestos");
        if (dto.paraService() != 0 && dto.paraVenta() != 0) {
            String titulo = "Uso de repuestos entre: " + fechaMin.getValue() + " y " + fechaMax.getValue() +
                    " - Total usados: " + dto.total();
            pieChart.setTitle(titulo);
            obsPie.add(new PieChart.Data("Ventas: " + dto.paraVenta() + " - " + dto.pctParaVenta() + " %",
                    dto.paraVenta()));
            obsPie.add(new PieChart.Data("Service: " + dto.paraService() + " - " + dto.pctParaService() + " %",
                    dto.paraService()));
        } else {
            Alertas.aviso("Reporte de uso", "No se encontraron notas de retiro entre esas fechas.");
        }
    }

    @FXML
    private void exportar(ActionEvent event) {
        if (!obsPie.isEmpty()) {
            GeneradorReportes.exportarJPEG(event, rootPane, "Reporte de usos de repuestos");
        } else {
            Alertas.aviso("Exportar reporte", "No hay datos para exportar.");
        }
    }

    private void configurarCampos() {
        fechaMin.setConverter(new SafeLocalDateConverter());
        fechaMax.setConverter(new SafeLocalDateConverter());

        pieChart.setData(obsPie);
    }
}
