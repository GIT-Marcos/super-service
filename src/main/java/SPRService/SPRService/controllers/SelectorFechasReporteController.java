package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.util.SafeLocalDateConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class SelectorFechasReporteController implements Initializable, ModalController<LocalDate[]> {

    private LocalDate[] fechas;

    @FXML
    private DatePicker dateMin;
    @FXML
    private DatePicker dateMax;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateMin.setConverter(new SafeLocalDateConverter());
        dateMax.setConverter(new SafeLocalDateConverter());
    }

    @Override
    public Optional<LocalDate[]> getResult() {
        return Optional.ofNullable(fechas);
    }

    @FXML
    private void continuar(ActionEvent event) {
        this.fechas = new LocalDate[2];
        this.fechas[0] = dateMin.getValue();
        this.fechas[1] = dateMax.getValue();
        // todo: agregar checkboxes para seleccionar varios formatos de creación para el reporte.
        cancelar(event);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }
}
