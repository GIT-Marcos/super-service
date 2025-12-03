package SPRService.SPRService.util.generadores;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.util.SimpleDialogs;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

public class GeneradorImagenes {

    /**
     * Genera un File temporal con un screenshot para ser enviado por mail. Usado para enviar los reportes por mail.
     *
     * @param panel al que se le toma la screenshot.
     * @return File temporal de la screenshot. Borrar luego de enviar.
     * @throws IOException sí falla la creación del archivo.
     */
    public static File tomarScreenshotTemporalDeVista(Pane panel) throws IOException {
        // --- PASO A: TOMAR CAPTURA DEL CHART ---
        // Creamos un archivo temporal (se borra al cerrar la app o manualmente)
        File tempFile = File.createTempFile("reporte_temp_", ".jpg");

        // Tomamos la foto SOLAMENTE del PieChart (o puedes usar el nodo padre)
        WritableImage writableImage = panel.snapshot(new SnapshotParameters(), null);

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
        return tempFile;
    }

    /**
     * Toma un captura de pantalla y la guarda como JPEG.
     *
     * @param n nodo sobre el que se toma la captura de pantalla.
     */
    public static void exportarJPEG(ActionEvent event, Node n, String nombreDefecto) {
        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para exportar el reporte",
                nombreDefecto,
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) return;

        try {
            // 1) Snapshot de TODA la ventana
            WritableImage writableImage = n.snapshot(new SnapshotParameters(), null);
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
            Notifications.create()
                    .title("Exportar reporte")
                    .text("Reporte guardado y exportado con éxito.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
        } catch (IOException ex) {
            Notifications.create()
                    .title("Exportar reporte")
                    .text("Ocurrió un error al exportar el reporte.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
            ex.printStackTrace();
        }
    }

    //todo: corregir las fechas en los reportes
    public static void modelosMasRegistrados(File file, List<ModelosMasRegistradosDTO> listaModelosDTO) {
        // 1. Crear el conjunto de datos para el gráfico
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        long maxCantidad = 1; // Para calcular el valor máximo del eje Y

        for (ModelosMasRegistradosDTO dto : listaModelosDTO) {
            long cantidad = dto.cantidadRetirada();
            // Crear una etiqueta legible para el eje X (ej: "Toyota Corolla (2020)")
            String etiqueta = dto.nombreMarca() + " " + dto.nombreModelo() + " (" + dto.anioModelo() + ")";

            // Añadir el valor al dataset
            // Parámetros: valor, nombre de la serie, etiqueta de la categoría
            dataset.setValue(cantidad, "Modelos", etiqueta);

            // Actualizar el valor máximo encontrado
            if (cantidad > maxCantidad) {
                maxCantidad = cantidad;
            }
        }

        // 2. Crear el objeto JFreeChart
        JFreeChart chart = ChartFactory.createBarChart(
                "LOS " + listaModelosDTO.size() + " MODELOS DE VEHÍCULOS MÁS REGISTRADOS", // Título del gráfico
                "MODELOS DE VEHÍCULOS",       // Etiqueta del eje X (Categorías)
                "CANTIDAD DE REGISTROS",       // Etiqueta del eje Y (Valores)
                dataset,
                PlotOrientation.VERTICAL,
                false, // No mostrar leyenda
                true,  // Generar tooltips (útil si se muestra en una GUI)
                false  // No generar URLs
        );

        // 3. Personalizar la apariencia del gráfico (Plot y Ejes)
        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // Calcular dinámicamente la escala del eje Y para que se vea bien
        int tickUnit = calcularTickUnitModelos(maxCantidad);
        // El límite superior es el siguiente múltiplo del 'tickUnit' por encima del máximo
        double upperBound = Math.ceil((double) maxCantidad / tickUnit) * tickUnit;
        if (upperBound == maxCantidad) {
            upperBound += tickUnit; // Añadir espacio extra en la parte superior
        }

        rangeAxis.setRange(0, upperBound);
        rangeAxis.setTickUnit(new NumberTickUnit(tickUnit));
        // Asegurar que el eje Y solo muestre números enteros
        rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());

        // 4. Personalizar las barras (Renderer)
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(true); // Mostrar el valor sobre cada barra

        // Formato para las etiquetas de las barras (mostrar el valor numérico)
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));

        // Posición de la etiqueta (fuera de la barra, en la parte superior)
        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER)
        );

        // Opcional: Cambiar el ángulo de las etiquetas del eje X si son muy largas
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // 5. Guardar el gráfico en un archivo
        try {
            // Guardar como JPEG con dimensiones específicas
            ChartUtils.saveChartAsJPEG(file, chart, 1200, 800);
            Notifications.create()
                    .title("Exportar reporte")
                    .text("Reporte guardado y exportado con éxito.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
        } catch (IOException ex) {
            Notifications.create()
                    .title("Exportar reporte")
                    .text("Ocurrió un error al exportar el reporte.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
            ex.printStackTrace();
        }
    }

    public static void repuestosMasRetiradosEnMes(File file, List<RepuestoRetiradoReporteDTO> listaMasRetiradosDTO,
                                                  LocalDate fechaMin, LocalDate fechaMax) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        long max = 1; // Para calcular el valor máximo

        for (RepuestoRetiradoReporteDTO dto : listaMasRetiradosDTO) {
            long cantidad = dto.cantidad();
            String etiqueta = dto.detalle() + " - " + dto.marca();
            dataset.setValue(cantidad, "masRetirados", etiqueta);
            if (cantidad > max) {
                max = cantidad;
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "LOS " + listaMasRetiradosDTO.size() + " REPUESTOS MÁS RETIRADOS ENTRE: " + fechaMin + " y " + fechaMax,
                "REPUESTOS",
                "VECES RETIRADO",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                true
        );

        // Configurar el eje Y
        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        int tickUnit = calcularTickUnit(max);
        double upperBound = Math.ceil((double) max / tickUnit) * tickUnit;

        rangeAxis.setRange(0, upperBound);
        rangeAxis.setTickUnit(new NumberTickUnit(tickUnit));
        rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());

        // Configurar renderizado de las barras
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(true);

        // Formato para etiquetas
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", numberFormat));

        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER)
        );

        try {
            ChartUtils.saveChartAsJPEG(file, chart, 1200, 700);
            Notifications.create()
                    .title("Exportar reporte")
                    .text("Reporte guardado y exportado con éxito.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
        } catch (IOException ex) {
            Notifications.create()
                    .title("Exportar reporte")
                    .text("Ocurrió un error al exportar el reporte.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
            ex.printStackTrace();
        }
    }

    /**
     * Calcula un intervalo de marca (tick) adecuado para el eje Y basado en el valor máximo.
     *
     * @param max El valor más alto en el conjunto de datos.
     * @return Un entero para usar como unidad de marca en el eje.
     */
    private static int calcularTickUnitModelos(long max) {
        if (max <= 10) return 1;
        if (max <= 25) return 2;
        if (max <= 50) return 5;
        if (max <= 100) return 10;
        if (max <= 250) return 25;
        if (max <= 500) return 50;
        return (int) Math.pow(10, Math.floor(Math.log10(max)));
    }

    // auxiliar para calcular el TickUnit óptimo
    private static int calcularTickUnit(long max) {
        if (max <= 10) {
            return 1;
        }
        if (max <= 50) {
            return 5;
        }
        if (max <= 100) {
            return 10;
        }
        if (max <= 500) {
            return 50;
        }
        if (max <= 1000) {
            return 100;
        }
        return 500;
    }

}
