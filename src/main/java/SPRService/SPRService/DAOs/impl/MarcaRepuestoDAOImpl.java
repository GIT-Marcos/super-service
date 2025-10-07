package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.MarcaRepuestoDAO;
import SPRService.SPRService.entities.MarcaRepuesto;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class MarcaRepuestoDAOImpl extends GenericDAOImpl<MarcaRepuesto, Long> implements MarcaRepuestoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public MarcaRepuestoDAOImpl() {
        super(MarcaRepuesto.class);
    }

    @Override
    public Set<MarcaRepuesto> verTodas() {
        EntityManager em = emProvider.get();
        List<MarcaRepuesto> l = em.createQuery("SELECT DISTINCT m FROM MarcaRepuesto m " +
                        "LEFT JOIN FETCH m.repuestos ORDER BY m.nombreMarca ASC",
                MarcaRepuesto.class).getResultList();
        return new HashSet<>(l);
    }
}
