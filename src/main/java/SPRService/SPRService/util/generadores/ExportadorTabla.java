package SPRService.SPRService.util.generadores;

import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.tablas.RepuestoRowViewModel;
import SPRService.SPRService.viewModels.tablas.VehiculoRowViewModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
                "COD BARRA", "DETALLE", "MARCA", "PRECIO",
                "CANTIDAD STOCK", "STOCK MÍNIMO", "UNIDAD MEDIDA", "ESTADO"
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
        estiloMoneda.setDataFormat(formato.getFormat("$ #,##0.00"));

        // Rellenar datos desde el ViewModel
        int rowNum = 1;
        for (RepuestoRowViewModel r : listaRepuestos) {
            Row fila = sheet.createRow(rowNum++);
            fila.createCell(0).setCellValue(r.getCoBarra());
            fila.createCell(1).setCellValue(r.getNombre());
            fila.createCell(2).setCellValue(r.getMarca());

            Cell celdaPrecio = fila.createCell(3);
            celdaPrecio.setCellValue(r.getPrecio().doubleValue());
            celdaPrecio.setCellStyle(estiloMoneda);

            fila.createCell(4).setCellValue(r.getCantidad());
            fila.createCell(5).setCellValue(r.getCantidadMinima());
            fila.createCell(6).setCellValue(r.getUniMedida());
            fila.createCell(7).setCellValue(r.getEstado());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar archivo
        try (FileOutputStream salida = new FileOutputStream(file)) {
            workbook.write(salida);
            workbook.close();
            mostrarMensajeExito(file);
        } catch (IOException e) {
            mostrarMensajeError();
            e.printStackTrace();
        }
    }


    public static void exportarRepuestosCSV(List<RepuestoRowViewModel> listaRepuestos, File file) {
        try (Writer w = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            // Escribe encabezado
            w.write("COD BARRA;DETALLE;MARCA;PRECIO;CANTIDAD STOCK;STOCK MÍNIMO;UNIDAD MEDIDA;ESTADO\n");
            // Escribe repuestos
            for (RepuestoRowViewModel r : listaRepuestos) {
                String precioNumerico = String.valueOf(r.getPrecio()).replace('.', ',');

                w.write(escapaeCSV(r.getCoBarra()) + ";"
                        + escapaeCSV(r.getNombre()) + ";"
                        + escapaeCSV(r.getMarca()) + ";"
                        + precioNumerico + ";"
                        + r.getCantidad() + ";"
                        + r.getCantidadMinima() + ";"
                        + escapaeCSV(r.getUniMedida()) + ";"
                        + escapaeCSV(r.getEstado()) + "\n"
                );
            }
            mostrarMensajeExito(file);
        } catch (IOException e) {
            mostrarMensajeError();
            e.printStackTrace();
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
                "PATENTE", "FECHA REGISTRO", "MARCA", "MODELO",
                "CILINDRADA", "AÑO", "COLOR", "ESTADO"
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
            fila.createCell(1).setCellValue(v.getFechaRegistro());
            fila.createCell(2).setCellValue(v.getMarca());
            fila.createCell(3).setCellValue(v.getModelo());
            fila.createCell(4).setCellValue(v.getCilindrada());
            fila.createCell(5).setCellValue(v.getAnio());
            fila.createCell(6).setCellValue(v.getColor());
            fila.createCell(7).setCellValue(v.getEstado());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar archivo
        try (FileOutputStream salida = new FileOutputStream(file)) {
            workbook.write(salida);
            workbook.close();
            mostrarMensajeExito(file);
        } catch (IOException e) {
            mostrarMensajeError();
            e.printStackTrace();
        }
    }

    public static void exportarVehiculosCSV(List<VehiculoRowViewModel> listaVehiculos, File file) {
        try (Writer w = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            // Escribe encabezado
            w.write("PATENTE;FECHA REGISTRO;MARCA;MODELO;CILINDRADA;AÑO;COLOR;ESTADO\n");

            // Escribe datos de vehículos
            for (VehiculoRowViewModel v : listaVehiculos) {
                w.write(escapaeCSV(v.getPatente()) + ";"
                        + escapaeCSV(v.getFechaRegistro()) + ";"
                        + escapaeCSV(v.getMarca()) + ";"
                        + escapaeCSV(v.getModelo()) + ";"
                        + String.valueOf(v.getCilindrada()).replace('.', ',') + ";"
                        + v.getAnio() + ";"
                        + escapaeCSV(v.getColor()) + ";"
                        + escapaeCSV(v.getEstado()) + "\n"
                );
            }
            mostrarMensajeExito(file);
        } catch (IOException e) {
            mostrarMensajeError();
            e.printStackTrace();
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

    private static void mostrarMensajeExito(File file) {
        NotificationHelper.mostrarExito("Generación de tabla",
                "Se ha generado con éxito la tabla en: \n" + file);
    }

    private static void mostrarMensajeError() {
        NotificationHelper.mostrarError("Generación de tabla", "Ha ocurrido un error inesperado.");
    }
}
