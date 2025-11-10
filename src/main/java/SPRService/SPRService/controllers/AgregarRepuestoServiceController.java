package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgregarRepuestoServiceController implements Initializable, ModalController<DetalleRetiro>,
        DataReceiver<List<DetalleRetiro>> {

    private DetalleRetiro detalleRetiro;
    private List<DetalleRetiro> detallesExistentes;
    private final RepuestoServ repuestoServ;
    private ObservableList<Repuesto> obsListRepuestos = FXCollections.observableArrayList();

    @FXML
    private ListView<Repuesto> lvRepuestos;
    @FXML
    private CustomTextField ctfBuscar, ctfCantidad;

    @Inject
    public AgregarRepuestoServiceController(RepuestoServ repuestoServ) {
        this.repuestoServ = repuestoServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvRepuestos.setItems(obsListRepuestos);

        obsListRepuestos.addAll(repuestoServ.verTodos());
    }

    @Override
    public Optional<DetalleRetiro> getResult() {
        return Optional.ofNullable(this.detalleRetiro);
    }

    @Override
    public void receiveData(List<DetalleRetiro> data) {
        this.detallesExistentes = data;
    }

    @FXML
    private void agregarRepuesto() {
        Double cantidad;
        Repuesto r = lvRepuestos.getSelectionModel().getSelectedItem();
        if (r == null) {
            Alertas.aviso("Agregar repuesto", "Debe seleccionar un repuesto para agregarlo " +
                    "al service");
            return;
        }
        if (verificarDuplicado(r)) {
            Alertas.aviso("Agregar repuesto", "Ya se ha agregado el repuesto: \n" +
                    r.getDetalle() + "\nal service.");
            return;
        }

        try {
            cantidad = ManejadorInputs.cantidadStock(ctfCantidad.getText(), true);
        } catch (RuntimeException e) {
            Alertas.aviso("Agregar cantidad", e.getMessage());
            return;
        }
        detalleRetiro = new DetalleRetiro(null, cantidad, r);
        cerrarVentana();
    }

    private boolean verificarDuplicado(Repuesto r) {
        Optional<DetalleRetiro> optionalDuplicado = this.detallesExistentes.stream().filter(d ->
                d.getRepuesto().getId().equals(r.getId())).findAny();
        return optionalDuplicado.isPresent();
    }

    private void cerrarVentana() {
        Stage s = (Stage) ctfBuscar.getScene().getWindow();
        s.close();
    }
}
