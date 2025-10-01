package SPRService.SPRService.util.generadores;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.util.alertas.Alertas;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GeneradorPDF {

    public static void generaPDFVenta(VentaRepuesto venta, File file) {
        if (file == null || venta == null) {
            throw new IllegalArgumentException("argumento nulo el generar pdf venta.");
        }
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Paragraph titulo = new Paragraph("Factura de Venta", FontFactory.getFont(FontFactory.TIMES_BOLD, 16));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" "));
            Phrase letraC = new Phrase("C", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32));
// Crear celda con bordes y la letra centrada
            PdfPCell celdaTipo = new PdfPCell(letraC);
            celdaTipo.setFixedHeight(40f);
            celdaTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTipo.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaTipo.setBorderWidth(1f);
// Crear una tabla de 1 columna para ubicar la celda
            PdfPTable tablaTipo = new PdfPTable(1);
            tablaTipo.setWidthPercentage(10); // ancho relativo del cuadrado
            tablaTipo.setHorizontalAlignment(Element.ALIGN_CENTER); // centrar tabla
            tablaTipo.addCell(celdaTipo);
// Agregar al documento
            document.add(tablaTipo);
            document.add(new Paragraph(" "));
            Paragraph espaciado = new Paragraph("***************************************************************************");
            espaciado.setAlignment(Element.ALIGN_CENTER);
            document.add(espaciado);

            document.add(new Paragraph(" "));

            // Crear tabla con 2 columnas: datos fiscales + logo
            PdfPTable cabecera = new PdfPTable(2);
            cabecera.setWidthPercentage(100);
            cabecera.setWidths(new float[]{3, 1}); // relación ancho columnas (ajustable)

// Celda izquierda: datos fiscales
            PdfPCell datos = new PdfPCell();
            datos.setBorder(Rectangle.NO_BORDER);
            datos.addElement(new Paragraph("A CONSUMIDOR FINAL"));
            datos.addElement(new Paragraph("IVA RESPONSABLE INSCRITO"));
            datos.addElement(new Paragraph("Punto de venta: 00001 - Avenida Super Service 999"));
            datos.addElement(new Paragraph("Estado Venta: " + venta.getEstadoVenta()));
            datos.addElement(new Paragraph("Fecha de Venta: " + venta.getFechaVenta()));
            datos.addElement(new Paragraph("Factura nro: 00002 - 0000" + venta.getId()));
            DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
            datos.addElement(new Paragraph("Factura emitida el: " + LocalDate.now() + " "
                    + LocalTime.now().format(formatoHora)));
            datos.addElement(new Paragraph("C.U.I.T: 30-11111111-2"));
            datos.addElement(new Paragraph("Ingresos brutos: 00000000000000"));
            datos.addElement(new Paragraph("Inicio de actividades: 06/2016"));
            datos.addElement(new Paragraph(" "));

// Celda derecha: imagen
            Image img = Image.getInstance(Objects.requireNonNull(
                    GeneradorPDF.class.getResource("/imgs/icon.png")));
            img.scaleToFit(80, 80); // ajusta el tamaño según necesidad

            PdfPCell celdaImagen = new PdfPCell(img);
            celdaImagen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaImagen.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaImagen.setBorder(Rectangle.NO_BORDER);

// Añadir celdas a la tabla
            cabecera.addCell(datos);
            cabecera.addCell(celdaImagen);

// Añadir la cabecera al documento
            document.add(cabecera);
//datos cliente
            // Font común
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

// Tabla principal de 2 columnas
            PdfPTable tablaComprador = new PdfPTable(2);
            tablaComprador.setWidthPercentage(100);
            tablaComprador.setSpacingBefore(10f);
            tablaComprador.setWidths(new float[]{1f, 2f});

// --- Fila 1: Razón Social ---
            tablaComprador.addCell(new Phrase("Razón Social:", labelFont));
            tablaComprador.addCell(new Phrase(" ", normalFont));

// --- Fila 2: Domicilio ---
            tablaComprador.addCell(new Phrase("Domicilio:", labelFont));
            tablaComprador.addCell(new Phrase(" ", normalFont));

// --- Fila 3: CUIT/DNI ---
            tablaComprador.addCell(new Phrase("CUIT / DNI:", labelFont));
            tablaComprador.addCell(new Phrase(" ", normalFont));

// --- Fila 4: Condición frente al IVA con checkboxes ---
            tablaComprador.addCell(new Phrase("Condición frente al IVA:", labelFont));

// Subtabla con opciones
            PdfPTable subtablaIVA = new PdfPTable(2);
            subtablaIVA.setWidthPercentage(100);

            String[] opciones = {
                    "Responsable Inscripto",
                    "Monotributista",
                    "Exento",
                    "Consumidor Final"
            };

// Supón que este cliente es "Consumidor Final"
            String seleccionado = "Consumidor Final";

// Agregar filas con checkbox simulados
            for (String opcion : opciones) {
                String check = opcion.equals(seleccionado) ? "[ ] " : "[ ] ";
                subtablaIVA.addCell(new Phrase(check + opcion, normalFont));
            }

            PdfPCell celdaIVA = new PdfPCell(subtablaIVA);
            celdaIVA.setBorder(Rectangle.NO_BORDER);
            tablaComprador.addCell(celdaIVA);
            document.add(new Paragraph("Datos del Cliente", labelFont));
            document.add(tablaComprador);

            // Tabla con detalles
            PdfPTable table = new PdfPTable(6); // columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            //títulos columnas
            Font whiteFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD, Color.WHITE);
            Color azul = new Color(0, 102, 204); // Azul más profesional

            PdfPCell headerCell;
            headerCell = new PdfPCell(new Phrase("Cód barras", whiteFont));
            headerCell.setBackgroundColor(azul);
            table.addCell(headerCell);
            headerCell = new PdfPCell(new Phrase("Marca", whiteFont));
            headerCell.setBackgroundColor(azul);
            table.addCell(headerCell);
            headerCell = new PdfPCell(new Phrase("Detalle", whiteFont));
            headerCell.setBackgroundColor(azul);
            table.addCell(headerCell);
            headerCell = new PdfPCell(new Phrase("Precio uni.", whiteFont));
            headerCell.setBackgroundColor(azul);
            table.addCell(headerCell);
            headerCell = new PdfPCell(new Phrase("Cantidad", whiteFont));
            headerCell.setBackgroundColor(azul);
            table.addCell(headerCell);
            headerCell = new PdfPCell(new Phrase("Subtotal", whiteFont));
            headerCell.setBackgroundColor(azul);
            table.addCell(headerCell);

            BigDecimal total = BigDecimal.ZERO;

            Font smallFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

            for (DetalleRetiro d : venta.getNotaRetiro().getDetallesRetiroList()) {
                BigDecimal subtotal = d.getRepuesto().getPrecio().multiply(new BigDecimal(d.getCantidadRetirada()));
                total = total.add(subtotal);

                table.addCell(new PdfPCell(new Phrase(d.getRepuesto().getCodBarra(), smallFont)));
                table.addCell(new PdfPCell(new Phrase(d.getRepuesto().getMarca(), smallFont)));
                table.addCell(new PdfPCell(new Phrase(d.getRepuesto().getDetalle(), smallFont)));
                table.addCell(new PdfPCell(new Phrase("$ " + d.getRepuesto().getPrecio(), smallFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(d.getCantidadRetirada()), smallFont)));
                table.addCell(new PdfPCell(new Phrase("$ " + subtotal, smallFont)));
            }
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total: $" + total, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph(" "));

            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);

            Font footerFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY);
            Phrase footerPhrase = new Phrase("Nro CAI: 12345678901234    |    Fecha vencimiento CAI: 31/12/2025", footerFont);

            PdfPCell footerCell = new PdfPCell(footerPhrase);
            footerCell.setBorder(Rectangle.TOP);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPaddingTop(5f);
            footerCell.setPaddingBottom(5f);

            footerTable.addCell(footerCell);

// Agregar al documento
            document.add(footerTable);

            document.close();
            Alertas.exito("Factura", "Factura creada con éxito en :\n" +
                    file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
