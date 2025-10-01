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
        boolean confir = Alertas.confirmacion("Cerrar sesión",
                "¿Está seguro de que quiere cerrar sesión?");
        if (!confir) {
            return false;
        }
        usuarioSesion = null;
        return true;
    }

    public static boolean haySesionActiva() {
        return usuarioSesion != null;
    }
}
