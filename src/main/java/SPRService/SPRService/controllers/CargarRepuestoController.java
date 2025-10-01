package SPRService.SPRService.controllers;

import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.services.RepuestoServ;
import com.google.inject.Inject;
import jakarta.persistence.PersistenceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarRepuestoController implements Initializable, DataReceiver<Repuesto>, ModalController<Repuesto> {

    private Repuesto repuesto;
    //    private Stock stock = new Stock();
    private final RepuestoServ repuestoServ;
    private boolean flagModificacion = false;

    @FXML
    private TextField tfCodBarra;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfPrecio;
    @FXML
    private TextField tfCantidadStock;
    @FXML
    private TextField tfCantidadStockMin;
    @FXML
    private TextField tfLote;
    @FXML
    private TextField tfObservaciones;
    @FXML
    private ComboBox<String> comboMarcas;
    @FXML
    private ComboBox<String> comboUniMedidas;
    @FXML
    private ComboBox<String> comboUbicaciones;

    @Inject
    public CargarRepuestoController(RepuestoServ repuestoServ) {
        this.repuestoServ = repuestoServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarCombos();
    }

    @Override
    public void receiveData(Repuesto data) {
        if (data != null) {
            flagModificacion = true;
            llenarCamposParaModificacionRepuesto(data);
        }
    }

    @Override
    public Optional<Repuesto> getResult() {
        return Optional.ofNullable(this.repuesto);
    }

    @FXML
    private void cargarRepuesto(ActionEvent event) {
        Repuesto repuestoParaCargar;
        String codBarra = tfCodBarra.getText().trim();
        String marca = comboMarcas.getSelectionModel().getSelectedItem();
        String nombre = tfNombre.getText().trim();
        String inputPrecio = tfPrecio.getText().trim();
        String inputCantidad = tfCantidadStock.getText().trim();
        String inputCantidadMin = tfCantidadStockMin.getText().trim();
        String uniMedida = comboUniMedidas.getSelectionModel().getSelectedItem();
        String ubicacion = comboUbicaciones.getSelectionModel().getSelectedItem();
        String lote = tfLote.getText().trim();
        String observaciones = tfObservaciones.getText().trim();
        BigDecimal precio;
        Double cantidad;
        Double cantidadMin;
        try {
            ManejadorInputs.codBarras(codBarra, true);
            ManejadorInputs.textoGenerico(marca, true, null, 30);
            ManejadorInputs.textoGenerico(nombre, true, null, 40);
            precio = ManejadorInputs.dinero(inputPrecio, true);
            cantidad = ManejadorInputs.cantidadStock(inputCantidad, true);
            cantidadMin = ManejadorInputs.cantidadStock(inputCantidadMin, true);
            ManejadorInputs.textoGenerico(uniMedida, true, null, 20);
            ManejadorInputs.textoGenerico(ubicacion, true, null, 20);
            ManejadorInputs.textoGenerico(lote, false, null, 40);
            ManejadorInputs.textoGenerico(observaciones, false, null, 100);
        } catch (NumberFormatException nfe) {
            Alertas.aviso("Carga repuesto", nfe.getMessage());
            return;
        } catch (IllegalArgumentException iae) {
            Alertas.aviso("Carga repuesto", iae.getMessage());
            return;
        }

        //TODO: usar patrón de diseño para crear objetos esto es un asco
        if (!flagModificacion) {
            Stock stock = new Stock(null, cantidad, cantidadMin, uniMedida, ubicacion, lote, observaciones);
            repuestoParaCargar = new Repuesto(null, codBarra, marca, nombre, precio, stock);
        } else {
            Stock stock = new Stock(this.repuesto.getStock().getId(), cantidad, cantidadMin, uniMedida, ubicacion, lote, observaciones);
            repuestoParaCargar = new Repuesto(this.repuesto.getId(), codBarra, marca, nombre, precio,
                    stock);
        }

        boolean resultado = Alertas.confirmacion("Guardar repuesto",
                "¿Está seguro que desea guardar el repuesto: " + repuestoParaCargar.getDetalle() + "?");
        if (!resultado) return;

        try {
            if (!flagModificacion) {
                repuestoParaCargar = repuestoServ.cargarRepuesto(repuestoParaCargar);
            } else {
                try {
                    repuestoParaCargar = repuestoServ.modificarRepuesto(repuestoParaCargar);
                } catch (PersistenceException e) {
                    if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                            e.getCause() instanceof org.postgresql.util.PSQLException) {
                        throw new DuplicateProductException("Ya existe un producto con el código de barras: "
                                + repuestoParaCargar.getCodBarra() + " en el sistema.");
                    } else {
                        throw e;
                    }
                }
            }
            Alertas.exito("Guardar repuesto", "Se a guardado con éxito el repuesto: " +
                    repuestoParaCargar.getDetalle());
            cerrar(event);
        } catch (DuplicateProductException e) {
            Alertas.aviso("Producto Duplicado", e.getMessage());
            return;
        }
        this.repuesto = repuestoParaCargar;
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    private void llenarCombos() {
        ObservableList<String> observableListMarcas = FXCollections.observableArrayList();
        observableListMarcas.add("Corven");
        observableListMarcas.add("Fate");
        observableListMarcas.add("Mirgor");
        observableListMarcas.add("Bosch");
        observableListMarcas.add("Valeo");
        observableListMarcas.add("SKF");
        comboMarcas.setItems(observableListMarcas);
        /*****************/
        ObservableList<String> observableListUniMed = FXCollections.observableArrayList();
        observableListUniMed.add("Unidad");
        observableListUniMed.add("Metros");
        observableListUniMed.add("Kilos");
        observableListUniMed.add("Gramos");
        observableListUniMed.add("Litros");
        comboUniMedidas.setItems(observableListUniMed);
        comboUniMedidas.getSelectionModel().select(0);
        /*****************/
        ObservableList<String> observableListUbic = FXCollections.observableArrayList();
        observableListUbic.add("Depósito A");
        observableListUbic.add("Depósito B");
        observableListUbic.add("Depósito C");
        observableListUbic.add("Depósito D");
        observableListUbic.add("Depósito E");
        comboUbicaciones.setItems(observableListUbic);
    }

    /**
     * Para llenar los campos con los datos de un repuesto y poder modificarlo.
     */
    private void llenarCamposParaModificacionRepuesto(Repuesto r) {
        this.repuesto = r;

        this.tfCodBarra.setText(r.getCodBarra());
        if (this.comboMarcas.getItems().contains(r.getMarca())) {
            this.comboMarcas.getSelectionModel().select(r.getMarca());
        } else {
            this.comboMarcas.getItems().add(r.getMarca());
            this.comboMarcas.getSelectionModel().select(r.getMarca());
        }
        this.tfNombre.setText(r.getDetalle());
        this.tfPrecio.setText(r.getPrecio().toString());
        this.tfCantidadStock.setText(r.getStock().getCantidadExistente().toString());
        this.tfCantidadStockMin.setText(r.getStock().getCantMinima().toString());
        this.comboUniMedidas.getSelectionModel().select(r.getStock().getUnidadMedida());
        if (this.comboUbicaciones.getItems().contains(r.getStock().getUbicacion())) {
            this.comboUbicaciones.getSelectionModel().select(r.getStock().getUbicacion());
        } else {
            this.comboUbicaciones.getItems().add(r.getStock().getUbicacion());
            this.comboUbicaciones.getSelectionModel().select(r.getStock().getUbicacion());
        }
        this.tfLote.setText(r.getStock().getLote());
        this.tfObservaciones.setText(r.getStock().getObservaciones());
    }
}
