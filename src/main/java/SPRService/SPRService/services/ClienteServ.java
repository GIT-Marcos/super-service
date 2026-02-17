package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.ClientesMasIngresosDTO;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClienteServ {

    List<Cliente> verTodosActivos();

    Optional<Cliente> verOperacionesConVehiculos(Long id);

    Optional<Cliente> verDatosContacto(Long id);

    ResultadoPaginado<Cliente> buscarPaginado(String dni, String apellido, String nombre,
                                              boolean activos, boolean baja, int pagina, int itemsPorPagina);

    Cliente saveClient(Cliente c);

    Optional<Cliente> editClient(Cliente clienteDTO);

    void reActivar(Cliente c);

    void softDeleteClient(Cliente c);

    //   ____  _____ ____   ___  ____ _____ _____ ____
    //  |  _ \| ____|  _ \ / _ \|  _ \_   _| ____/ ___|
    //  | |_) |  _| | |_) | | | | |_) || | |  _| \___ \
    //  |  _ <| |___|  __/| |_| |  _ < | | | |___ ___) |
    //  |_| \_\_____|_|    \___/|_| \_\|_| |_____|____/

    List<ClientesMasIngresosDTO> generarReporteClientesMasIngresos(
            Integer cantidad, LocalDate fechaMin, LocalDate fechaMax);
}
