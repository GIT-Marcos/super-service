package SPRService.SPRService.DTOs.filtros;

import SPRService.SPRService.enums.RolUsuario;

import java.util.Set;

public record FiltroUsuarioDTO(
        String nombre,
        String correo,
        Set<RolUsuario> roles,
        boolean verActivos,
        boolean verInactivos
) {
}
