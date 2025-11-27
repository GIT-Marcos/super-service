package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaRepuesto;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.viewModels.celdas.ItemRepuestoViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgregarRepuestoController implements Initializable, ModalController<DetalleRetiro>,
        DataReceiver<List<DetalleRetiro>> {

    private DetalleRetiro detalleRetiro;
    private List<DetalleRetiro> detallesExistentes;
    private final RepuestoServ repuestoServ;
    private ValidationSupport valSupp;
    private ObservableList<ItemRepuestoViewModel> items = FXCollections.observableArrayList();

    @FXML
    private ListView<ItemRepuestoViewModel> lvRepuestos;
    @FXML
    private CustomTextField ctfCantidad;
    @FXML
    private TextField tfCodBarras, tfNombre, tfMarca;
    @FXML
    private CheckBox cbStockBajo, cbStockNormal;

    @Inject
    public AgregarRepuestoController(RepuestoServ repuestoServ) {
        this.repuestoServ = repuestoServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        valSupp = new ValidationSupport();
        valSupp.registerValidator(ctfCantidad, Validator.createRegexValidator(
                "Formato inválido", "^(?!0+(?:\\.0{1,2})?$)\\d{1,7}(?:\\.\\d{1,2})?$\n",
                org.controlsfx.validation.Severity.WARNING
        ));

        lvRepuestos.setCellFactory(cell -> new CeldaRepuesto());
        String css = getClass().getResource("/styles/celdaRepuesto.css").toExternalForm();
        lvRepuestos.getStylesheets().add(css);
        lvRepuestos.setItems(items);

        cargarItems(repuestoServ.verTodos());
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
    private void buscar() {
        cargarItems(repuestoServ.buscarConCriteria(tfCodBarras.getText().strip(), tfNombre.getText().strip(),
                tfMarca.getText().strip(), cbStockNormal.isSelected(), cbStockBajo.isSelected(),
                "detalle", 0));
    }

    @FXML
    private void agregarRepuesto() {
        Double cantidad;
        SPRService.SPRService.viewModels.celdas.ItemRepuestoViewModel vm =
                lvRepuestos.getSelectionModel().getSelectedItem();
        if (vm == null) {
            Notifications.create()
                    .title("Agregar repuesto")
                    .text("Debe seleccionar un repuesto para agregarlo.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        Repuesto r = vm.getRepuesto();
        if (verificarDuplicado(r)) {
            Notifications.create()
                    .title("Agregar repuesto")
                    .text("El repuesto seleccionado ya ha sido agregado al detalle.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        try {
            cantidad = ManejadorInputs.cantidadStock(ctfCantidad.getText(), true);
            validarStock(r, cantidad);
            detalleRetiro = new DetalleRetiro(null, cantidad, r);
            cerrarVentana();
        } catch (RuntimeException e) {
            Notifications.create()
                    .title("Agregar repuesto")
                    .text(e.getMessage())
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showWarning();
        }
    }

    private void validarStock(Repuesto r, Double cantidadParaRetirar) {
        if (!r.getStock().haySuficiente(cantidadParaRetirar)) throw new IllegalArgumentException(
                "No hay suficiente stock para retirar esa cantidad.");
    }

    private void cargarItems(List<Repuesto> repuestos) {
        items.clear();
        for (Repuesto r : repuestos) {
            items.add(new ItemRepuestoViewModel(r));
        }
    }

    private boolean verificarDuplicado(Repuesto r) {
        if (this.detallesExistentes != null) {
            Optional<DetalleRetiro> optionalDuplicado = this.detallesExistentes.stream().filter(d ->
                    d.getRepuesto().getId().equals(r.getId())).findAny();
            return optionalDuplicado.isPresent();
        }
        return false;
    }

    private void cerrarVentana() {
        Stage s = (Stage) lvRepuestos.getScene().getWindow();
        s.close();
    }
}
