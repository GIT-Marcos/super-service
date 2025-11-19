package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.services.RepuestoServ;
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
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ChartUsoDeRepuestosController implements Initializable {

    private final RepuestoServ repuestoServ;
    private ObservableList<PieChart.Data> obsPie = FXCollections.observableArrayList();

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
        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para exportar el reporte",
                "Reporte de uso de repuestos",
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) return;

        try {
            WritableImage writableImage = pieChart.snapshot(new SnapshotParameters(), null);
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

    private void configurarCampos() {
        fechaMin.setConverter(new SafeLocalDateConverter());
        fechaMax.setConverter(new SafeLocalDateConverter());

        pieChart.setData(obsPie);
    }
}
