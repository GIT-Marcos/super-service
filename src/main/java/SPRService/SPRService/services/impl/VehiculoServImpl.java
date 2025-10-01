package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.MarcaVehiculoDAO;
import SPRService.SPRService.DAOs.ModeloVehiculoDAO;
import SPRService.SPRService.DAOs.VehiculoDAO;
import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.MarcaVehiculo;
import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.exceptions.DuplicateVehicleException;
import SPRService.SPRService.services.VehiculoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Singleton
public class VehiculoServImpl implements VehiculoServ {

    private final VehiculoDAO daoVehiculo;
    private final MarcaVehiculoDAO daoMarca;
    private final ModeloVehiculoDAO daoModelo;

    @Inject
    public VehiculoServImpl(VehiculoDAO daoVehiculo, MarcaVehiculoDAO daoMarca, ModeloVehiculoDAO daoModelo) {
        this.daoVehiculo = daoVehiculo;
        this.daoMarca = daoMarca;
        this.daoModelo = daoModelo;
    }

    @Transactional
    //todo: ver si esta va en la interfaz de serv
    public List<MarcaVehiculo> getAllBrands() {
        return daoMarca.getAllAlphabetically();
    }

    @Transactional
    @Override
    public List<Vehiculo> verTodosActivos() {
        return daoVehiculo.getAllActive();
    }

    @Transactional
    @Override
    public List<Vehiculo> buscarPor(String patente, String modelo, String marca) {
        if (patente == null) patente = "";
        if (modelo == null) modelo = "";
        if (marca == null) marca = "";

        return daoVehiculo.buscarPor(patente, modelo, marca);
    }

    @Transactional
    @Override
    public List<ModelosMasRegistradosDTO> generarReporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                              LocalDate fechaMax) {
        if (cantidad == null || cantidad < 0 || cantidad > 13) {
            cantidad = 5;
        }
        if (fechaMin == null) {
            fechaMin = LocalDate.now().minusYears(20L);
        }
        if (fechaMax == null) {
            fechaMax = LocalDate.now();
        }
        return daoVehiculo.reporteModelosMasRegistrados(cantidad, fechaMin, fechaMax);
    }

    @Transactional
    @Override
    public Vehiculo cargarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) throw new NullPointerException("vehiculo nulo en servicio.");
        vehiculo.setPatente(vehiculo.getPatente().toUpperCase(Locale.ROOT));
        try {
            ModeloVehiculo modeloDetached = vehiculo.getModeloVehiculo();
            ModeloVehiculo modeloGestionado = daoModelo.update(modeloDetached);
            vehiculo.setModeloVehiculo(modeloGestionado);
            daoVehiculo.save(vehiculo);
        } catch (PersistenceException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                    e.getCause() instanceof org.postgresql.util.PSQLException) {
                throw new DuplicateVehicleException("Ya existe un vehículo con la patente: " +
                        vehiculo.getPatente() + " en el sistema.");
            }
            throw e;
        }
        return vehiculo;
    }

    @Transactional
    @Override
    public Vehiculo modificarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) throw new NullPointerException("vehiculo nulo en servicio.");
        // La gestión de la excepción de hace 1 capa más arriba por el .merge().
        return daoVehiculo.update(vehiculo);
    }

    @Transactional
    @Override
    public void borradoLogico(Vehiculo v) {
        if (v == null) throw new NullPointerException("Error: vehículo nulo en servicio");
        v.setEstado(Boolean.FALSE);
        // todo: buscar que tan malo es esto a largo plazo, sobretodo en reportes históricos.
        //  En caso de querer "reactivar" un borrado por error, se tendrá que revertir esto
        //  considerando el vehículo ya activo. Parece que el usuario deberá escoger cual mantener activo.
        // Modificación de patente única para cada borrado. Para poder cargar vehículos nuevos
        // con la misma patente sin sobreescribir borrados y mantener integridad referencial.
        v.setPatente(v.getPatente().concat(".DEL" + v.getId()));
        daoVehiculo.update(v);
    }

//    @Transactional
//    private Boolean consultarEstado(Vehiculo v) {
//        List<Boolean> result = daoVehiculo.consultarEstado(v.getPatente());
//        if (result.isEmpty()) return null;
//        return result.getFirst();
//    }

//    @Transactional
//    private void reactivarVehiculo(Vehiculo v) {
//        v.setEstado(Boolean.TRUE);
//        Long id = daoVehiculo.consultarId(v.getPatente());
//        v.setId(id);
//        daoVehiculo.update(v);
//    }
}
