package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.DatosContacto;
import SPRService.SPRService.exceptions.DuplicateClientDNI;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarClienteController implements Initializable, DataReceiver<Cliente>, ModalController<Cliente> {

    private Cliente cliente;
    private Cliente clienteParaDevolver;
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
        return Optional.ofNullable(this.clienteParaDevolver);
    }

    @FXML
    private void addEmail() {
        String eMail;
        try {
            eMail = ManejadorInputs.eMail(tfEmail.getText(), true);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarAdvertencia("Agregar eMail", e.getMessage());
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
            nro = ManejadorInputs.nroTel(tfNro.getText(), true);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarAdvertencia("Agregar número de teléfono.", e.getMessage());
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
        // 1. Validar campos de texto
        String dni, nombre, apellido;
        try {
            dni = ManejadorInputs.dni(tfDNI.getText(), true);
            nombre = ManejadorInputs.textoGenerico(tfNombre.getText(), true, "Nombre", 40);
            apellido = ManejadorInputs.textoGenerico(tfApellido.getText(), true, "Apellido", 40);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Guardar cliente", e.getMessage());
            return;
        }

        // 2. Validar contactos
        if (obsListEmails.isEmpty() && obsListNrosTelefono.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Datos de contacto", "Debe haber al menos un dato de contacto.");
            return;
        }

        // 3. Confirmación de guardado
        if (!SimpleDialogs.confirmacion("Guardar cliente", "¿Confirmar guardado de cliente?")) return;

        // 4. Construir DatosContacto
        DatosContacto contactos = new DatosContacto();
        if (flagModifyMode && cliente.getContactosCliente() != null) {
            contactos.setId(cliente.getContactosCliente().getId()); // ⚠ Reusar ID existente
        }
        contactos.setEmailSet(new HashSet<>(obsListEmails));
        contactos.setNroTelefonoSet(new HashSet<>(obsListNrosTelefono));

        // 5. Construir Cliente
        Cliente clienteParaCargar = new Cliente(
                //flagModifyMode ? cliente.getId() : null,
                dni,
                nombre,
                apellido,
                contactos
        );
        clienteParaCargar.setId(flagModifyMode ? cliente.getId() : null);

        // 6. Guardar o editar
        try {
            if (!flagModifyMode) {
                this.clienteParaDevolver = clienteServ.saveClient(clienteParaCargar);
            } else {
                this.clienteParaDevolver = clienteServ.editClient(clienteParaCargar).orElse(null);
            }

            NotificationHelper.mostrarExito("Guardar cliente", "Se han guardado los datos del cliente con éxito.");
            cancelar(event);
        } catch (DuplicateClientDNI e) {
            NotificationHelper.mostrarAdvertencia("Guardar cliente", e.getMessage());
        } catch (PersistenceException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                    e.getCause() instanceof org.postgresql.util.PSQLException) {
                NotificationHelper.mostrarAdvertencia("Guardar cliente", "Ya existe un cliente con el DNI: " + dni);
            } else {
                NotificationHelper.mostrarError("Guardar cliente", e.getMessage());
                e.printStackTrace();
            }

        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Guardar cliente", e.getMessage());
            e.printStackTrace();
        }
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
