package SPRService.SPRService.util.generadores;

import SPRService.SPRService.DTOs.FacturaServiceDTO;
import SPRService.SPRService.entities.Trabajo;
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

public class GeneradorFacturasPDF {

    //TODO: QUE TOME LOS DATOS FISCALES Y TODO ESO DE UN .PROPERTIES

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
                    GeneradorFacturasPDF.class.getResource("/imgs/icon.png")));
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
                table.addCell(new PdfPCell(new Phrase(d.getRepuesto().getMarcaRepuesto().getNombreMarca(), smallFont)));
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

    public static void generaPDFService(FacturaServiceDTO dto, File file) {
        if (file == null || dto == null) {
            throw new IllegalArgumentException("Argumento nulo al generar PDF de Service.");
        }
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // --- 1. TÍTULO ---
            Paragraph titulo = new Paragraph("Factura de Servicio / Orden de Reparación", FontFactory.getFont(FontFactory.TIMES_BOLD, 16));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" "));

            // --- 2. LETRA C ---
            Phrase letraC = new Phrase("C", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32));
            PdfPCell celdaTipo = new PdfPCell(letraC);
            celdaTipo.setFixedHeight(40f);
            celdaTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTipo.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaTipo.setBorderWidth(1f);
            PdfPTable tablaTipo = new PdfPTable(1);
            tablaTipo.setWidthPercentage(10);
            tablaTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaTipo.addCell(celdaTipo);
            document.add(tablaTipo);

            document.add(new Paragraph(" "));
            Paragraph espaciado = new Paragraph("***************************************************************************");
            espaciado.setAlignment(Element.ALIGN_CENTER);
            document.add(espaciado);
            document.add(new Paragraph(" "));

            // --- 3. CABECERA ---
            PdfPTable cabecera = new PdfPTable(2);
            cabecera.setWidthPercentage(100);
            cabecera.setWidths(new float[]{3, 1});

            DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            PdfPCell datos = new PdfPCell();
            datos.setBorder(Rectangle.NO_BORDER);
            datos.addElement(new Paragraph("A CONSUMIDOR FINAL"));
            datos.addElement(new Paragraph("Punto de venta: 00001 - Avenida Super Service 999"));
            datos.addElement(new Paragraph("Nro. Service: " + dto.idService()));
            datos.addElement(new Paragraph("Fecha Ingreso: " + dto.fechaCarga().format(fmtFecha)));
            datos.addElement(new Paragraph("Fecha Entrega: " + (dto.fechaEntrega() != null ? dto.fechaEntrega().format(fmtFecha) : "Pendiente")));
            datos.addElement(new Paragraph(" "));

            Image img;
            try {
                if (dto.rutaImgLogoMarca() != null && !dto.rutaImgLogoMarca().isEmpty()) {
                    img = Image.getInstance(dto.rutaImgLogoMarca());
                } else {
                    img = Image.getInstance(Objects.requireNonNull(GeneradorFacturasPDF.class.
                            getResource("/imgs/icon.png")));
                }
            } catch (Exception e) {
                img = Image.getInstance(Objects.requireNonNull(GeneradorFacturasPDF.
                        class.getResource("/imgs/icon.png")));
            }
            img.scaleToFit(80, 80);
            PdfPCell celdaImagen = new PdfPCell(img);
            celdaImagen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaImagen.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaImagen.setBorder(Rectangle.NO_BORDER);

            cabecera.addCell(datos);
            cabecera.addCell(celdaImagen);
            document.add(cabecera);

            // --- 4. CLIENTE Y VEHÍCULO ---
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            PdfPTable tablaDatos = new PdfPTable(4);
            tablaDatos.setWidthPercentage(100);
            tablaDatos.setSpacingBefore(10f);
            tablaDatos.setWidths(new float[]{1f, 2f, 1f, 2f});

            PdfPCell cellTitulo = new PdfPCell(new Phrase("Datos del Cliente", labelFont));
            cellTitulo.setColspan(4);
            cellTitulo.setBackgroundColor(Color.LIGHT_GRAY);
            tablaDatos.addCell(cellTitulo);

            tablaDatos.addCell(new Phrase("Cliente:", labelFont));
            PdfPCell cellCliente = new PdfPCell(new Phrase(dto.dniNombreApellidoCliente(), normalFont));
            cellCliente.setColspan(3);
            tablaDatos.addCell(cellCliente);

            cellTitulo = new PdfPCell(new Phrase("Datos del Vehículo", labelFont));
            cellTitulo.setColspan(4);
            cellTitulo.setBackgroundColor(Color.LIGHT_GRAY);
            tablaDatos.addCell(cellTitulo);

            tablaDatos.addCell(new Phrase("Vehículo:", labelFont));
            tablaDatos.addCell(new Phrase(dto.marca() + " " + dto.modeloAnioCilindrada(), normalFont));
            tablaDatos.addCell(new Phrase("Patente:", labelFont));
            tablaDatos.addCell(new Phrase(dto.patente(), normalFont));
            tablaDatos.addCell(new Phrase("Chasis:", labelFont));
            tablaDatos.addCell(new Phrase(dto.nroChasis(), normalFont));
            tablaDatos.addCell(new Phrase("Motor:", labelFont));
            tablaDatos.addCell(new Phrase(dto.nroMotor(), normalFont));
            tablaDatos.addCell(new Phrase("Color:", labelFont));
            tablaDatos.addCell(new Phrase(dto.color(), normalFont));
            tablaDatos.addCell(new Phrase("", normalFont));
            tablaDatos.addCell(new Phrase("", normalFont));

            document.add(tablaDatos);

            // --- 5. INFORME ---
            document.add(new Paragraph(" "));
            PdfPTable tablaInforme = new PdfPTable(1);
            tablaInforme.setWidthPercentage(100);

            PdfPCell cellMotivo = new PdfPCell();
            cellMotivo.addElement(new Paragraph("Motivo de Ingreso:", labelFont));
            cellMotivo.addElement(new Paragraph(dto.motivoIngreso(), normalFont));
            cellMotivo.setPaddingBottom(5f);
            tablaInforme.addCell(cellMotivo);

            PdfPCell cellInforme = new PdfPCell();
            cellInforme.addElement(new Paragraph("Informe Técnico / Solución:", labelFont));
            cellInforme.addElement(new Paragraph(dto.informeTecnico(), normalFont));
            cellInforme.setPaddingBottom(5f);
            tablaInforme.addCell(cellInforme);
            document.add(tablaInforme);

            Font whiteFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD, Color.WHITE);
            Font smallFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
            Color azul = new Color(0, 102, 204);

            // --- 6. REPUESTOS ---
            if (dto.detalles() != null && !dto.detalles().isEmpty()) {
                document.add(new Paragraph("Repuestos Utilizados", labelFont));
                PdfPTable tableRepuestos = new PdfPTable(5);
                tableRepuestos.setWidthPercentage(100);
                tableRepuestos.setSpacingBefore(5f);
                tableRepuestos.setWidths(new float[]{2f, 3f, 1f, 1f, 1f});

                addHeader(tableRepuestos, "Marca/Cód", whiteFont, azul);
                addHeader(tableRepuestos, "Detalle", whiteFont, azul);
                addHeader(tableRepuestos, "Precio U.", whiteFont, azul);
                addHeader(tableRepuestos, "Cant.", whiteFont, azul);
                addHeader(tableRepuestos, "Subtotal", whiteFont, azul);

                for (DetalleRetiro d : dto.detalles()) {
                    tableRepuestos.addCell(new PdfPCell(new Phrase(d.getRepuesto().getMarcaRepuesto().getNombreMarca() + " - " + d.getRepuesto().getCodBarra(), smallFont)));
                    tableRepuestos.addCell(new PdfPCell(new Phrase(d.getRepuesto().getDetalle(), smallFont)));
                    tableRepuestos.addCell(new PdfPCell(new Phrase("$ " + d.getRepuesto().getPrecio(), smallFont)));
                    tableRepuestos.addCell(new PdfPCell(new Phrase(String.valueOf(d.getCantidadRetirada()), smallFont)));
                    tableRepuestos.addCell(new PdfPCell(new Phrase("$ " + d.getSubTotal(), smallFont)));
                }
                document.add(tableRepuestos);
            }

            // --- 7. MANO DE OBRA ---
            if (dto.trabajos() != null && !dto.trabajos().isEmpty()) {
                document.add(new Paragraph("Mano de Obra / Trabajos", labelFont));
                PdfPTable tableTrabajos = new PdfPTable(2);
                tableTrabajos.setWidthPercentage(100);
                tableTrabajos.setSpacingBefore(5f);
                tableTrabajos.setWidths(new float[]{4f, 1f});

                addHeader(tableTrabajos, "Descripción", whiteFont, azul);
                addHeader(tableTrabajos, "Costo", whiteFont, azul);

                for (Trabajo t : dto.trabajos()) {
                    tableTrabajos.addCell(new PdfPCell(new Phrase(t.getDescripcion(), smallFont)));
                    tableTrabajos.addCell(new PdfPCell(new Phrase("$ " + t.getPrecio(), smallFont)));
                }
                document.add(tableTrabajos);
            }

            // --- 8. TOTALES FINALES (ACTUALIZADO CON PAGO) ---
            document.add(new Paragraph(" "));
            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.setWidthPercentage(40);

            // 8.1 TOTAL GENERAL
            PdfPCell cellTotalLabel = new PdfPCell(new Phrase("TOTAL GENERAL:", labelFont));
            cellTotalLabel.setBorder(Rectangle.NO_BORDER);
            tablaTotales.addCell(cellTotalLabel);

            PdfPCell cellTotalValue = new PdfPCell(new Phrase("$ " + dto.montoTotal(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            cellTotalValue.setBorder(Rectangle.BOTTOM);
            tablaTotales.addCell(cellTotalValue);

            // 8.2 MONTO PAGADO (NUEVO)
            // Se muestra si hay algún pago registrado (mayor a cero)
            if (dto.montoPagado() != null && dto.montoPagado().compareTo(BigDecimal.ZERO) > 0) {
                PdfPCell cellPagoLabel = new PdfPCell(new Phrase("Pagado / A cuenta (-):", labelFont));
                cellPagoLabel.setBorder(Rectangle.NO_BORDER);
                cellPagoLabel.setPaddingTop(5f);
                tablaTotales.addCell(cellPagoLabel);

                Font greenFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(34, 139, 34)); // Verde Forest
                PdfPCell cellPagoValue = new PdfPCell(new Phrase("$ " + dto.montoPagado(), greenFont));
                cellPagoValue.setBorder(Rectangle.NO_BORDER);
                cellPagoValue.setPaddingTop(5f);
                tablaTotales.addCell(cellPagoValue);
            }

            // 8.3 MONTO FALTANTE (SALDO)
            // Se muestra si queda deuda
            if (dto.montoFaltante().compareTo(BigDecimal.ZERO) > 0) {
                PdfPCell cellRestanteLabel = new PdfPCell(new Phrase("Saldo Restante:", labelFont));
                cellRestanteLabel.setBorder(Rectangle.TOP); // Línea divisoria
                cellRestanteLabel.setPaddingTop(5f);
                tablaTotales.addCell(cellRestanteLabel);

                Font redFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.RED);
                PdfPCell cellRestanteValue = new PdfPCell(new Phrase("$ " + dto.montoFaltante(), redFont));
                cellRestanteValue.setBorder(Rectangle.TOP);
                cellRestanteValue.setPaddingTop(5f);
                tablaTotales.addCell(cellRestanteValue);
            } else if (dto.montoPagado().compareTo(BigDecimal.ZERO) > 0) {
                // Si no falta nada pero pagó algo, mostramos "Pagado"
                PdfPCell cellOkLabel = new PdfPCell(new Phrase("Estado:", labelFont));
                cellOkLabel.setBorder(Rectangle.TOP);
                tablaTotales.addCell(cellOkLabel);

                PdfPCell cellOkValue = new PdfPCell(new Phrase("PAGADO", new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLUE)));
                cellOkValue.setBorder(Rectangle.TOP);
                tablaTotales.addCell(cellOkValue);
            }

            document.add(tablaTotales);

            // --- 9. FOOTER ---
            document.add(new Paragraph(" "));
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20f);
            Font footerFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY);
            PdfPCell footerCell = new PdfPCell(new Phrase("Garantía de reparación: 90 días.", footerFont));
            footerCell.setBorder(Rectangle.TOP);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPaddingTop(5f);
            footerTable.addCell(footerCell);
            document.add(footerTable);

            document.close();
            Alertas.exito("Factura Service", "Factura generada con éxito en :\n" + file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Auxiliar para headers de tablas
    private static void addHeader(PdfPTable table, String text, Font font, Color color) {
        PdfPCell header = new PdfPCell(new Phrase(text, font));
        header.setBackgroundColor(color);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
