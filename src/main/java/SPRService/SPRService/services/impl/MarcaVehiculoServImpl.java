package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.MarcaVehiculoDAO;
import SPRService.SPRService.entities.MarcaVehiculo;
import SPRService.SPRService.services.MarcaVehiculoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.List;

@Singleton
public class MarcaVehiculoServImpl implements MarcaVehiculoServ {

    private final MarcaVehiculoDAO dao;

    @Inject
    public MarcaVehiculoServImpl(MarcaVehiculoDAO dao) {
        this.dao = dao;
    }

    @Transactional
    @Override
    public List<MarcaVehiculo> verTodas() {
        return dao.getAllAlphabetically();
    }

    @Transactional
    @Override
    public void cargarMarca(MarcaVehiculo m) {
        System.out.println("NO IMPLEMENTADO TODAVÍA");
    }
}
