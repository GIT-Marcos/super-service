package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.ClienteDAO;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.exceptions.DuplicateClientDNI;
import SPRService.SPRService.services.ClienteServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.List;
import java.util.Locale;

@Singleton
public class ClienteServImpl implements ClienteServ {

    private final ClienteDAO dao;

    @Inject
    public ClienteServImpl(ClienteDAO dao) {
        this.dao = dao;
    }

    @Transactional
    @Override
    public List<Cliente> getAllActive() {
        return dao.getAllActive();
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

    @Transactional
    @Override
    public Cliente editClient(Cliente c) {
        if (c == null) throw new IllegalArgumentException("Cliente nulo en servicio.");
        poneMayus(c);

        return dao.update(c);
    }

    @Transactional
    @Override
    public void softDeleteClient(Cliente c) {
        if (c == null) throw new IllegalArgumentException("Cliente nulo en servicio.");
        c.setDni(c.getDni() + ".DEL" + c.getId());
        c.setActivo(Boolean.FALSE);
        dao.update(c);
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
