package SPRService.SPRService.components;

import SPRService.SPRService.viewModels.DetalleVentaVM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import SPRService.SPRService.entities.DetalleRetiro;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// TODO: cambiar esta tabla por una celda personalizada
public class TablaDetallesVentaController implements Initializable {

    private ObservableList<DetalleVentaVM> obsListDetallesVM = FXCollections.observableArrayList();

    @FXML
    private TableView<DetalleVentaVM> tablaDetalles;
    @FXML
    private TableColumn<DetalleVentaVM, String> colNombreRepuesto, colMarcaRepuesto, colPrecioUni, colSubTotal;
    @FXML
    private TableColumn<DetalleVentaVM, Double> colCantidadVendida;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configColumnas();
        tablaDetalles.setItems(obsListDetallesVM);
    }

    private void configColumnas() {
        colNombreRepuesto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarcaRepuesto.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecioUni.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCantidadVendida.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
        colSubTotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
    }

    /**
     * Para que otros controladores puedan poblar esta tabla. Borra los que estaban y agrega los argumentados.
     * @param detalles La lista de detalles a mostrar.
     */
    public void setDetalles(List<DetalleRetiro> detalles) {
        this.obsListDetallesVM.clear();
        for (DetalleRetiro d : detalles) {
            this.obsListDetallesVM.add(new DetalleVentaVM(d));
        }
    }

    /**
     * Para que otros controladores puedan agregar un detalle a esta tabla.
     * @param d detalle a agregar.
     */
    public void addDetalle(DetalleRetiro d) {
        this.obsListDetallesVM.addFirst(new DetalleVentaVM(d));
    }

    public void clearTable() {
        this.obsListDetallesVM.clear();
    }

    /**
     * Para obtener el elemento seleccionado.
     * @return El detalle seleccionado, o null si no hay selección.
     */
    public DetalleRetiro getDetalle() {
        return tablaDetalles.getSelectionModel().getSelectedItem().getDetalleRetiro();
    }

    /**
     * Para obtener todos los elementos.
     * @return Los detalles, o null si no hay.
     */
    public List<DetalleRetiro> getDetalles() {
        List<DetalleRetiro> list = new ArrayList<>();
        for (DetalleVentaVM dvm : obsListDetallesVM) {
            list.add(dvm.getDetalleRetiro());
        }
        return list;
    }
}
