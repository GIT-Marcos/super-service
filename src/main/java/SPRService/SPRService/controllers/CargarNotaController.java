package SPRService.SPRService.controllers;

import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.generadores.GeneradorTXT;
import SPRService.SPRService.util.generadores.Impresor;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarNotaController implements Initializable, ModalController<NotaRetiro> {

    private final Navigator navigator;
    private final NotaRetiroServ notaRetiroServ;
    private NotaRetiro paraDevolver;
    private List<DetalleRetiro> detalles = new ArrayList<>();
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();

    @FXML
    private ListView<ItemDetalleViewModel> lista;
    @FXML
    private CheckBox chkGuardarRutaDefecto, chkImprimir;
    @FXML
    private Label lblTotalItems;

    @Inject
    public CargarNotaController(AppCoordinator coordinator, NotaRetiroServ notaRetiroServ) {
        this.navigator = coordinator.getMainNavigator();
        this.notaRetiroServ = notaRetiroServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControles();
    }

    @Override
    public Optional<NotaRetiro> getResult() {
        return Optional.ofNullable(paraDevolver);
    }

    private void configurarControles() {
        lista.setItems(items);
        lista.setCellFactory(new ItemCellFactory(this::eliminarItem));
        String css = getClass().getResource("/styles/celdasDetalles.css").toExternalForm();
        lista.getStylesheets().add(css);
    }

    @FXML
    private void agregarProducto() {
        Optional<DetalleRetiro> result = navigator.openModal(Views.AGREGAR_REPUESTO, "Agregar repuesto a Nota de Retiro",
                detalles);
        result.ifPresent(detalle -> {
            detalles.add(detalle);
            items.add(new ItemDetalleRetiroViewModel(detalle));
            contarDetalles();
        });
    }

    @FXML
    private void limpiarLista() {
        detalles.clear();
        items.clear();
        contarDetalles();
    }

    @FXML
    private void guardarNota(ActionEvent event) {
        if (detalles.isEmpty()) {
            Notifications.create()
                    .title("Cargar nota")
                    .text("Debe agregar productos a la nota para poder cargarla.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }
        if (!SimpleDialogs.confirmacion("Cargar nota", "¿Esté seguro que desea cargar esta Nota de Retiro?"))
            return;
        if (SimpleDialogs.confirmacion("Cargar nota", "¿Desea crear un archivo de texto de la nota de retiro?")) {
            gestionarNota(event);
        }

        try {
            NotaRetiro nota = new NotaRetiro(null, NotaRetiro.TipoUsoRetiro.OTRO, detalles);
            this.paraDevolver = notaRetiroServ.guardarNota(nota);
            Notifications.create()
                    .title("Cargar nota")
                    .text("La Nota de Retiro se ha cargado con éxito.")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showInformation();
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        } catch (Exception e) {
            Notifications.create()
                    .title("Cargar nota")
                    .text(e.getMessage())
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showError();
            e.printStackTrace();
        }
    }

    private void gestionarNota(ActionEvent event) {
        File file;
        if (chkGuardarRutaDefecto.isSelected()) {
            file = new File("C:\\Users\\Usuario\\Desktop\\nota retiro.txt");
        } else {
            file = SimpleDialogs.selectorRuta(event, "Seleccione donde quiere guardar la nota",
                    "nota retiro.txt",
                    new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        }
        if (file == null) return;
        GeneradorTXT.generaNotaRetiro(detalles, file);

        if (chkImprimir.isSelected())
            Impresor.imprimirConSistema(file);
    }

    private void eliminarItem(ItemDetalleViewModel item) {
        items.remove(item);
        detalles.remove(((ItemDetalleRetiroViewModel) item).getDetalleRetiro());
        contarDetalles();
    }

    private void contarDetalles() {
        lblTotalItems.setText(String.valueOf(detalles.size()));
    }
}
