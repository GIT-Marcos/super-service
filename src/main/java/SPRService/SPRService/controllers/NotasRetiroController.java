package SPRService.SPRService.controllers;

import SPRService.SPRService.viewModels.tablas.RepuestoRowViewModel;
import SPRService.SPRService.components.CeldaDetalleRetiro;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.GeneradorNotaRetiroTXT;
import SPRService.SPRService.util.generadores.Impresor;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NotasRetiroController implements Initializable {

    private final NotaRetiroServ notaRetiroServ;
    private final RepuestoServ repuestoServ;
    private final Navigator navigator;
    private ObservableList<NotaRetiro> notasObsList = FXCollections.observableArrayList();
    private ObservableList<DetalleRetiro> detallesObsList = FXCollections.observableArrayList();
    //    private List<Repuesto> repuestos = new ArrayList<>();
    private ObservableList<RepuestoRowViewModel> obsListRepuestoVM = FXCollections.observableArrayList();

    @Inject
    public NotasRetiroController(NotaRetiroServ notaRetiroServ, AppCoordinator appCoordinator, RepuestoServ repuestoServ) {
        this.notaRetiroServ = notaRetiroServ;
        this.navigator = appCoordinator.getMainNavigator();
        this.repuestoServ = repuestoServ;
    }

    @FXML
    private ListView<NotaRetiro> listViewNotas;
    @FXML
    private DatePicker dateFechaMin;
    @FXML
    private DatePicker dateFechaMax;
    @FXML
    private TextField tfCodBarras;
    @FXML
    private TextField tfMarca;
    @FXML
    private TextField tfNombre;
    @FXML
    private ComboBox<String> cbTipoOrden;
    @FXML
    private ComboBox<String> cbOrdenarPor;
    @FXML
    private CheckBox checkVerBajo;
    @FXML
    private CheckBox checkVerNormal;
    @FXML
    private Button btnCrearNota;
    @FXML
    private ListView<DetalleRetiro> listViewDetalles;
    @FXML
    private TableView<RepuestoRowViewModel> tablaRepuestos;
    @FXML
    private TableColumn<RepuestoRowViewModel, String> colCodigo;
    @FXML
    private TableColumn<RepuestoRowViewModel, String> colNombre;
    @FXML
    private TableColumn<RepuestoRowViewModel, String> colMarca;
    @FXML
    private TableColumn<RepuestoRowViewModel, Double> colExistente;
    @FXML
    private TableColumn<RepuestoRowViewModel, Double> colMin;
    @FXML
    private TableColumn<RepuestoRowViewModel, String> colUniMedida;
    @FXML
    private CheckBox checkGuardar;
    @FXML
    private CheckBox checkImprimir;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateFechaMin.setConverter(new SafeLocalDateConverter());
        dateFechaMax.setConverter(new SafeLocalDateConverter());
        llenarCombos();
        // ListView detalles
        this.notasObsList.addAll(notaRetiroServ.verTodasPorFecha());
        listViewNotas.setItems(this.notasObsList);
        listViewNotas.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(NotaRetiro item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("N° de nota: " + item.getId() + " . Fecha de nota: " + item.getFecha());
                }
            }
        });
        // ListView nueva nota
        this.listViewDetalles.setItems(this.detallesObsList);
        listViewDetalles.setCellFactory(param -> new CeldaDetalleRetiro());
        // seteo de Tabla
        configColumnas();
        tablaRepuestos.setItems(this.obsListRepuestoVM);
        seteaEstiloTabla();
        llenarFilas(repuestoServ.verTodos());
    }

    @FXML
    private void actualizar() {
        this.notasObsList.clear();
        this.notasObsList.addAll(this.notaRetiroServ.verTodasPorFecha());
    }

    @FXML
    private void aplicarFiltros() {
        if (dateFechaMin.getValue() == null && dateFechaMax.getValue() == null) return;
        this.notasObsList.clear();
        this.notasObsList.addAll(notaRetiroServ.buscarPorFecha(dateFechaMin.getValue(), dateFechaMax.getValue()));
    }

    @FXML
    private void verDetalles() {
        NotaRetiro n = listViewNotas.getSelectionModel().getSelectedItem();
        if (n == null) {
            Alertas.aviso("Detalles de nota re retiro", "Debe seleccionar una nota para " +
                    "ver sus detalles");
            return;
        }
        navigator.openModal(Views.DETALLE_NOTA_RETIRO,
                "Detalles de nota de retiro", n);
    }

    @FXML
    private void eliminarNota() {
        NotaRetiro n = listViewNotas.getSelectionModel().getSelectedItem();
        if (n == null) {
            Alertas.aviso("Cancelar nota de retiro", "Debe seleccionar una nota para cancelarla.");
            return;
        }
        if (!Alertas.confirmacion("Cancelar nota de retiro", "Esta acción es irreversible y el stock " +
                "retirado de los productos se restablecerá.\n ¿Confirmar cancelación de nota e retiro?"))
            return;
        try {
            notaRetiroServ.cancelarNota(n);
            notasObsList.remove(n);
            Alertas.exito("Cancelar nota de retiro", "Se ha cancelado la nota de retiro con éxito.");
        } catch (RuntimeException e) {
            Alertas.error("Cancelar nota de retiro", "Ha ocurrido un error inesperado al cancelar la nota.");
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void buscar() {
        int tipoOrden = cbTipoOrden.getSelectionModel().getSelectedIndex();
        Boolean verNormal = checkVerNormal.isSelected();
        Boolean verBajo = checkVerBajo.isSelected();
        if (!verNormal && !verBajo) {
            obsListRepuestoVM.clear();
            return;
        }
        List<Repuesto> results = this.repuestoServ.buscarConCriteria(tfCodBarras.getText().trim(),
                tfNombre.getText().trim(), tfMarca.getText().trim(), verNormal, verBajo,
                tomarColOrden(), tipoOrden);
        llenarFilas(results);
    }

    @FXML
    private void verTodos() {
        llenarFilas(repuestoServ.verTodos());
    }

    @FXML
    private void agregar() {
        RepuestoRowViewModel rrvm = tablaRepuestos.getSelectionModel().getSelectedItem();
        if (rrvm == null) {
            Alertas.aviso("Agregar repuesto",
                    "Debe seleccionar un repuesto de la tabla para agregarlo a la venta.");
            return;
        }
        for (DetalleRetiro d : detallesObsList) {
            if (d.getRepuesto().equals(rrvm.getRepuestoOriginal())) {
                Alertas.aviso("Agregar repuesto", "Ya se ha añadido ese repuesto a la venta.");
                return;
            }
        }
        //abrir dialog para cantidad
        Double cantidad = SimpleDialogs.inputStock();
        if (cantidad == null) return;

        Repuesto repuesto = rrvm.getRepuestoOriginal();
        Double existenteStock = repuesto.getStock().getCantidadExistente();
        if (cantidad > existenteStock) {
            Alertas.error("Nueva nota de retiro", "La cantidad que se intenta retirar: "
                    + cantidad + "\n" + "es mayor que el existente: " + repuesto.getStock().getCantidadExistente());
            return;
        }
        Double stockNuevo = existenteStock - cantidad;
        if (cantidad > stockNuevo) {
            boolean confir = Alertas.confirmacion("Nueva nota de retiro",
                    "El stock luego del retiro quedará por " +
                            "debajo del mínimo tolerado.\n" +
                            "La cantidad de stock de " + repuesto.getDetalle() +
                            " quedará por de bajo del mínimo establecido " +
                            "(" + repuesto.getStock().getCantMinima() + " " + repuesto.getStock().getUnidadMedida() + ").\n" +
                            "¿Continuar con la venta?");
            if (!confir) {
                return;
            }
        }
        DetalleRetiro detalleRetiro = new DetalleRetiro(null, cantidad, repuesto);
        detallesObsList.add(detalleRetiro);
        btnCrearNota.setDisable(true);
    }

    @FXML
    private void limpiarLista() {
        this.detallesObsList.clear();
    }

    @FXML
    private void emitirNota(ActionEvent event) {
        if (this.detallesObsList.isEmpty()) {
            Alertas.aviso("Emisión nota de retiro", "Debe cargar productos para la venta.");
            return;
        }

        File file;
        if (checkGuardar.isSelected()) {
            file = new File("C:\\Users\\Usuario\\Desktop\\nota retiro.txt");
        } else {
            file = SimpleDialogs.selectorRuta(event, "Seleccione donde guardar la nora de retiro",
                    "nota retiro.txt",
                    new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        }
        if (file == null) {
            return;
        }

        try {
            GeneradorNotaRetiroTXT.generaNotaRetiro(this.detallesObsList, file);
            if (checkImprimir.isSelected()) {
                Impresor.imprimirConSistema(file);
            }
            btnCrearNota.setDisable(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void crearNota() {
        if (!Alertas.confirmacion("Nueva nota de retiro", "¿Confirmar creación de nota de retiro?"))
            return;

        try {
            NotaRetiro notaParaCargar = new NotaRetiro(null, detallesObsList);
            notaRetiroServ.guardarNota(notaParaCargar);
            Alertas.exito("Nueva nota de retiro", "Se ha creado la nota de retiro con éxito.");
            detallesObsList.clear();
            actualizar();
            btnCrearNota.setDisable(true);
            verTodos();
        } catch (Exception e) {
            Alertas.error("Nueva nota de retiro", "Error inesperado al guardar la nota.");
            throw new RuntimeException(e);
        }
    }

    private void configColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("coBarra"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colExistente.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("cantidadMinima"));
        colUniMedida.setCellValueFactory(new PropertyValueFactory<>("uniMedida"));
    }

    private void seteaEstiloTabla() {
        tablaRepuestos.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(RepuestoRowViewModel item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else if (item.getCantidad() <= item.getCantidadMinima()) {
                    setStyle("-fx-background-color: lightcoral;");
                } else {
                    //limpieza de estilo
                    setStyle("");
                }
            }
        });
    }

    private void llenarFilas(List<Repuesto> repuestos) {
        this.obsListRepuestoVM.clear();
        for (Repuesto r : repuestos) {
            this.obsListRepuestoVM.add(new RepuestoRowViewModel(r));
        }
    }

    private void llenarCombos() {
        ObservableList<String> obsListOrdenarPor = FXCollections.observableArrayList();
        obsListOrdenarPor.add("Detalle");
        obsListOrdenarPor.add("Marca");
        obsListOrdenarPor.add("Cod Barra");
        cbOrdenarPor.setItems(obsListOrdenarPor);
        cbOrdenarPor.getSelectionModel().select(0);
        ObservableList<String> obsListTipoOrden = FXCollections.observableArrayList();
        obsListTipoOrden.add("Ascendente");
        obsListTipoOrden.add("Descendente");
        cbTipoOrden.setItems(obsListTipoOrden);
        cbTipoOrden.getSelectionModel().select(0);
    }

    private String tomarColOrden() {
        int ordenarPor = cbOrdenarPor.getSelectionModel().getSelectedIndex();
        //los valores q toma son de los atributos de Repuesto.class
        return switch (ordenarPor) {
            case 1 -> "marca";
            case 2 -> "codBarra";
            default -> "detalle";
        };
    }
}
