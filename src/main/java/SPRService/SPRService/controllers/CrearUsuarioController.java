package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.UsuarioServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.tablas.UsuarioViewModelTabla;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.util.ManejadorInputs;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CrearUsuarioController implements Initializable, ModalController<UsuarioViewModelTabla> {

    private final UsuarioServ usuarioServ;
    private UsuarioViewModelTabla viewModelUsuarioCreado;

    @FXML
    private TextField tfNombre, tfContrasenia, tfCorreo;
    @FXML
    private ComboBox<RolUsuario> comboRoles;

    @Inject
    public CrearUsuarioController(UsuarioServ usuarioServ) {
        this.usuarioServ = usuarioServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarComboRoles();
    }

    @Override
    public Optional<UsuarioViewModelTabla> getResult() {
        return Optional.ofNullable(viewModelUsuarioCreado);
    }

    // ================= ACCIÓN PRINCIPAL =================

    @FXML
    private void cargarUsuario() {
        Optional<UsuarioFormData> datosValidados = validarFormulario();
        if (datosValidados.isEmpty()) return;

        UsuarioFormData data = datosValidados.get();

        if (!SimpleDialogs.confirmacion("Crear usuario",
                "¿Está seguro que desea crear un nuevo usuario?")) {
            return;
        }

        persistirUsuario(data);
    }

    // ================= VALIDACIÓN =================

    private Optional<UsuarioFormData> validarFormulario() {
        UsuarioFormData data = new UsuarioFormData();
        StringBuilder errores = new StringBuilder("Por favor corrija los siguientes errores:\n");
        boolean hayErrores = false;

        // Validar nombre
        try {
            data.nombre = ManejadorInputs.nombreUsuario(tfNombre.getText());
            marcarCampoError(tfNombre, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfNombre, true);
            errores.append("- Nombre: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        // Validar correo
        try {
            data.correo = ManejadorInputs.eMail(tfCorreo.getText().strip(), true);
            marcarCampoError(tfCorreo, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfCorreo, true);
            errores.append("- Correo: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        // Validar contraseña
        try {
            data.contrasenia = ManejadorInputs.contrasenia(tfContrasenia.getText());
            marcarCampoError(tfContrasenia, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfContrasenia, true);
            errores.append("- Contraseña: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        // Validar rol
        RolUsuario rolSeleccionado = comboRoles.getSelectionModel().getSelectedItem();
        if (rolSeleccionado == null) {
            marcarCampoError(comboRoles, true);
            errores.append("- Rol: Debe seleccionar un rol.\n");
            hayErrores = true;
        } else {
            data.rol = rolSeleccionado;
            marcarCampoError(comboRoles, false);
        }

        if (hayErrores) {
            NotificationHelper.mostrarError("Error de Validación", errores.toString());
            return Optional.empty();
        }

        return Optional.of(data);
    }

    // ================= PERSISTENCIA =================

    private void persistirUsuario(UsuarioFormData data) {
        try {
            Usuario usuario = new Usuario(null, data.nombre, data.correo,
                    data.contrasenia, data.rol);
            Usuario creado = usuarioServ.cargarUsuario(usuario);
            viewModelUsuarioCreado = new UsuarioViewModelTabla(creado);

            limpiarCampos();
            NotificationHelper.mostrarExito("Crear usuario",
                    "Se ha creado el usuario " + data.nombre + " con éxito.");

        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Crear usuario", e.getMessage());
        } catch (Exception e) {
            NotificationHelper.mostrarError("Error de Persistencia", "Ha ocurrido un error inesperado.");
            e.printStackTrace();
        }
    }

    // ================= UI =================

    private void marcarCampoError(Node node, boolean esError) {
        if (esError) {
            if (!node.getStyleClass().contains("error-border")) {
                node.getStyleClass().add("error-border");
            }
        } else {
            node.getStyleClass().remove("error-border");
        }
    }

    private void limpiarCampos() {
        tfNombre.clear();
        tfCorreo.clear();
        tfContrasenia.clear();
        comboRoles.getSelectionModel().select(RolUsuario.GERENCIAL);

        // Limpiar estilos de error
        marcarCampoError(tfNombre, false);
        marcarCampoError(tfCorreo, false);
        marcarCampoError(tfContrasenia, false);
        marcarCampoError(comboRoles, false);
    }

    private void llenarComboRoles() {
        ObservableList<RolUsuario> datosLista = FXCollections.observableArrayList(RolUsuario.values());
        comboRoles.setItems(datosLista);
        comboRoles.getSelectionModel().select(RolUsuario.GERENCIAL);
    }

    // ================= DTO INTERNO =================

    private static class UsuarioFormData {
        String nombre;
        String correo;
        String contrasenia;
        RolUsuario rol;
    }
}