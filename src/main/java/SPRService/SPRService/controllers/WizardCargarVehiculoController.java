package SPRService.SPRService.controllers;

import SPRService.SPRService.exceptions.DuplicateVehicleException;
import SPRService.SPRService.viewModels.VehiculoVM;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.navigation.*;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.PersistenceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.function.BooleanSupplier;

public class WizardCargarVehiculoController implements Initializable, ModalController<VehiculoVM> {

    private final WizardStateProvider wsp;
    private VehiculoVM vehiculoVM;
    private Optional<VehiculoVM> result = Optional.empty();
    private final VehiculoServ vehiculoServ;

    private final Navigator localNavigator;
    private final AsistenteState asistenteState;

    @FXML
    private Pane stackPane;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnFinalizar;

    @Inject
    public WizardCargarVehiculoController(Provider<FXMLLoader> fxmlLoaderProvider,
                                          VehiculoServ vehiculoServ, WizardStateProvider wsp) {
        this.localNavigator = new DefaultNavigator(fxmlLoaderProvider);


        this.vehiculoServ = vehiculoServ;
        this.asistenteState = new AsistenteState(
                Arrays.asList(Views.DATOS_VEHICULO, Views.MARCA_VEHICULO, Views.MODELO_VEHICULO));

        this.wsp = wsp;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        wsp.startNewVehicleWizard();
        try {
            // Intentamos obtener el ViewModel. Si falla, significa que nadie
            // lo preparó (estamos en modo CREAR).
            this.vehiculoVM = wsp.getViewModel();
            System.out.println("Wizard iniciado en modo EDICIÓN.");
        } catch (IllegalStateException e) {
            // Si no hay ViewModel, estamos en modo CREAR. Lo iniciamos.
            System.out.println("Wizard iniciado en modo CREAR.");
            wsp.startNewVehicleWizard();
            this.vehiculoVM = wsp.getViewModel();
        }
        localNavigator.bind(stackPane);
        // El navegador local necesita saber la ventana para futuros modales.
        stackPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnCloseRequest(event -> {
                            wsp.endWizard();
                        });
                    }
                });
            }
        });

        navegarAPaso(asistenteState.getPasoActualIndex());
    }

    @Override
    public Optional<VehiculoVM> getResult() {
        return this.result;
    }

    @FXML
    private void volver() {
        if (asistenteState.puedeRetroceder()) {
            asistenteState.retroceder();
            navegarAPaso(asistenteState.getPasoActualIndex());
        }
    }

    @FXML
    private void siguiente() {
        if (asistenteState.validarPasoActual()) {
            if (asistenteState.puedeAvanzar()) {
                asistenteState.avanzar();
                navegarAPaso(asistenteState.getPasoActualIndex());
            }
        } else {
            mostrarErroresPasoActual();
        }
    }

    @FXML
    private void finalizar(ActionEvent event) {
        if (asistenteState.validarPasoActual()) {
            boolean confir = Alertas.confirmacion("Cargar vehículo", "¿Confirmar carga de vehículo?");
            if (!confir) return;
            Vehiculo v = wsp.getViewModel().obtenerEntidadActualizada();
            try {
                if (v.getId() == null) {
                    v = vehiculoServ.cargarVehiculo(v);
                } else {
                    try {
                        v = vehiculoServ.modificarVehiculo(v);
                    } catch (PersistenceException e) {
                        if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                                e.getCause() instanceof org.postgresql.util.PSQLException) {
                            throw new DuplicateVehicleException("Error: ya existe un vehículo con la patente: " +
                                    v.getPatente() + " en el sistema.");
                        }else {
                            Alertas.error("Error de Base de Datos",
                                    "No se pudo guardar el vehículo. Causa: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                vehiculoVM.cargarDesdeEntidad(v);
                this.result = Optional.of(this.vehiculoVM);
                wsp.endWizard();
                Alertas.exito("Guardar vehículo", "Se guardado el vehículo con éxito.");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } catch (DuplicateVehicleException e) {
                Alertas.error("guardar vehículo", e.getMessage());
            }
        }
    }

    private void navegarAPaso(int indicePaso) {
        // 1. Obtenemos la vista a la que queremos navegar.
        Views vistaPaso = asistenteState.getVistaPaso(indicePaso);

        // 2. Delegamos TODA la lógica de carga y visualización al Navigator.
        // Él se encargará del caché, de crear el FXMLLoader, cargar el FXML y
        // poner el nodo resultante en el 'pasosContainer' que le indicamos en initialize().
        localNavigator.navigateTo(vistaPaso);

        // 3. Obtenemos el controlador de la vista que acabamos de mostrar.
        // El navigator nos lo da desde su caché.
        localNavigator.getControllerFor(vistaPaso).ifPresent(controller -> {
            // 4. Realizamos la lógica específica del asistente: registrar el validador.
            if (controller instanceof WizardStepController) {
                asistenteState.setValidadorPasoActual(
                        ((WizardStepController) controller).getValidador()
                );
            } else {
                // Si el controlador no implementa la interfaz, asumimos que siempre es válido.
                asistenteState.setValidadorPasoActual(() -> true);
            }
        });

        // 5. Actualizamos el estado de la UI del asistente.
        actualizarBotones();
    }

    private void mostrarErroresPasoActual() {
        localNavigator.getControllerFor(asistenteState.getVistaPasoActual())
                .ifPresent(controller -> {
                    // Verificamos si el controlador cumple con el contrato
                    if (controller instanceof WizardStepController) {
                        ((WizardStepController) controller).mostrarErrores();
                    } else {
                        Alertas.aviso("Error de validación",
                                "Por favor, complete los campos requeridos correctamente.");
                    }
                });
    }

    private void actualizarBotones() {
        btnVolver.setDisable(!asistenteState.puedeRetroceder());
        btnSiguiente.setVisible(!asistenteState.esUltimoPaso());
        btnFinalizar.setVisible(asistenteState.esUltimoPaso());
    }

    /**
     * Clase interna para gestionar el estado del asistente.
     * Encapsula la lógica de qué paso se está mostrando,
     * cuántos pasos hay y si se puede avanzar o retroceder.
     */
    private static class AsistenteState {
        private final List<Views> pasos;
        private int pasoActualIndex;

        // Almacena una referencia a la función de validación del paso actual.
        // Esto permite que la lógica de validación permanezca en el controlador del paso.
        private BooleanSupplier validadorPasoActual = () -> true; // Por defecto, siempre es válido.

        /**
         * Constructor que inicializa el estado del asistente.
         *
         * @param pasos Una lista ordenada de las vistas (enum Views) que componen el asistente.
         */
        public AsistenteState(List<Views> pasos) {
            if (pasos == null || pasos.isEmpty()) {
                throw new IllegalArgumentException("La lista de pasos no puede ser nula o vacía.");
            }
            this.pasos = pasos;
            this.pasoActualIndex = 0; // El asistente siempre empieza en el primer paso (índice 0).
        }

        /**
         * Avanza al siguiente paso si es posible.
         */
        public void avanzar() {
            if (puedeAvanzar()) {
                pasoActualIndex++;
            }
        }

        /**
         * Retrocede al paso anterior si es posible.
         */
        public void retroceder() {
            if (puedeRetroceder()) {
                pasoActualIndex--;
            }
        }

        /**
         * Comprueba si el asistente puede avanzar al siguiente paso.
         *
         * @return true si no está en el último paso, false en caso contrario.
         */
        public boolean puedeAvanzar() {
            return pasoActualIndex < pasos.size() - 1;
        }

        /**
         * Comprueba si el asistente puede retroceder al paso anterior.
         *
         * @return true si no está en el primer paso, false en caso contrario.
         */
        public boolean puedeRetroceder() {
            return pasoActualIndex > 0;
        }

        /**
         * Comprueba si el paso actual es el último del asistente.
         *
         * @return true si el índice actual corresponde al último paso.
         */
        public boolean esUltimoPaso() {
            return pasoActualIndex == pasos.size() - 1;
        }

        /**
         * Obtiene el índice del paso actual.
         *
         * @return El índice (basado en 0) del paso actual.
         */
        public int getPasoActualIndex() {
            return pasoActualIndex;
        }

        /**
         * Obtiene el enum Views correspondiente a un índice de paso.
         *
         * @param indice El índice del paso.
         * @return El enum Views para ese paso.
         */
        public Views getVistaPaso(int indice) {
            if (indice < 0 || indice >= pasos.size()) {
                throw new IndexOutOfBoundsException("Índice de paso fuera de rango: " + indice);
            }
            return pasos.get(indice);
        }

        /**
         * Obtiene la vista del paso actual.
         *
         * @return El enum Views para el paso actual.
         */
        public Views getVistaPasoActual() {
            return getVistaPaso(pasoActualIndex);
        }

        /**
         * Registra la función de validación para el paso que se está mostrando.
         * El controlador principal llamará a esta función antes de permitir avanzar.
         *
         * @param validador Una función que devuelve true si el paso es válido, false en caso contrario.
         */
        public void setValidadorPasoActual(BooleanSupplier validador) {
            this.validadorPasoActual = validador;
        }

        /**
         * Ejecuta la validación registrada para el paso actual.
         *
         * @return true si el paso es válido.
         */
        public boolean validarPasoActual() {
            return validadorPasoActual.getAsBoolean();
        }
    }
}

