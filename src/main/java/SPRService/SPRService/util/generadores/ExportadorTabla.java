package SPRService.SPRService.util.generadores;

import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.viewModels.tablas.RepuestoRowViewModel;
import SPRService.SPRService.viewModels.tablas.VehiculoRowViewModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

//TODO: el par de métodos que usan Repuesto deberían usar su view model.
public class ExportadorTabla {

    public static void exportarRepuestosXLSX(List<RepuestoRowViewModel> listaRepuestos, File file) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Repuestos");

        // Estilo encabezado
        CellStyle estiloEncabezado = workbook.createCellStyle();
        Font fuenteNegrita = workbook.createFont();
        fuenteNegrita.setBold(true);
        estiloEncabezado.setFont(fuenteNegrita);

        // Crear encabezado
        String[] columnas = {
                "COD BARRA", "DETALLE", "MARCA", "PRECIO", "CANTIDAD STOCK", "STOCK MÍNIMO"
        };

        Row filaEncabezado = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(columnas[i]);
            celda.setCellStyle(estiloEncabezado);
        }

        // Formato para precio
        CellStyle estiloMoneda = workbook.createCellStyle();
        DataFormat formato = workbook.createDataFormat();
        estiloMoneda.setDataFormat(formato.getFormat("$ #,##0.00")); // Formato de moneda

        // Rellenar datos desde el ViewModel
        int rowNum = 1;
        for (RepuestoRowViewModel r : listaRepuestos) {
            Row fila = sheet.createRow(rowNum++);
            fila.createCell(0).setCellValue(r.getCoBarra());
            fila.createCell(1).setCellValue(r.getNombre()); // "nombre" es el nuevo "detalle"
            fila.createCell(2).setCellValue(r.getMarca());

            Cell celdaPrecio = fila.createCell(3);
            // Obtenemos el precio numérico del objeto original para aplicar el formato de celda correctamente
            celdaPrecio.setCellValue(r.getRepuestoOriginal().getPrecio().doubleValue());
            celdaPrecio.setCellStyle(estiloMoneda);

            fila.createCell(4).setCellValue(r.getCantidad());
            fila.createCell(5).setCellValue(r.getCantidadMinima());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar archivo
        try (FileOutputStream salida = new FileOutputStream(file)) {
            workbook.write(salida);
            workbook.close();
            Alertas.exito("Generación tabla", "Se generado con éxito la tabla en:\n" +
                    file);
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.error("Emisión nota de retiro", e.getMessage());
        }
    }


    public static void exportarRepuestosCSV(List<RepuestoRowViewModel> listaRepuestos, File file) {
        try (Writer w = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            //escribe encabezado
            w.write("COD BARRA;DETALLE;MARCA;PRECIO;CANTIDAD STOCK;STOCK MÍNIMO\n");
            //escribe repuestos
            for (RepuestoRowViewModel r : listaRepuestos) {
                // Obtenemos el precio numérico para evitar el símbolo "$" en el CSV
                String precioNumerico = String.valueOf(r.getRepuestoOriginal().getPrecio()).replace('.', ',');

                w.write(escapaeCSV(r.getCoBarra()) + ";"
                        + escapaeCSV(r.getNombre()) + ";" // "nombre" es el nuevo "detalle"
                        + escapaeCSV(r.getMarca()) + ";"
                        + precioNumerico + ";"
                        + r.getCantidad() + ";"
                        + r.getCantidadMinima() + "\n"
                );
            }
            Alertas.exito("Generación tabla", "Se generado con éxito la tabla en:\n" +
                    file);
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.error("Emisión nota de retiro", e.getMessage());
        }
    }

    public static void exportarVehiculosXLSX(List<VehiculoRowViewModel> listaVehiculos, File file) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Vehiculos");

        // Estilo encabezado
        CellStyle estiloEncabezado = workbook.createCellStyle();
        Font fuenteNegrita = workbook.createFont();
        fuenteNegrita.setBold(true);
        estiloEncabezado.setFont(fuenteNegrita);

        // Crear encabezado
        String[] columnas = {
                "PATENTE", "MARCA", "MODELO", "CILINDRADA", "AÑO", "COLOR"
        };

        Row filaEncabezado = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(columnas[i]);
            celda.setCellStyle(estiloEncabezado);
        }

        // Rellenar datos
        int rowNum = 1;
        for (VehiculoRowViewModel v : listaVehiculos) {
            Row fila = sheet.createRow(rowNum++);
            fila.createCell(0).setCellValue(v.getPatente());
            fila.createCell(1).setCellValue(v.getMarca());
            fila.createCell(2).setCellValue(v.getModelo());
            fila.createCell(3).setCellValue(v.getCilindrada());
            fila.createCell(4).setCellValue(v.getAnio());
            fila.createCell(5).setCellValue(v.getColor());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar archivo
        try (FileOutputStream salida = new FileOutputStream(file)) {
            workbook.write(salida);
            workbook.close();
            Alertas.exito("Generación tabla de Vehículos", "Se generado con éxito la tabla en:\n" +
                    file);
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.error("Error al generar tabla de Vehículos", e.getMessage());
        }
    }

    public static void exportarVehiculosCSV(List<VehiculoRowViewModel> listaVehiculos, File file) {
        try (Writer w = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            // Escribe encabezado
            w.write("PATENTE;MARCA;MODELO;CILINDRADA;AÑO;COLOR\n");

            // Escribe datos de vehículos
            for (VehiculoRowViewModel v : listaVehiculos) {
                w.write(escapaeCSV(v.getPatente()) + ";"
                        + escapaeCSV(v.getMarca()) + ";"
                        + escapaeCSV(v.getModelo()) + ";"
                        + String.valueOf(v.getCilindrada()).replace('.',',') + ";" // Reemplazar punto por coma
                        + v.getAnio() + ";"
                        + escapaeCSV(v.getColor()) + "\n"
                );
            }
            Alertas.exito("Generación tabla de Vehículos", "Se generado con éxito la tabla en:\n" +
                    file);
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.error("Error al generar tabla de Vehículos", e.getMessage());
        }
    }

    //previene errores en valores que tengan comas, comillas, etc.
    private static String escapaeCSV(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            valor.replace("\"", "\"\"");
            return "\"" + valor + "\"";

        }
        return valor;
    }

}
