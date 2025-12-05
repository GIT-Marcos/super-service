package SPRService.SPRService.util.generadores;

import SPRService.SPRService.DTOs.TicketRetiroServiceDTO;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.util.alertas.NotificationHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class GeneradorTXT {

    public static void generaNotaRetiro(List<DetalleRetiro> listaDetallesRetiro, File ruta) {
        StringBuilder sb = new StringBuilder();
        sb.append("*** NOTA DE RETIRO (AGRUPADA POR UBICACIÓN) ***\r\n");
        sb.append("-------------------------------------------------\r\n\r\n");

        // 1. Agrupamos la lista por el nombre de la Ubicación.
        // Usamos TreeMap para que las ubicaciones salgan ordenadas alfabéticamente.
        Map<String, List<DetalleRetiro>> mapPorUbicacion = listaDetallesRetiro.stream()
                .collect(Collectors.groupingBy(
                        dr -> dr.getRepuesto().getStock().getUbicacion().getUbicacion(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        // 2. Iteramos sobre cada ubicación (Depósito)
        for (Map.Entry<String, List<DetalleRetiro>> entry : mapPorUbicacion.entrySet()) {
            String ubicacion = entry.getKey();
            List<DetalleRetiro> repuestosEnEstaUbicacion = entry.getValue();

            // Encabezado del Depósito/Ubicación
            sb.append(">>> UBICACIÓN: ").append(ubicacion).append("\r\n");
            sb.append("========================================\r\n");

            // 3. Listamos los repuestos que están en esa ubicación
            for (DetalleRetiro dr : repuestosEnEstaUbicacion) {
                sb.append(String.format(
                        "   CÓD. BARRAS: %s\r\n" +
                                "   DETALLE:     %s\r\n" +
                                "   MARCA:       %s\r\n" +
                                "   LOTE:        %s\r\n" +
                                "   CANTIDAD:    %s\r\n" +
                                "----------------------------------------\r\n",
                        dr.getRepuesto().getCodBarra(),
                        dr.getRepuesto().getDetalle(),
                        dr.getRepuesto().getMarcaRepuesto().getNombreMarca(),
                        dr.getRepuesto().getStock().getLote(),
                        dr.getCantidadRetirada()
                ));
            }
            sb.append("\r\n"); // Espacio entre ubicaciones para cortar el papel o separar visualmente
        }

        sb.append("*** FIN DE LISTADO PARA RETIRAR ***\r\n");

        guardarArchivo(sb.toString(), ruta, "Nota de retiro");
    }

    public static void generarTicketRetiroService(TicketRetiroServiceDTO dto, File ruta) {
        if (dto == null || ruta == null) {
            NotificationHelper.mostrarError("Error", "Datos insuficientes para generar el ticket.");
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();

        sb.append("=================================\r\n");
        sb.append("         TICKET DE SERVICE  \r\n");
        sb.append("=================================\r\n");
        sb.append("\r\n");

        sb.append("NRO SERVICE: ").append(dto.idService()).append("\r\n");
        sb.append("--------------------------------\r\n");

        sb.append("CLIENTE:\r\n");
        sb.append(dto.dniNombreApellidoCliente()).append("\r\n");
        sb.append("--------------------------------\r\n");

        sb.append("VEHÍCULO:\r\n");
        sb.append(dto.marca()).append(" ").append(dto.modeloAnioCilindrada()).append("\r\n");
        sb.append("PATENTE: ").append(dto.patente()).append("\r\n");
        sb.append("--------------------------------\r\n");

        sb.append("FECHAS:\r\n");
        sb.append("Ingreso: ").append(dto.fechaCarga().format(fmt)).append("\r\n");
        String entrega = (dto.fechaEntrega() != null) ? dto.fechaEntrega().format(fmt) : "Pendiente";
        sb.append("Entrega: ").append(entrega).append("\r\n");
        sb.append("--------------------------------\r\n");

        sb.append("TOTAL OPERACIÓN: $").append(dto.montoTotal()).append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("FIRMA CONFORMIDAD CLIENTE:\r\n");
        sb.append("\r\n");
        sb.append("................................\r\n");
        sb.append("\r\n");
        sb.append("================================\r\n");
        sb.append("   GRACIAS POR SU VISITA        \r\n");

        guardarArchivo(sb.toString(), ruta, "Ticket de retiro de Service");
    }

    private static void guardarArchivo(String contenido, File ruta, String tipoDoc) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ruta), StandardCharsets.ISO_8859_1))) {
            bw.write(contenido);
            NotificationHelper.mostrarExito("Emisión " + tipoDoc, "Se ha generado con éxito en:\n" + ruta);
        } catch (Exception e) {
            NotificationHelper.mostrarError("Error al generar " + tipoDoc, e.getMessage());
            e.printStackTrace();
        }
    }
}