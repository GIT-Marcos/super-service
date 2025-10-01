package SPRService.SPRService.util.generadores;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.util.alertas.Alertas;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GeneradorReportes {

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
            System.out.println("Reporte generado con éxito en: " + file.getAbsolutePath());
            Alertas.exito("Reporte", "Se ha generado con éxito el reporte en:\n" + file);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al generar el reporte: " + ex.getMessage());
            Alertas.error("Error en reporte", "No se pudo generar el gráfico: " + ex.getMessage());
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
            Alertas.exito("Reporte", "Se generado con éxito el reporte en:\n" +
                    file);
        } catch (IOException ex) {
            ex.printStackTrace();
            Alertas.error("Emisión nota de retiro", ex.getMessage());
        }
    }

    public static void totalVentasAnual(File file, Map<String, BigDecimal> valores) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, BigDecimal> entry : valores.entrySet()) {
            String mes = entry.getKey();
            BigDecimal totalVentas = entry.getValue();
            dataset.setValue(totalVentas, "totalVentas", mes);
        }

        //TODO: HACER Q TOME EL AÑO CORRESPONDIENTE AL REPORTE
        JFreeChart chart = ChartFactory.createBarChart(
                "TOTAL DE VENTAS AÑO: 2025",
                null, // eje X
                "$", // eje Y
                dataset,
                PlotOrientation.VERTICAL,
                false, // leyenda
                false,
                true
        );

        // Configurar renderizado para mostrar montos en las barras
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Mostrar etiquetas con los valores
        renderer.setDefaultItemLabelsVisible(true);

        // Formato de número (ej. 12,000)
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", numberFormat));

        // Posición de las etiquetas (encima de la barra)
        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER)
        );

        try {
            ChartUtils.saveChartAsJPEG(file, chart, 1200, 700);
            Alertas.exito("Reporte", "Se generado con éxito el reporte en:\n" +
                    file);
        } catch (IOException ex) {
            ex.printStackTrace();
            Alertas.error("Emisión nota de retiro", ex.getMessage());
        }
    }

    public static void cantidadVentasAnual(File file, Map<String, Long> valores) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Long> entry : valores.entrySet()) {
            String mes = entry.getKey();
            Long cantidadDeVentas = entry.getValue();
            dataset.setValue(cantidadDeVentas, "CantidadVentas", mes);
        }

        //TODO: HACER Q TOME EL AÑO CORRESPONDIENTE AL REPORTE
        JFreeChart chart = ChartFactory.createBarChart(
                "CANTIDAD DE VENTAS AÑO: 2025",
                null, // eje X
                "N° de Ventas", // eje Y
                dataset,
                PlotOrientation.VERTICAL,
                false, // leyenda
                false,
                true
        );

        CategoryPlot plot = chart.getCategoryPlot();

        // Configurar eje Y con rango dinámico
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        long max = valores.values().stream().mapToLong(Long::longValue).max().orElse(1);
        int tickUnit = calcularTickUnit(max);
        double upperBound = Math.ceil((double) max / tickUnit) * tickUnit;

        rangeAxis.setRange(0, upperBound);
        rangeAxis.setTickUnit(new NumberTickUnit(tickUnit));
        rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());

        // Mostrar etiquetas encima de las barras
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getIntegerInstance()));
        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER)
        );

        try {
            ChartUtils.saveChartAsJPEG(file, chart, 1200, 700);
            Alertas.exito("Reporte", "Se generado con éxito el reporte en:\n" +
                    file);
        } catch (IOException ex) {
            ex.printStackTrace();
            Alertas.error("Emisión nota de retiro", ex.getMessage());
        }
    }

    /**
     * Calcula un intervalo de marca (tick) adecuado para el eje Y basado en el valor máximo.
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
