package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.services.VentaRepuestoServ;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import org.apache.commons.mail.EmailException;

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
import java.util.Optional;
import java.util.ResourceBundle;

public class ChartTotalVentasMesController implements Initializable {

    private final VentaRepuestoServ ventaRepuestoServ;

    @FXML
    private BorderPane rootPane;
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
        GeneradorReportes.exportarJPEG(event, rootPane, "Reporte ingresos ventas en mes");
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
            String asunto = "Reporte de ventas mensuales - " + spinnerAnio.getValue();
            String cuerpo = "Estimado,\n\nAdjunto encontrará el gráfico de ventas mensual " +
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
