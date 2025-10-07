package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.services.MarcaRepuestoServ;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.SimpleDialogs;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarRepuestoController implements Initializable, DataReceiver<Repuesto>, ModalController<Repuesto> {

    private Repuesto repuesto;
    private final RepuestoServ repuestoServ;
    private final MarcaRepuestoServ marcaRepuestoServ;
    private ObservableList<MarcaRepuesto> obsListMarcaRepuesto = FXCollections.observableArrayList();
    private boolean flagModificacion = false;

    @FXML
    private TextField tfCodBarra, tfNombre, tfPrecio, tfCantidadStock, tfCantidadStockMin, tfLote,
            tfObservaciones;
    @FXML
    private ComboBox<MarcaRepuesto> comboMarcas;
    @FXML
    private ComboBox<String> comboUniMedidas;
    @FXML
    private ComboBox<String> comboUbicaciones;

    @Inject
    public CargarRepuestoController(RepuestoServ repuestoServ, MarcaRepuestoServ marcaRepuestoServ) {
        this.repuestoServ = repuestoServ;
        this.marcaRepuestoServ = marcaRepuestoServ;
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
    private void nuevaMarca(ActionEvent event) {
        String nombreMarca = SimpleDialogs.nombreMarcaRepuesto(event);
        if (nombreMarca == null) return;

        MarcaRepuesto marcaRepuesto = new MarcaRepuesto(null, nombreMarca, new HashSet<>());
        try {
            marcaRepuesto = marcaRepuestoServ.cargarMarca(marcaRepuesto);
            obsListMarcaRepuesto.addFirst(marcaRepuesto);
        } catch (RuntimeException e) {
            Alertas.error("Crear nueva marca de repuestos", e.getMessage());
//            throw new RuntimeException(e);
        }
    }

    @FXML
    private void cargarRepuesto(ActionEvent event) {
        Repuesto repuestoParaCargar;
        String codBarra = tfCodBarra.getText().strip();
        MarcaRepuesto marca = comboMarcas.getSelectionModel().getSelectedItem();
        if (marca == null) {
            Alertas.aviso("Guardar repuesto", "Debe seleccionar una marca para el repuesto.");
            return;
        }
        String nombre = tfNombre.getText().strip();
        String inputPrecio = tfPrecio.getText().strip();
        String inputCantidad = tfCantidadStock.getText().strip();
        String inputCantidadMin = tfCantidadStockMin.getText().strip();
        String uniMedida = comboUniMedidas.getSelectionModel().getSelectedItem();
        String ubicacion = comboUbicaciones.getSelectionModel().getSelectedItem();
        String lote = tfLote.getText().strip();
        String observaciones = tfObservaciones.getText().strip();
        BigDecimal precio;
        Double cantidad;
        Double cantidadMin;
        try {
            ManejadorInputs.codBarras(codBarra, true);
            ManejadorInputs.textoGenerico(nombre, true, null, 40);
            precio = ManejadorInputs.dinero(inputPrecio, true);
            cantidad = ManejadorInputs.cantidadStock(inputCantidad, true);
            cantidadMin = ManejadorInputs.cantidadStock(inputCantidadMin, true);
            ManejadorInputs.textoGenerico(uniMedida, true, null, 20);
            ManejadorInputs.textoGenerico(ubicacion, true, null, 20);
            ManejadorInputs.textoGenerico(lote, false, null, 40);
            ManejadorInputs.textoGenerico(observaciones, false, null, 100);
        } catch (IllegalArgumentException iae) {
            Alertas.aviso("Guardar repuesto", iae.getMessage());
            return;
        }
        boolean resultado = Alertas.confirmacion("Guardar repuesto",
                "¿Está seguro que desea guardar el repuesto: " + nombre + "?");
        if (!resultado) return;

        try {
            //TODO: usar patrón de diseño para crear objetos esto es un asco
            if (!flagModificacion) {
                Stock stock = new Stock(null, cantidad, cantidadMin, uniMedida, ubicacion, lote, observaciones);
                repuestoParaCargar = new Repuesto(null, codBarra, nombre, precio, marca, stock);
                repuestoParaCargar = repuestoServ.cargarRepuesto(repuestoParaCargar);
            } else {
                try {
                    Stock stock = new Stock(this.repuesto.getStock().getId(), cantidad, cantidadMin, uniMedida, ubicacion,
                            lote, observaciones);
                    repuestoParaCargar = new Repuesto(this.repuesto.getId(), codBarra, nombre, precio, marca, stock);
                    repuestoParaCargar = repuestoServ.modificarRepuesto(repuestoParaCargar);
                } catch (PersistenceException e) {
                    if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                            e.getCause() instanceof org.postgresql.util.PSQLException) {
                        throw new DuplicateProductException("Ya existe un producto con el código de barras: "
                                + codBarra + " en el sistema.");
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
        obsListMarcaRepuesto.addAll(marcaRepuestoServ.verTodas());
        comboMarcas.setItems(obsListMarcaRepuesto);
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
        if (this.comboMarcas.getItems().contains(r.getMarcaRepuesto())) {
            this.comboMarcas.getSelectionModel().select(r.getMarcaRepuesto());
        } else {
            this.comboMarcas.getItems().add(r.getMarcaRepuesto());
            this.comboMarcas.getSelectionModel().select(r.getMarcaRepuesto());
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
