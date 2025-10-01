package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.GenericDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;

public class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {

    //con protected las clases hijas pueden acceder.
    protected final Class<T> entityClass;
    @Inject
    private Provider<EntityManager> emProvider;

    public GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> getAll() {
        EntityManager em = emProvider.get();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityClass);
        criteria.from(entityClass);
        return em.createQuery(criteria).getResultList();
    }

    @Override
    public T getById(ID id) {
        EntityManager em = emProvider.get();
        return em.find(entityClass, id);
    }

    @Override
    public void save(T entity) {
        EntityManager em = emProvider.get();
        em.persist(entity);
    }

    @Override
    public T update(T entity) {
        EntityManager em = emProvider.get();
        return em.merge(entity);
    }

    @Override
    public void update(List<T> entities) {
        EntityManager em = emProvider.get();
        for (T e : entities) {
            em.merge(e);
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = emProvider.get();
        em.remove(entity);
    }
}
