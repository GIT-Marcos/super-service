package SPRService.SPRService.controllers.otros;

import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.EMailSender;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.mail.EmailException;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.Rating;

import java.net.URL;
import java.util.ResourceBundle;

public class RateController implements Initializable {

    private final EMailSender eMailSender;

    @FXML
    private Rating rate, rate2, rate3;
    @FXML
    private TextArea tfComentario;

    @Inject
    public RateController(EMailSender eMailSender) {
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void enviar(ActionEvent event) {
        double csat = Math.round(rate.getRating());
        double nsp = Math.round(rate2.getRating());
        double ces = Math.round(rate3.getRating());
        String comentario = tfComentario.getText().strip();

        try {
            eMailSender.enviarReview(comentario, csat, nsp, ces);
            closeWindow(event);
            Notifications.create()
                    .title("Gracias!!!")
                    .text("Se ha enviado su valoración\nMuchas gracias!")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
        } catch (EmailException e) {
            Alertas.error("Error Mail", "Fallo al enviar el correo. Verifique su " +
                    "conexión o configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}