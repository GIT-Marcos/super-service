package SPRService.SPRService.controllers.otros;

import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.EMailSender;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.commons.mail.EmailException;
import org.controlsfx.control.Rating; // Asegúrate de tener la librería ControlsFX importada en tu proyecto

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RateController implements Initializable, ModalController<Boolean> {

    private final EMailSender eMailSender;
    private Boolean result;

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

    @Override
    public Optional<Boolean> getResult() {
        return Optional.ofNullable(this.result);
    }

    @FXML
    void enviar(ActionEvent event) {
        double csat = Math.round(rate.getRating());
        double nsp = Math.round(rate2.getRating());
        double ces = Math.round(rate3.getRating());
        String comentario = tfComentario.getText().strip();

        try {
            eMailSender.enviarReview(comentario, csat, nsp, ces);
            this.result = true;
            closeWindow(event);
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