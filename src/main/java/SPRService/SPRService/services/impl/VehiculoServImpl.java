package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.MarcaVehiculoDAO;
import SPRService.SPRService.DAOs.ModeloVehiculoDAO;
import SPRService.SPRService.DAOs.VehiculoDAO;
import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.DTOs.filtros.FiltroVehiculoDTO;
import SPRService.SPRService.entities.MarcaVehiculo;
import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.exceptions.DuplicateVehicleException;
import SPRService.SPRService.services.ModeloVehiculoServ;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Singleton
public class VehiculoServImpl implements VehiculoServ {

    private final VehiculoDAO daoVehiculo;
    private final MarcaVehiculoDAO daoMarca;
    private final ModeloVehiculoDAO daoModelo;
    private final ModeloVehiculoServ modeloVehiculoServ;

    @Inject
    public VehiculoServImpl(VehiculoDAO daoVehiculo, MarcaVehiculoDAO daoMarca,
                            ModeloVehiculoDAO daoModelo, ModeloVehiculoServ modeloVehiculoServ) {
        this.daoVehiculo = daoVehiculo;
        this.daoMarca = daoMarca;
        this.daoModelo = daoModelo;
        this.modeloVehiculoServ = modeloVehiculoServ;
    }

    @Transactional
    public List<MarcaVehiculo> getAllBrands() {
        return daoMarca.getAllAlphabetically();
    }

    @Transactional
    @Override
    public List<Vehiculo> verTodosActivos() {
        return daoVehiculo.verTodos();
    }

    @Transactional
    @Override
    public Optional<Vehiculo> verDetalle(Long id) {
        return daoVehiculo.verDetalle(id);
    }

    @Transactional
    @Override
    public List<Vehiculo> buscarPor(String patente, String modelo, String marca, boolean activos, boolean baja) {
        if (patente == null) patente = "";
        if (modelo == null) modelo = "";
        if (marca == null) marca = "";

        return daoVehiculo.buscarPor(patente, modelo, marca, activos, baja);
    }

    @Transactional
    @Override
    public ResultadoPaginado<Vehiculo> buscarPaginado(String patente, String modelo, String marca,
                                                      boolean activos, boolean baja, int pagina, int itemsPorPagina) {
        if (patente == null) patente = "";
        if (modelo == null) modelo = "";
        if (marca == null) marca = "";

        int offset = pagina * itemsPorPagina;
        FiltroVehiculoDTO filtro = new FiltroVehiculoDTO(patente, modelo, marca, activos, baja, offset, itemsPorPagina);
        return daoVehiculo.buscarPaginado(filtro);
    }

    @Transactional
    @Override
    public List<ModelosMasRegistradosDTO> generarReporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                              LocalDate fechaMax) {
        if (fechaMin == null) fechaMin = LocalDate.now().minusYears(20L);
        if (fechaMax == null) fechaMax = LocalDate.now();
        if (cantidad < 0 || cantidad > 20) cantidad = 1;
        return daoVehiculo.reporteModelosMasRegistrados(cantidad, fechaMin.atStartOfDay(), fechaMax.atTime(LocalTime.MAX));
    }

    @Transactional
    @Override
    public Vehiculo cargarVehiculo(Vehiculo vehiculoDTO) {
        ModeloVehiculo managedModelo;
        Optional<ModeloVehiculo> result = modeloVehiculoServ.verVehiculosDeModelo(vehiculoDTO.getModeloVehiculo().getId());
        if (result.isPresent()) {
            managedModelo = result.get();
            vehiculoDTO.setPatente(vehiculoDTO.getPatente().toUpperCase(Locale.ROOT));
            vehiculoDTO.asociarModelo(managedModelo);
            try {
                daoVehiculo.save(vehiculoDTO);
            } catch (PersistenceException e) {
                if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ||
                        e.getCause() instanceof org.postgresql.util.PSQLException) {
                    throw new DuplicateVehicleException("Ya existe un vehículo con la patente: " +
                            vehiculoDTO.getPatente() + " en el sistema.");
                }
                throw e;
            }
        }
        return vehiculoDTO;
    }

    @Transactional
    @Override
    public Vehiculo modificarVehiculo(Vehiculo veh) {
        if (veh == null) throw new NullPointerException("vehiculo nulo en servicio.");

        Vehiculo managedVeh = daoVehiculo.getById(veh.getId());
        ModeloVehiculo managedMod = daoModelo.getById(veh.getModeloVehiculo().getId());
        if (managedVeh == null) throw new EntityNotFoundException("No existe ese vehículo.");
        if (managedMod == null) throw new EntityNotFoundException("No existe ese modelo.");

        managedVeh.setPatente(veh.getPatente());
        managedVeh.setNroChasis(veh.getNroChasis());
        managedVeh.setNroMotor(veh.getNroMotor());
        managedVeh.setColor(veh.getColor());

        Hibernate.initialize(managedMod.getVehiculos());
        Hibernate.initialize(managedMod.getMarcaVehiculo());
        Hibernate.initialize(managedVeh.getModeloVehiculo());

        managedVeh.asociarModelo(managedMod);
        return managedVeh;
    }

    @Transactional
    @Override
    public void darDeBaja(Vehiculo v) {
        v.setEstado(Boolean.FALSE);
        daoVehiculo.update(v);
    }

    @Transactional
    @Override
    public void reactivar(Vehiculo v) {
        v.setEstado(Boolean.TRUE);
        daoVehiculo.update(v);
    }
}