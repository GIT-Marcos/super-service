package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.ModeloVehiculoDAO;
import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.services.ModeloVehiculoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.List;

@Singleton
public class ModeloVehiculoServImpl implements ModeloVehiculoServ {

    private final ModeloVehiculoDAO dao;

    @Inject
    public ModeloVehiculoServImpl(ModeloVehiculoDAO dao) {
        this.dao = dao;
    }

    //todo: nose usa esto, las trae con el fetch.EAGER por la marca
    @Transactional
    @Override
    public List<ModeloVehiculo> verTodos() {
        return dao.getAllModels();
    }

    @Transactional
    @Override
    public void cargarModelo(ModeloVehiculo m) {
        System.out.println("NO IMPLEMENTADO");
    }

    @Transactional
    @Override
    public ModeloVehiculo modificarModelo(ModeloVehiculo m) {
        return dao.update(m);
    }
}
