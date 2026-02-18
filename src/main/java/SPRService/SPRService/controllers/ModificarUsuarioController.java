package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.UsuarioServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.tablas.UsuarioViewModelTabla;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModificarUsuarioController implements Initializable, ModalController<UsuarioViewModelTabla>,
        DataReceiver<UsuarioViewModelTabla> {

    private final UsuarioServ usuarioServ;
    private UsuarioViewModelTabla viewModelUsuarioModificar;
    private UsuarioViewModelTabla viewModelUsuarioParaDevolver;

    @FXML
    private TextField tfNombre, tfContrasenia, tfCorreo, tfContraseniaOriginal;
    @FXML
    private ComboBox<RolUsuario> comboRoles;
    @FXML
    private Label lblInactivo;
    @FXML
    private Button btnGuardar;

    @Inject
    public ModificarUsuarioController(UsuarioServ usuarioServ) {
        this.usuarioServ = usuarioServ;
    }

    // ================= INIT =================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboRoles.getItems().setAll(RolUsuario.values());
    }

    @Override
    public Optional<UsuarioViewModelTabla> getResult() {
        return Optional.ofNullable(viewModelUsuarioParaDevolver);
    }

    @Override
    public void receiveData(UsuarioViewModelTabla data) {
        if (data != null) {
            this.viewModelUsuarioModificar = data;
            tfNombre.setText(data.getNombre());
            tfCorreo.setText(data.getCorreo());
            comboRoles.getSelectionModel().select(data.getUsuario().getRol());

            if (!data.getUsuario().getActivo()) {
                lblInactivo.setVisible(true);
                btnGuardar.setDisable(true);
            }
        }
    }

    // ================= ACCIÓN PRINCIPAL =================

    @FXML
    private void guardarUsuario(ActionEvent event) {
        Optional<UsuarioFormData> datosValidados = validarFormulario();
        if (datosValidados.isEmpty()) return;

        UsuarioFormData data = datosValidados.get();

        if (!SimpleDialogs.confirmacion("Guardar usuario",
                "¿Está seguro que desea guardar los cambios del usuario?")) {
            return;
        }

        persistirUsuario(data, event);
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

        // Validar contraseña nueva
        try {
            data.contraseniaNueva = ManejadorInputs.contrasenia(tfContrasenia.getText());
            marcarCampoError(tfContrasenia, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfContrasenia, true);
            errores.append("- Contraseña nueva: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        // Validar contraseña original
        try {
            data.contraseniaOriginal = ManejadorInputs.contrasenia(tfContraseniaOriginal.getText());
            marcarCampoError(tfContraseniaOriginal, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfContraseniaOriginal, true);
            errores.append("- Contraseña original: ").append(e.getMessage()).append("\n");
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

    private void persistirUsuario(UsuarioFormData data, ActionEvent event) {
        try {
            Usuario usuario = viewModelUsuarioModificar.getUsuario();
            usuario.setNombre(data.nombre);
            usuario.setCorreo(data.correo);
            usuario.setPassword(data.contraseniaNueva);
            usuario.setRol(data.rol);

            Usuario guardado = usuarioServ.modificarUsuario(usuario, data.contraseniaOriginal);
            viewModelUsuarioParaDevolver = new UsuarioViewModelTabla(guardado);

            NotificationHelper.mostrarExito("Guardar usuario",
                    "Se ha guardado el usuario '" + data.nombre + "' con éxito.");

            cerrarVentana(event);

        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Error de Validación", e.getMessage());
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

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // ================= DTO INTERNO =================

    private static class UsuarioFormData {
        String nombre;
        String correo;
        String contraseniaNueva;
        String contraseniaOriginal;
        RolUsuario rol;
    }
}