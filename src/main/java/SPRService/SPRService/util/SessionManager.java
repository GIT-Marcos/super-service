package SPRService.SPRService.util;

import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.util.alertas.Alertas;

//TODO: ver si conviene manejar esto con inyección de dependencias.
public class SessionManager {

    private static Usuario usuarioSesion;

    public static void iniciarSesion(Usuario usuario) {
        usuarioSesion = usuario;
    }

    public static Usuario getUsuarioSesion() {
        return usuarioSesion;
    }

    public static boolean cerrarSesion() {
        if (SimpleDialogs.confirmacion("Cerrar sesión", "¿Está seguro que desea cerrar sesión?")) {
            usuarioSesion = null;
            return true;
        }
        return false;
    }

    public static boolean haySesionActiva() {
        return usuarioSesion != null;
    }
}
