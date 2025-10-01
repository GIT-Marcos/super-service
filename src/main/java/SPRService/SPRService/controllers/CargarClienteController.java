package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.DatosContacto;
import SPRService.SPRService.exceptions.DuplicateClientDNI;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import jakarta.persistence.PersistenceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarClienteController implements Initializable, DataReceiver<Cliente>, ModalController<Cliente> {

    private Cliente cliente;
    private final ClienteServ clienteServ;
    private boolean flagModifyMode = false;
    private ObservableList<String> obsListEmails = FXCollections.observableArrayList();
    private ObservableList<String> obsListNrosTelefono = FXCollections.observableArrayList();

    @FXML
    private TextField tfDNI;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellido;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfNro;
    @FXML
    private ListView<String> lvEmails;
    @FXML
    private ListView<String> lvNrosTelefono;

    @Inject
    public CargarClienteController(ClienteServ clienteServ) {
        this.clienteServ = clienteServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvEmails.setItems(obsListEmails);
        lvNrosTelefono.setItems(obsListNrosTelefono);

        configListenersListViews();
    }


    @Override
    public void receiveData(Cliente data) {
        if (data != null) {
            flagModifyMode = true;
            this.cliente = data;
            tfDNI.setText(data.getDni());
            tfNombre.setText(data.getNombre());
            tfApellido.setText(data.getApellido());
            obsListEmails.setAll(data.getContactosCliente().getEmailSet());
            obsListNrosTelefono.setAll(data.getContactosCliente().getNroTelefonoSet());
        }
    }

    @Override
    public Optional<Cliente> getResult() {
        // todo: cambiar el resto de los usos de Optional por esta forma
        return Optional.ofNullable(this.cliente);
    }

    @FXML
    private void addEmail() {
        String eMail;
        try {
            eMail = ManejadorInputs.eMail(tfEmail.getText().strip(), true);
        } catch (RuntimeException e) {
            Alertas.aviso("Agregar eMail", e.getMessage());
            return;
        }
        if (obsListEmails.contains(eMail)) {
            tfEmail.setText("");
            return;
        }

        obsListEmails.add(eMail);
        tfEmail.setText("");
    }

    @FXML
    private void addNro() {
        String nro;
        try {
            nro = ManejadorInputs.nroTel(tfNro.getText().strip(), true);
        } catch (RuntimeException e) {
            Alertas.aviso("Agregar número de teléfono.", e.getMessage());
            return;
        }
        if (obsListNrosTelefono.contains(nro)) {
            tfNro.setText("");
            return;
        }

        obsListNrosTelefono.add(nro);
        tfNro.setText("");
    }

    @FXML
    private void guardar(ActionEvent event) {
        Cliente clienteParaCargar;
        String dni;
        String nombre;
        String apellido;
        try {
            dni = ManejadorInputs.dni(tfDNI.getText().strip(), true);
            nombre = ManejadorInputs.textoGenerico(tfNombre.getText().strip(), true,
                    2, 40);
            apellido = ManejadorInputs.textoGenerico(tfApellido.getText().strip(), true,
                    2, 40);
        } catch (RuntimeException e) {
            Alertas.aviso("Guardar cliente", e.getMessage());
            return;
        }
        if (obsListEmails.isEmpty() && obsListNrosTelefono.isEmpty()) {
            Alertas.aviso("Datos de contacto", "Debe haber al menos un dato de contacto.");
            return;
        }

        if (!Alertas.confirmacion("Guardar cliente", "¿Confirmar guardado de cliente?")) return;
        if (!flagModifyMode) {
            DatosContacto datosContacto = new DatosContacto();
            datosContacto.setId(null);
            datosContacto.getEmailSet().addAll(obsListEmails);
            datosContacto.getNroTelefonoSet().addAll(obsListNrosTelefono);
            clienteParaCargar = new Cliente(null, dni, nombre, apellido, datosContacto, new ArrayList<>());
        } else {
            DatosContacto datosContacto = this.cliente.getContactosCliente();
            clienteParaCargar = new Cliente(this.cliente.getId(), dni, nombre, apellido,
                    datosContacto, new ArrayList<>());
        }

        try {
            if (!flagModifyMode) {
                clienteParaCargar = clienteServ.saveClient(clienteParaCargar);
            } else {
                try {
                    clienteParaCargar = clienteServ.editClient(clienteParaCargar);
                } catch (PersistenceException e) {
                    if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                            e.getCause() instanceof org.postgresql.util.PSQLException) {
                        throw new DuplicateClientDNI("Ya existe un cliente con el DNI: " + clienteParaCargar.getDni()
                                + " en el sistema.");
                    } else {
                        throw e;
                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException("Error inesperado al guardar el cliente.", e);
                }
            }
            Alertas.exito("Guardar cliente", "Se han guardado los datos del cliente con éxito.");
            cancelar(event);
        } catch (DuplicateClientDNI e) {
            Alertas.aviso("Guardar cliente", e.getMessage());
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            Alertas.aviso("Guardar cliente", e.getMessage());
            return;
        }
        this.cliente = clienteParaCargar;
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    private void configListenersListViews() {
        lvEmails.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu cm = new ContextMenu();
            MenuItem menuItem = new MenuItem("Quitar eMail");

            menuItem.setOnAction(event -> {
                obsListEmails.remove(cell.getItem());
            });
            cm.getItems().add(menuItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                if (isEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(cm);
                }
            });
            return cell;
        });

        lvNrosTelefono.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu cm = new ContextMenu();
            MenuItem menuItem = new MenuItem("Quitar n° de teléfono");

            menuItem.setOnAction(event -> {
                obsListNrosTelefono.remove(cell.getItem());
            });
            cm.getItems().add(menuItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                if (isEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(cm);
                }
            });
            return cell;
        });
    }
}
