package SPRService.SPRService.controllers;

import SPRService.SPRService.viewModels.VehiculoVM;
import SPRService.SPRService.navigation.WizardStateProvider;
import SPRService.SPRService.navigation.WizardStepController;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BooleanSupplier;

public class StepDatosVehiculoController implements Initializable, WizardStepController {

    private final VehiculoVM vehiculoVM;

    @FXML
    private TextField tfPatente;
    @FXML
    private TextField tfColor;
    @FXML
    private TextField tfNroChasis;
    @FXML
    private TextField tfNroMotor;
    @FXML
    private Label lblAviso;

    @Inject
    public StepDatosVehiculoController(WizardStateProvider wsp) {
        this.vehiculoVM = wsp.getViewModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- ENLACE DE DATOS (DATA BINDING) ---
        // Campos de texto (bidireccional: la vista actualiza el VM y viceversa)
        tfPatente.textProperty().bindBidirectional(vehiculoVM.patenteProperty());
        tfNroChasis.textProperty().bindBidirectional(vehiculoVM.nroChasisProperty());
        tfNroMotor.textProperty().bindBidirectional(vehiculoVM.nroMotorProperty());
        tfColor.textProperty().bindBidirectional(vehiculoVM.colorProperty());
    }

    @Override
    public BooleanSupplier getValidador() {
        // Esto se evalúa solo cuando el controlador principal lo pide.
        // La lambda captura el estado actual de los campos de texto
        // y devuelve true si todos son válidos, false en caso contrario.
//        return () -> true;
        return this::validar;
    }

    @Override
    public void mostrarErrores() {
        try {
            // Reutilizamos la lógica de ManejadorInputs que lanza excepciones
            ManejadorInputs.patente(tfPatente.getText().strip(), true);
            ManejadorInputs.textoGenerico(tfColor.getText().strip(), true, 3, 40);
            ManejadorInputs.textoGenerico(tfNroChasis.getText().strip(), false, 17, 17);
            ManejadorInputs.textoGenerico(tfNroMotor.getText().strip(), false, 3, 20);
            lblAviso.setText(""); // Limpiar mensajes de error previos
        } catch (IllegalArgumentException e) {
            // La validación falló, mostramos el error
            Alertas.aviso("Datos del vehículo incorrectos", e.getMessage());
            lblAviso.setText(e.getMessage()); // También podemos mostrarlo en la UI
        }
    }

    //todo: esta repetición de código es un asco. El problema es del manejador de inputs.
    private boolean validar() {
        try {
            // Reutilizamos la lógica de ManejadorInputs que lanza excepciones
            ManejadorInputs.patente(tfPatente.getText().strip(), true);
            ManejadorInputs.textoGenerico(tfColor.getText().strip(), true, 3, 40);
            ManejadorInputs.textoGenerico(tfNroChasis.getText().strip(), false, 17, 17);
            ManejadorInputs.textoGenerico(tfNroMotor.getText().strip(), false, 3, 20);
            lblAviso.setText(""); // Limpiar mensajes de error previos
            return true;
        } catch (IllegalArgumentException e) {
            lblAviso.setText(e.getMessage());
            return false;
        }
    }
}
