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
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarClienteController implements Initializable, DataReceiver<Cliente>, ModalController<Cliente> {

    private Cliente cliente;
    private Cliente clienteParaDevolver;
    private final ClienteServ clienteServ;
    private boolean flagModifyMode = false;
    private ObservableList<ItemDatoContactoViewModel> items = FXCollections.observableArrayList();

    @FXML
    private TextField tfDNI, tfNombre, tfApellido, tfDatoContacto;
    @FXML
    private Button btnGuardar, btnReActivar;
    @FXML
    private ComboBox<ItemDatoContactoViewModel.TipoContacto> cbTipoContacto;
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

        cbTipoContacto.getItems().addAll(ItemDatoContactoViewModel.TipoContacto.values());
        cbTipoContacto.getSelectionModel().selectFirst();

        cbTipoContacto.setOnAction(e -> actualizarPlaceholder());
        actualizarPlaceholder();
    }

    private void actualizarPlaceholder() {
        ItemDatoContactoViewModel.TipoContacto tipo = cbTipoContacto.getValue();
        switch (tipo) {
            case EMAIL -> tfDatoContacto.setPromptText("email@ejemplo.com");
            case TELEFONO -> tfDatoContacto.setPromptText("+5491123456789");
        }
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
        ItemDatoContactoViewModel.TipoContacto tipo = cbTipoContacto.getSelectionModel().getSelectedItem();
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
        if (items.isEmpty()) {
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
        ItemDatoContactoViewModel.updateEntity(contactos, items);

        // 5. Construir Cliente
        Cliente clienteParaCargar = new Cliente(
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

    private void crearNuevo() {

    }



    @FXML
    private void cancelar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }
}
