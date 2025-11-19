package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.services.VentaRepuestoServ;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
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
import java.time.YearMonth;
import java.util.List;
import java.util.ResourceBundle;

public class ChartTotalVentasMesController implements Initializable {

    private final VentaRepuestoServ ventaRepuestoServ;

    @FXML
    private BarChart<String, Number> chart;
    @FXML
    private ComboBox<String> comboMeses;
    @FXML
    private Spinner<Integer> spinnerAnio;

    @Inject
    public ChartTotalVentasMesController(VentaRepuestoServ ventaRepuestoServ) {
        this.ventaRepuestoServ = ventaRepuestoServ;
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
        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para exportar el reporte",
                "Reporte total ventas mes " + comboMeses.getSelectionModel().getSelectedItem() +
                        " año " + spinnerAnio.getValue(),
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) return;

        try {
            // 1. Tomar el snapshot del gráfico
            WritableImage writableImage = chart.snapshot(new SnapshotParameters(), null);

            // 2. Convertir a una imagen de AWT (puede tener transparencia)
            BufferedImage imageConTransparencia = SwingFXUtils.fromFXImage(writableImage, null);

            // --- LA SOLUCIÓN ESTÁ AQUÍ ---
            // 3. Crear una nueva imagen sin canal alfa (tipo RGB) y con fondo blanco
            BufferedImage imageSinTransparencia = new BufferedImage(
                    imageConTransparencia.getWidth(),
                    imageConTransparencia.getHeight(),
                    BufferedImage.TYPE_INT_RGB); // Clave: RGB significa sin alfa

            // 4. Dibujar la imagen original sobre el fondo blanco
            Graphics2D graphics = imageSinTransparencia.createGraphics();
            graphics.setColor(Color.WHITE); // Establecer el color de fondo
            graphics.fillRect(0, 0, imageSinTransparencia.getWidth(), imageSinTransparencia.getHeight()); // Rellenar
            graphics.drawImage(imageConTransparencia, 0, 0, null);
            graphics.dispose(); // Liberar recursos gráficos

            // 5. Guardar la nueva imagen (sin transparencia) y verificar el resultado
            boolean exito = ImageIO.write(imageSinTransparencia, "jpg", file);

            Alertas.exito("Exportar reporte", "Reporte exportado con éxito.");
        } catch (IOException ex) {
            System.err.println("Error al guardar la imagen del gráfico.");
            ex.printStackTrace();
            Alertas.error("Exportar reporte", "Ha ocurrido un error al exportar el reporte.");
        }
    }

    private void poblarChart(List<VentaRepuestosEnMesDTO> ventasDTO) {
        chart.getData().clear();

        if (ventasDTO == null) {
            Alertas.aviso("Generación de reporte", "Error al obtener los datos.");
            return;
        }
        if (ventasDTO.isEmpty()) {
            Alertas.aviso("Generación de reporte", "No se encontraron registros para esa fecha.");
            return;
        } else {
            Alertas.exito("Generación de reporte", "Se ha generado el reporte con éxito.");
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
