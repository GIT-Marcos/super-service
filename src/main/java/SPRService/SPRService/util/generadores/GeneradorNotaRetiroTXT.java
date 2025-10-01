package SPRService.SPRService.util.generadores;

import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.util.alertas.Alertas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GeneradorNotaRetiroTXT {

    public static void generaNotaRetiro(List<DetalleRetiro> listaDetallesRetiro, File ruta) {

        StringBuilder sb = new StringBuilder();
        sb.append("*** NOTA DE RETIRO ***\r\n");
        sb.append("------------------------\r\n");

        for (DetalleRetiro dr : listaDetallesRetiro) {
            sb.append(String.format(
                    "CÓDIGO: %s\r\nDETALLE: %s\r\nMARCA: %s\r\nCANTIDAD: %s\r\nUBICACIÓN: %s\r\nLOTE: %s\r\n------------------------\r\n",
                    dr.getRepuesto().getCodBarra(),
                    dr.getRepuesto().getDetalle(),
                    dr.getRepuesto().getMarca(),
                    dr.getCantidadRetirada(),
                    dr.getRepuesto().getStock().getUbicacion(),
                    dr.getRepuesto().getStock().getLote()
            ));
        }

        sb.append("*** PARA RETIRAR DE DEPÓSITO ***\r\n");

        // Guarda archivo en codificación ISO-8859-1 para evitar problemas con UTF-8
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ruta), StandardCharsets.ISO_8859_1))) {
            bw.write(sb.toString());
            Alertas.exito("Emisión nota de retiro", "Se generado con éxito la nota de retiro en :\n" +
                    ruta);
        } catch (Exception e) {
            e.printStackTrace();
            Alertas.error("Emisión nota de retiro", e.getMessage());
        }

    }

}
