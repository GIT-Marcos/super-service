package SPRService.SPRService.util.generadores;

import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
            NotificationHelper.mostrarExito("Exportar reporte",
                    "Reporte guardado y exportado con éxito.");
        } catch (IOException ex) {
            NotificationHelper.mostrarError("Exportar reporte", "Ocurrió un error al exportar el reporte.");
            ex.printStackTrace();
        }
    }
}
