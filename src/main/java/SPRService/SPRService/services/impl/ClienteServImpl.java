package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.ClienteDAO;
import SPRService.SPRService.DTOs.ClientesMasIngresosDTO;
import SPRService.SPRService.DTOs.filtros.FiltroClienteDTO;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.DatosContacto;
import SPRService.SPRService.exceptions.DuplicateClientDNI;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Singleton
public class ClienteServImpl implements ClienteServ {

    private final ClienteDAO dao;

    @Inject
    public ClienteServImpl(ClienteDAO dao) {
        this.dao = dao;
    }

    @Transactional
    @Override
    public List<Cliente> verTodosActivos() {
        return dao.verTodos();
    }

    @Transactional
    @Override
    public Optional<Cliente> verOperacionesConVehiculos(Long id) {
        return dao.verOperacionesConVehiculos(id);
    }

    @Transactional
    @Override
    public Optional<Cliente> verDatosContacto(Long id) {
        return dao.verDatosContacto(id);
    }

    @Transactional
    @Override
    public ResultadoPaginado<Cliente> buscarPaginado(String dni, String apellido, String nombre,
                                                     boolean activos, boolean baja,
                                                     int pagina, int itemsPorPagina) {
        if (dni == null) dni = "";
        if (apellido == null) apellido = "";
        if (nombre == null) nombre = "";

        int offset = pagina * itemsPorPagina;
        FiltroClienteDTO filtro = new FiltroClienteDTO(dni, apellido, nombre, activos, baja, offset, itemsPorPagina);
        return dao.buscarPaginado(filtro);
    }

    @Transactional
    @Override
    public Cliente saveClient(Cliente c) {
        if (c == null) throw new IllegalArgumentException("Cliente nulo en servicio.");
        poneMayus(c);

        try {
            dao.save(c);
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            throw new DuplicateClientDNI("Ya existe un cliente con el DNI: " + c.getDni() + " en el sistema.");
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al guardar el cliente.", e);
        }
        return c;
    }

    @Transactional
    @Override
    public Optional<Cliente> editClient(Cliente clienteDTO) {
        if (clienteDTO == null)
            throw new IllegalArgumentException("Cliente nulo en servicio.");

        Cliente existente = dao.getById(clienteDTO.getId());
        if (existente == null)
            throw new EntityNotFoundException("Cliente no encontrado");

        poneMayus(clienteDTO);
        existente.setDni(clienteDTO.getDni());
        existente.setNombre(clienteDTO.getNombre());
        existente.setApellido(clienteDTO.getApellido());

        DatosContacto contactosExistentes = existente.getContactosCliente();
        DatosContacto contactosNuevos = clienteDTO.getContactosCliente();

        contactosExistentes.getEmailSet().clear();
        contactosExistentes.getEmailSet().addAll(contactosNuevos.getEmailSet());

        contactosExistentes.getNroTelefonoSet().clear();
        contactosExistentes.getNroTelefonoSet().addAll(contactosNuevos.getNroTelefonoSet());

        Hibernate.initialize(existente.getVehiculos());
        Hibernate.initialize(existente.getVentas());
        Hibernate.initialize(existente.getServices());

        return Optional.of(existente);
    }

    @Transactional
    @Override
    public void reActivar(Cliente c) {
        c.setActivo(true);
        dao.update(c);
    }

    @Transactional
    @Override
    public void softDeleteClient(Cliente c) {
        Cliente managedClient = dao.getById(c.getId());
        managedClient.setActivo(Boolean.FALSE);
    }

    @Transactional
    @Override
    public List<ClientesMasIngresosDTO> generarReporteClientesMasIngresos(
            Integer cantidad, LocalDate fechaMin, LocalDate fechaMax) {
        return dao.reporteClientesMasIngresos(
                cantidad,
                fechaMin.atStartOfDay(),
                fechaMax.atTime(LocalTime.MAX)
        );
    }

    private void poneMayus(Cliente c) {
        String[] palabras = c.getNombre().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String p : palabras) {
            if (!p.isBlank()) {
                String primeraLetra = p.substring(0, 1).toUpperCase(Locale.ROOT);
                String resto = p.substring(1).toLowerCase(Locale.ROOT);
                builder.append(primeraLetra).append(resto).append(" ");
            }
        }
        c.setNombre(builder.toString().strip());
        builder = new StringBuilder();

        palabras = c.getApellido().split("\\s+");
        for (String p : palabras) {
            if (!p.isBlank()) {
                String primeraLetra = p.substring(0, 1).toUpperCase(Locale.ROOT);
                String resto = p.substring(1).toLowerCase(Locale.ROOT);
                builder.append(primeraLetra).append(resto).append(" ");
            }
        }
        c.setApellido(builder.toString().strip());
    }
}