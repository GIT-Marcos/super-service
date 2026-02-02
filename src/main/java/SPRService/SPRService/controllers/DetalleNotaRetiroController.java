package SPRService.SPRService.controllers;

import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.tablas.NotaRetiroViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DetalleNotaRetiroController implements Initializable, DataReceiver<NotaRetiroViewModel> {

    //todo: hacer estas listas de items retirados y trabajos un objeto reutilizable como componente por cada vista.
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();

    @FXML
    private Label labNroNota, labFechaNota, labTipoUso, labEstado;
    @FXML
    private ListView<ItemDetalleViewModel> lista;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lista.setItems(this.items);
        lista.setCellFactory(new ItemCellFactory().setMostrarBotonEliminar(false));
        String css = getClass().getResource("/styles/celdasDetalles.css").toExternalForm();
        lista.getStylesheets().add(css);
    }

    @Override
    public void receiveData(NotaRetiroViewModel data) {
        java.util.List<ItemDetalleRetiroViewModel> retiros = data.getNotaOriginal().getDetallesRetiro().stream()
                .map(ItemDetalleRetiroViewModel::new).toList();
        this.items.setAll(retiros);

        // TODO: usar de esta forma las propiedades de los view models para desacoplar un poco
        labNroNota.setText(String.valueOf(data.idNotaProperty().getValue()));
        labFechaNota.setText(String.valueOf(data.fechaProperty().getValueSafe()));
        labEstado.setText(data.estadoProperty().getValueSafe());
        labTipoUso.setText(data.tipoProperty().getValueSafe());
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }
}
