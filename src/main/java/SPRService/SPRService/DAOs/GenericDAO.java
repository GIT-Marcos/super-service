package SPRService.SPRService.DAOs;

import org.hibernate.Session;

import java.util.List;

/**
 * Operaciones básicas que usan DAOs simples que no requieren complejidad en las consultas a bd y son repetitivas.
 * @param <T> tipo de entidad.
 */
public interface GenericDAO<T, ID> {

    List<T> getAll();

    T getById(ID id);

    void save(T t);

    T update(T t);

    void update(List<T> entities);

    void delete(T t);

}
