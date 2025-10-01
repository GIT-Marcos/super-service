package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.NotaRetiroDAO;
import SPRService.SPRService.entities.NotaRetiro;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

@Singleton
public class NotaRetiroDAOImpl extends GenericDAOImpl<NotaRetiro, Long> implements NotaRetiroDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public NotaRetiroDAOImpl() {
        super(NotaRetiro.class);
    }

    @Override
    public List<NotaRetiro> verTodasPorFecha() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT n FROM NotaRetiro n " +
                        "WHERE n.activo = TRUE " +
                        "ORDER BY n.fecha DESC",
                NotaRetiro.class).getResultList();
    }

    @Override
    public List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT n FROM NotaRetiro n " +
                                "WHERE n.fecha BETWEEN :fechaMin AND :fechaMax",
                        NotaRetiro.class)
                .setParameter("fechaMin", fechaMin)
                .setParameter("fechaMax", fechaMax)
                .getResultList();
    }
}
