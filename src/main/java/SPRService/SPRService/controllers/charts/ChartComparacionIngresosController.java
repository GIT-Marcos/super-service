package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        if (obsPie.isEmpty()) {
            Alertas.aviso("Exportar reporte", "No hay datos para exportar.");
            return;
        }
        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para exportar el reporte",
                "Reporte comparativo",
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) return;

        try {
            // 1) Snapshot de TODA la ventana
            WritableImage writableImage = rootPane.snapshot(new SnapshotParameters(), null);
            // 2) Convertir la imagen a BufferedImage
            BufferedImage imageConTransparencia = SwingFXUtils.fromFXImage(writableImage, null);
            // 3) Crear imagen sin canal alfa (fondo blanco)
            BufferedImage imageSinTransparencia = new BufferedImage(
                    imageConTransparencia.getWidth(),
                    imageConTransparencia.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D graphics = imageSinTransparencia.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imageSinTransparencia.getWidth(), imageSinTransparencia.getHeight());
            graphics.drawImage(imageConTransparencia, 0, 0, null);
            graphics.dispose();

            // 4) Guardar como JPG
            ImageIO.write(imageSinTransparencia, "jpg", file);
            Alertas.exito("Exportar reporte", "Reporte exportado con éxito.");
        } catch (IOException ex) {
            ex.printStackTrace();
            Alertas.error("Exportar reporte", "Ocurrió un error al exportar el reporte.");
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
        String css = getClass().getResource("/styles/pieChartColores.css").toExternalForm();;
        pieChart.getStylesheets().add(css);
    }
}
