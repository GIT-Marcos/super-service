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

    @FXML
    private void guardarUsuario(ActionEvent event) {
        String nombre = tfNombre.getText().strip();
        String contraseniaNueva = tfContrasenia.getText();
        String inputContraseniaOriginal = tfContraseniaOriginal.getText();
        String correo = tfCorreo.getText().strip();
        RolUsuario rol = comboRoles.getSelectionModel().getSelectedItem();

        try {
            ManejadorInputs.textoGenerico(nombre, true, "Nombre de usuario", 20);
            ManejadorInputs.eMail(correo, true);
            ManejadorInputs.contrasenia(contraseniaNueva);
            ManejadorInputs.contrasenia(inputContraseniaOriginal);
            if (!SimpleDialogs.confirmacion("Guardar usuario", "¿Está seguro que desea guardar el usuario?"))
                return;
            viewModelUsuarioModificar.getUsuario().setNombre(nombre);
            viewModelUsuarioModificar.getUsuario().setCorreo(correo);
            viewModelUsuarioModificar.getUsuario().setPassword(contraseniaNueva);
            viewModelUsuarioModificar.getUsuario().setRol(rol);

            Usuario guardado = usuarioServ.modificarUsuario(viewModelUsuarioModificar.getUsuario(), inputContraseniaOriginal);
            viewModelUsuarioParaDevolver = new UsuarioViewModelTabla(guardado);

            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
            NotificationHelper.mostrarExito("Guardar usuario",
                    "Se ha guardado el usuario '" + nombre + "' con éxito.");
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Guardar usuario", e.getMessage());
        } catch (Exception e) {
            NotificationHelper.mostrarError("Guardar usuario", e.getMessage());
            e.printStackTrace();
        }
    }
}
