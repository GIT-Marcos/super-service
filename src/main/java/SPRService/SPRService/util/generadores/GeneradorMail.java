package SPRService.SPRService.util.generadores;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GeneradorMail {

    //todo: método genérico para cargar archivos de propiedades
    private static Properties cargarConfiguracion() {
        Properties prop = new Properties();
        try (InputStream input = GeneradorMail.class.getResourceAsStream("/mail.properties")) {
            if (input == null) {
                System.out.println("No se pudo encontrar mail.properties");
                return null;
            }
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }

    public static void enviarEmailApache(String destinatario, String asunto, String mensaje, File archivoAdjunto)
            throws EmailException {

        // 1. Cargar configuración del archivo
        Properties config = cargarConfiguracion();
        if (config == null) throw new EmailException("No se pudo cargar la configuración del correo.");

        // LEER PROPIEDADES (Usamos .trim() para evitar errores de espacios)
        String host = config.getProperty("mail.smtp.host").trim();
        int port = Integer.parseInt(config.getProperty("mail.smtp.port").trim());

        // --- CAMBIO CLAVE AQUÍ ---
        String authUser = config.getProperty("mail.auth.user").trim();   // Usuario para Login (El código raro de Mailtrap)
        String authPass = config.getProperty("mail.auth.pass").trim();   // Contraseña
        String fromEmail = config.getProperty("mail.from.email").trim(); // Email que se muestra (ej: sistema@tienda.com)
        // -------------------------

        // 2. Configuración del Servidor
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(host);
        email.setSmtpPort(port);

        // AQUÍ USAMOS EL USUARIO DE LOGIN (authUser)
        email.setAuthenticator(new DefaultAuthenticator(authUser, authPass));
        email.setStartTLSEnabled(true);

        // 3. Datos del correo
        // AQUÍ USAMOS EL CORREO VISUAL (fromEmail)
        email.setFrom(fromEmail);
        email.addTo(destinatario);
        email.setSubject(asunto);
        email.setMsg(mensaje);

        // 4. Adjuntar el archivo
        if (archivoAdjunto != null) {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(archivoAdjunto.getPath());
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("Adjunto");
            attachment.setName(archivoAdjunto.getName());

            email.attach(attachment);
        }

        // 5. Enviar
        email.send();
    }
}