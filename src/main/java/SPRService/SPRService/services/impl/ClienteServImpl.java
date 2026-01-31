package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.ClienteDAO;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.DatosContacto;
import SPRService.SPRService.exceptions.DuplicateClientDNI;
import SPRService.SPRService.services.ClienteServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;

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
    public List<Cliente> filteredSearch(String dni, String lastName, String firstName) {
        if (dni == null) dni = "";
        if (lastName == null) lastName = "";
        if (firstName == null) firstName = "";
        return dao.filteredSearch(dni, lastName, firstName);
    }

    @Transactional
    @Override
    public Cliente saveClient(Cliente c) {
        if (c == null) throw new IllegalArgumentException("Cliente nulo en servicio.");
        poneMayus(c);

        try {
            dao.save(c);
            //todo: reemplazar los catch similares a este por excepciones de más bajo nivel para no acoplar
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            throw new DuplicateClientDNI("Ya existe un cliente con el DNI: " + c.getDni() + " en el sistema.");
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al guardar el cliente.", e);
        }
        return c;
    }

    //todo: reemplazar todas las ediciones de entidades por este formato usando escritura manual de datos y dejando que
    // Hibernate edite al terminar la transacción.
    @Transactional
    @Override
    public Optional<Cliente> editClient(Cliente clienteDTO) {
        if (clienteDTO == null)
            throw new IllegalArgumentException("Cliente nulo en servicio.");

        // Cargar cliente existente (managed)
        Cliente existente = dao.getById(clienteDTO.getId());
        if (existente == null)
            throw new EntityNotFoundException("Cliente no encontrado");

        // Actualizar campos
        poneMayus(clienteDTO);
        existente.setDni(clienteDTO.getDni());
        existente.setNombre(clienteDTO.getNombre());
        existente.setApellido(clienteDTO.getApellido());

        // Actualizar contactos
        DatosContacto contactosExistentes = existente.getContactosCliente();
        DatosContacto contactosNuevos = clienteDTO.getContactosCliente();

        contactosExistentes.getEmailSet().clear();
        contactosExistentes.getEmailSet().addAll(contactosNuevos.getEmailSet());

        contactosExistentes.getNroTelefonoSet().clear();
        contactosExistentes.getNroTelefonoSet().addAll(contactosNuevos.getNroTelefonoSet());

        // Cargar relaciones lazy para devolver
        Hibernate.initialize(existente.getVehiculos());
        Hibernate.initialize(existente.getVentas());
        Hibernate.initialize(existente.getServices());

        return Optional.of(existente);
    }

    @Transactional
    @Override
    public void softDeleteClient(Cliente c) {
        if (c == null) throw new IllegalArgumentException("Cliente nulo en servicio.");
        Cliente managedClient = dao.getById(c.getId());
        managedClient.setDni(managedClient.getDni() + ".DEL" + managedClient.getId());
        managedClient.setActivo(Boolean.FALSE);
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
