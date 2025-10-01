package SPRService.SPRService.controllers;

import SPRService.SPRService.services.UsuarioServ;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.enums.PrivilegioUsuario;
import SPRService.SPRService.exceptions.DuplicateUserException;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CrearUsuarioController implements Initializable {

    private final UsuarioServ usuarioServ;

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfContrasenia;
    @FXML
    private ComboBox<PrivilegioUsuario> comboRoles;

    @Inject
    public CrearUsuarioController(UsuarioServ usuarioServ) {
        this.usuarioServ = usuarioServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarComboRoles();
    }

    private void llenarComboRoles() {
        ObservableList<PrivilegioUsuario> datosLista = FXCollections.observableArrayList(PrivilegioUsuario.values());
        comboRoles.setItems(datosLista);
        comboRoles.getSelectionModel().select(PrivilegioUsuario.GERENCIAL);
    }

    @FXML
    private void cargarUsuario(ActionEvent event) {
        String nombre = tfNombre.getText().trim();
        String contrasenia = tfContrasenia.getText();
        PrivilegioUsuario privilegio = comboRoles.getSelectionModel().getSelectedItem();

        try {
            ManejadorInputs.textoGenerico(nombre, true, 4, 20);
            ManejadorInputs.contrasenia(contrasenia, true);

            boolean resultado = Alertas.confirmacion("Confirmación", "¿Está seguro que desea " +
                    "cargar el usuario " + nombre + "?");
            if (!resultado) {
                return;
            }
            Usuario usuario = new Usuario(null, nombre, contrasenia, privilegio);
            usuarioServ.cargarUsuario(usuario);
            Alertas.exito("Nuevo usuario", "Usuario " + nombre + " creado con éxito.");
            cancelar(event);
        } catch (IllegalArgumentException e) {
            Alertas.aviso("Datos incorrectos", e.getMessage());
            return;
        } catch (DuplicateUserException e) {
            Alertas.aviso("Nuevo usuario", e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            Alertas.error("Nuevo usuario", "Error inesperado.");
            return;
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

}
