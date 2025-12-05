package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.MarcaRepuestoDAO;
import SPRService.SPRService.entities.MarcaRepuesto;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;

import java.util.List;

@Singleton
public class MarcaRepuestoDAOImpl extends GenericDAOImpl<MarcaRepuesto, Long> implements MarcaRepuestoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public MarcaRepuestoDAOImpl() {
        super(MarcaRepuesto.class);
    }

    @Override
    public List<MarcaRepuesto> verTodas() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT m FROM MarcaRepuesto m " +
                        "LEFT JOIN FETCH m.repuestos ORDER BY m.nombreMarca ASC",
                MarcaRepuesto.class).getResultList();
    }

    @Override
    public List<MarcaRepuesto> validarUnicidadNombre(MarcaRepuesto m) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT m FROM MarcaRepuesto m " +
                                "WHERE m.nombreMarca = :nombre",
                        MarcaRepuesto.class)
                .setParameter("nombre", m.getNombreMarca())
                .setMaxResults(1)
                .getResultList();
    }
}
