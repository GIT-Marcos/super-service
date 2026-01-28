package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.PagoDAO;
import SPRService.SPRService.entities.Pago;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;

import java.util.UUID;

@Singleton
public class PagoDAOImpl extends GenericDAOImpl<Pago, UUID> implements PagoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public PagoDAOImpl() {
        super(Pago.class);
    }

}
