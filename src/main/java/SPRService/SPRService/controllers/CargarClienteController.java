package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaDatoContacto;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.DatosContacto;
import SPRService.SPRService.exceptions.DuplicateClientDNI;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.celdas.ItemDatoContactoViewModel;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarClienteController implements Initializable, DataReceiver<Cliente>, ModalController<Cliente> {

    private Cliente cliente;
    private Cliente clienteParaDevolver;
    private final ClienteServ clienteServ;
    private boolean flagModifyMode = false;
    private ObservableList<ItemDatoContactoViewModel> items = FXCollections.observableArrayList();
    private ToggleGroup tgTipoContacto;

    @FXML
    private TextField tfDNI, tfNombre, tfApellido, tfDatoContacto;
    @FXML
    private Button btnGuardar, btnReActivar;
    @FXML
    private RadioButton rbTelefono, rbEmail;
    @FXML
    private ListView<ItemDatoContactoViewModel> lvDatosContacto;

    @Inject
    public CargarClienteController(ClienteServ clienteServ) {
        this.clienteServ = clienteServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvDatosContacto.setItems(items);
        lvDatosContacto.setCellFactory(cell -> new CeldaDatoContacto());

        // Crear y configurar el ToggleGroup programáticamente
        tgTipoContacto = new ToggleGroup();
        rbTelefono.setToggleGroup(tgTipoContacto);
        rbEmail.setToggleGroup(tgTipoContacto);

        // Listener para cambio de selección en RadioButtons
        tgTipoContacto.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actualizarPlaceholder();
            }
        });

        // Placeholder inicial (Teléfono seleccionado por defecto)
        actualizarPlaceholder();
    }

    private void actualizarPlaceholder() {
        ItemDatoContactoViewModel.TipoContacto tipo = getTipoContactoSeleccionado();
        if (tipo == null) return;

        switch (tipo) {
            case EMAIL -> tfDatoContacto.setPromptText("email@ejemplo.com");
            case TELEFONO -> tfDatoContacto.setPromptText("+5491123456789");
        }
    }

    /**
     * Obtiene el tipo de contacto seleccionado basándose en el RadioButton activo
     */
    private ItemDatoContactoViewModel.TipoContacto getTipoContactoSeleccionado() {
        if (rbTelefono.isSelected()) {
            return ItemDatoContactoViewModel.TipoContacto.TELEFONO;
        } else if (rbEmail.isSelected()) {
            return ItemDatoContactoViewModel.TipoContacto.EMAIL;
        }
        return null;
    }

    @Override
    public void receiveData(Cliente data) {
        if (data != null) {
            flagModifyMode = true;
            this.cliente = data;
            tfDNI.setText(data.getDni());
            tfNombre.setText(data.getNombre());
            tfApellido.setText(data.getApellido());

            items.setAll(ItemDatoContactoViewModel.fromEntity(data.getContactosCliente()));

            if (!data.getActivo()) {
                btnGuardar.setDisable(true);
                btnReActivar.setDisable(false);
            }
        }
    }

    @Override
    public Optional<Cliente> getResult() {
        return Optional.ofNullable(this.clienteParaDevolver);
    }

    @FXML
    private void addDatoContacto() {
        ItemDatoContactoViewModel.TipoContacto tipo = getTipoContactoSeleccionado();
        if (tipo == null) {
            return;
        }

        try {
            String datoNuevo;
            switch (tipo) {
                case EMAIL -> datoNuevo = ManejadorInputs.eMail(tfDatoContacto.getText(), true);
                case TELEFONO -> datoNuevo = ManejadorInputs.nroTel(tfDatoContacto.getText(), true);
                default -> {
                    return;
                }
            }

            ItemDatoContactoViewModel nuevoContacto = new ItemDatoContactoViewModel(tipo, datoNuevo);
            if (items.contains(nuevoContacto)) {
                NotificationHelper.mostrarAdvertencia("Agregar contacto", "Ese contacto ya existe.");
                return;
            }

            items.add(nuevoContacto);
            tfDatoContacto.clear();
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Agregar contacto", e.getMessage());
        }
    }

    @FXML
    private void reActivar() {
        if (!SimpleDialogs.confirmacion("Guardar cliente",
                "¿Está seguro que quiere re-activar el cliente?")) return;
        try {
            clienteServ.reActivar(this.cliente);
            this.cliente.setActivo(true);

            btnGuardar.setDisable(false);
            btnReActivar.setDisable(true);

            this.clienteParaDevolver = this.cliente;
            NotificationHelper.mostrarExito("Re-activar cliente",
                    "Se ha re-activado el cliente con éxito.");
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Re-activar cliente", "Ha ocurrido un error inesperado.");
            e.printStackTrace();
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        String dni, nombre, apellido;
        try {
            dni = ManejadorInputs.dni(tfDNI.getText(), true);
            nombre = ManejadorInputs.textoGenerico(tfNombre.getText(), true, "Nombre", 40);
            apellido = ManejadorInputs.textoGenerico(tfApellido.getText(), true, "Apellido", 40);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Guardar cliente", e.getMessage());
            return;
        }

        if (items.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Datos de contacto", "Debe haber al menos un dato de contacto.");
            return;
        }

        if (!SimpleDialogs.confirmacion("Guardar cliente", "¿Confirmar guardado de cliente?")) return;

        DatosContacto contactos = new DatosContacto();
        if (flagModifyMode && cliente.getContactosCliente() != null) {
            contactos.setId(cliente.getContactosCliente().getId());
        }
        ItemDatoContactoViewModel.updateEntity(contactos, items);

        Cliente clienteParaCargar = new Cliente(dni, nombre, apellido, contactos);
        clienteParaCargar.setId(flagModifyMode ? cliente.getId() : null);

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
}