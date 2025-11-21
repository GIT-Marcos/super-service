package SPRService.SPRService.util.generadores;

import SPRService.SPRService.DTOs.TicketRetiroServiceDTO;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.util.alertas.Alertas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GeneradorTXT {

    public static void generaNotaRetiro(List<DetalleRetiro> listaDetallesRetiro, File ruta) {
        StringBuilder sb = new StringBuilder();
        sb.append("*** NOTA DE RETIRO ***\r\n");
        sb.append("------------------------\r\n");

        for (DetalleRetiro dr : listaDetallesRetiro) {
            sb.append(String.format(
                    "CÓDIGO: %s\r\nDETALLE: %s\r\nMARCA: %s\r\nCANTIDAD: %s\r\nUBICACIÓN: %s\r\nLOTE: %s\r\n------------------------\r\n",
                    dr.getRepuesto().getCodBarra(),
                    dr.getRepuesto().getDetalle(),
                    dr.getRepuesto().getMarcaRepuesto().getNombreMarca(),
                    dr.getCantidadRetirada(),
                    dr.getRepuesto().getStock().getUbicacion(),
                    dr.getRepuesto().getStock().getLote()
            ));
        }

        sb.append("*** PARA RETIRAR DE DEPÓSITO ***\r\n");

        guardarArchivo(sb.toString(), ruta, "Nota de retiro");
    }

    public static void generarTicketRetiroService(TicketRetiroServiceDTO dto, File ruta) {
        if (dto == null || ruta == null) {
            Alertas.error("Error", "Datos insuficientes para generar el ticket.");
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
            Alertas.exito("Emisión " + tipoDoc, "Se ha generado con éxito en:\n" + ruta);
        } catch (Exception e) {
            e.printStackTrace();
            Alertas.error("Error al generar " + tipoDoc, e.getMessage());
        }
    }
}