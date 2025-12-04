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

//todo: bug no carga datos al modificar
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
        // 1. Validaciones de campos de texto
        String dni;
        String nombre;
        String apellido;
        try {
            dni = ManejadorInputs.dni(tfDNI.getText(), true);
            nombre = ManejadorInputs.textoGenerico(tfNombre.getText(), true, "Nombre", 40);
            apellido = ManejadorInputs.textoGenerico(tfApellido.getText(), true, "Apellido", 40);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Guardar cliente", e.getMessage());
            return;
        }

        // 2. Validación de listas de contacto
        if (obsListEmails.isEmpty() && obsListNrosTelefono.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Datos de contacto", "Debe haber al menos un dato de contacto.");
            return;
        }

        if (!SimpleDialogs.confirmacion("Guardar cliente", "¿Confirmar guardado de cliente?")) return;

        // 3. PREPARACIÓN DE DATOS CONTACTO (Aquí estaba el error)
        DatosContacto datosContacto;

        if (!flagModifyMode) {
            // MODO NUEVO: Creamos uno nuevo
            datosContacto = new DatosContacto();
            datosContacto.setId(null); // Aseguramos que sea null para que se cree
        } else {
            // MODO EDICIÓN: Usamos el existente
            datosContacto = this.cliente.getContactosCliente();
        }

        // --- PASO CRUCIAL: ACTUALIZAR EL CONTENIDO DEL OBJETO ---
        // Limpiamos los sets actuales y agregamos lo que hay en la vista
        datosContacto.getEmailSet().clear();
        datosContacto.getEmailSet().addAll(obsListEmails);

        datosContacto.getNroTelefonoSet().clear();
        datosContacto.getNroTelefonoSet().addAll(obsListNrosTelefono);
        // --------------------------------------------------------

        // 4. Construcción del objeto Cliente
        Cliente clienteParaCargar;
        if (!flagModifyMode) {
            clienteParaCargar = new Cliente(null, dni, nombre, apellido, datosContacto, new HashSet<>(), new HashSet<>());
        } else {
            // Preservamos el ID y las relaciones existentes (ventas, etc) si es necesario,
            // aunque aquí pasamos HashSets vacíos asumiendo que el Servicio hace un 'merge' o ignora esos campos.
            clienteParaCargar = new Cliente(this.cliente.getId(), dni, nombre, apellido,
                    datosContacto, this.cliente.getVehiculos(), this.cliente.getServices());
            // Nota: He cambiado 'new HashSet<>()' por los getters originales del cliente
            // para no perder referencias si tu servicio usa este objeto directamente.
        }

        // 5. Llamada al Servicio
        try {
            if (!flagModifyMode) {
                this.clienteParaDevolver = clienteServ.saveClient(clienteParaCargar);
            } else {
                try {
                    this.clienteParaDevolver = clienteServ.editClient(clienteParaCargar);
                } catch (PersistenceException e) {
                    if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                            e.getCause() instanceof org.postgresql.util.PSQLException) {
                        throw new DuplicateClientDNI("Ya existe un cliente con el DNI: " + clienteParaCargar.getDni()
                                + " en el sistema.");
                    } else {
                        throw e;
                    }
                }
            }
            NotificationHelper.mostrarExito("Guardar cliente", "Se han guardado los datos del cliente con éxito.");
            cancelar(event);
        } catch (DuplicateClientDNI e) {
            NotificationHelper.mostrarAdvertencia("Guardar cliente", e.getMessage());
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
