package SPRService.SPRService.util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EMailSender {

    private final Properties properties;

    public EMailSender() {
        this.properties = cargarConfiguracion();
    }

    //todo: hacer método genérico reutilizable para cargar archivos de propiedades
    private Properties cargarConfiguracion() {
        Properties prop = new Properties();
        try (InputStream input = EMailSender.class.getResourceAsStream("/mail.properties")) {
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

    public void enviarReview(String review, double csat, double nsp, double ces)
            throws EmailException {
        String mensaje = "CSAT: " + csat + "\n" +
                "NPS: " + nsp + "\n" +
                "CES: " + ces + "\n" +
                "Comentario: " + review;
        String destinatarioReview = properties.getProperty("mail.review.addressee.email").strip();
        MultiPartEmail email = configurarEmail(destinatarioReview, "Review SuperService", mensaje);
        email.send();
    }

    public void enviarMailRecuperacionContrasenia(String mailUsuario) throws EmailException {
        String destinatario = properties.getProperty("mail.review.addressee.email").strip();
        String asunto = "Recuperación de contraseña";
        String mensaje = "El usuario " + mailUsuario + " ha solicitado la el proceso de recuperación de contraseña.";
        MultiPartEmail mail = configurarEmail(destinatario, asunto, mensaje);
        mail.send();
    }

    public void enviarEmailApache(String destinatario, String asunto, String mensaje, File archivoAdjunto)
            throws EmailException {
        MultiPartEmail email = configurarEmail(destinatario, asunto, mensaje);

        if (archivoAdjunto != null) {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(archivoAdjunto.getPath());
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("Adjunto");
            attachment.setName(archivoAdjunto.getName());

            email.attach(attachment);
        }
        email.send();
    }

    /**
     * Configura lo básico para enviar el mail: host, puesto, credencial usuario y autenticación, remitente, etc.
     *
     * @param destinatario a quien le llega el mail.
     * @param asunto       asunto del mail.
     * @param mensaje      cuerpo del mail.
     * @return MultiPartEmail para continuar configurando el mail y enviarlo.
     * @throws EmailException si ocurre un problema al configurar el eMail.
     */
    private MultiPartEmail configurarEmail(String destinatario, String asunto, String mensaje) throws EmailException {
        String host = properties.getProperty("mail.smtp.host").strip();
        int port = Integer.parseInt(properties.getProperty("mail.smtp.port").strip());

        String authUser = properties.getProperty("mail.auth.user").strip();   // Usuario para Login (El código raro de Mailtrap)
        String authPass = properties.getProperty("mail.auth.pass").strip();   // Contraseña
        String fromEmail = properties.getProperty("mail.from.email").strip(); // Email que se muestra (ej: sistema@tienda.com)

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
        return email;
    }
}