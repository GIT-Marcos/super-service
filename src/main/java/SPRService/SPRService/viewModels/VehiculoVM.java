package SPRService.SPRService.viewModels;

import SPRService.SPRService.entities.MarcaVehiculo;
import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.services.impl.VehiculoServImpl;
import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class VehiculoVM {
    // Referencia al modelo original. Útil para saber si estamos creando o editando.
    private Vehiculo vehiculoModelo;
    private final VehiculoServImpl vehiculoServImpl;

    // --- Propiedades para los campos de la entidad Vehículo ---
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty patente = new SimpleStringProperty("");
    private final StringProperty nroChasis = new SimpleStringProperty("");
    private final StringProperty nroMotor = new SimpleStringProperty("");
    private final StringProperty color = new SimpleStringProperty("");

    // --- Propiedades para manejar las relaciones ---

    // Para el ListView de Marcas:
    private final ObservableList<MarcaVehiculo> marcasDisponibles = FXCollections.observableArrayList();
    private final ObjectProperty<MarcaVehiculo> marcaSeleccionada = new SimpleObjectProperty<>();

    // Para el ListView de Modelos:
    private final ObservableList<ModeloVehiculo> modelosDisponibles = FXCollections.observableArrayList();
    private final ObjectProperty<ModeloVehiculo> modeloSeleccionado = new SimpleObjectProperty<>();

    // --- Propiedades "derivadas" (read-only) para mostrar info del modelo/marca seleccionado ---
    private final StringProperty nombreModeloSeleccionado = new SimpleStringProperty();
    private final ObjectProperty<Year> anioModeloSeleccionado = new SimpleObjectProperty<>();
    private final ObjectProperty<Double> cilindradaModeloSeleccionado = new SimpleObjectProperty<>();

    @Inject
    public VehiculoVM(VehiculoServImpl vehiculoServImpl) {
        this.vehiculoServImpl = vehiculoServImpl;
        setupListeners();
    }

    /**
     * Configura los listeners para la lógica interna del ViewModel.
     * Por ejemplo, cuando se cambia la marca, se deben actualizar los modelos disponibles.
     */
    private void setupListeners() {
        // 1. Cuando el usuario selecciona una marca diferente...
        // ESTE LISTENER ES LA CLAVE. No importa si la selección vino de un ComboBox,
        // un ListView, o un botón. La lógica del ViewModel reacciona igual.
        marcaSeleccionada.addListener((obs, oldMarca, newMarca) -> {
            System.out.println("Marca seleccionada en ViewModel: " + (newMarca != null ? newMarca.getNombreMarca() : "ninguna"));

            // Limpiar la selección de modelo anterior
            modeloSeleccionado.set(null);
            modelosDisponibles.clear();

            if (newMarca != null) {
                modelosDisponibles.setAll(marcaSeleccionada.getValue().getModelosVehiculos());
            }
        });


        // 2. Cuando el usuario selecciona un modelo diferente...
        modeloSeleccionado.addListener((obs, oldModelo, newModelo) -> {
            // Actualizar las propiedades de solo lectura que la vista podría estar mostrando.
            if (newModelo != null) {
                nombreModeloSeleccionado.set(newModelo.getNombreModelo());
                anioModeloSeleccionado.set(newModelo.getAnio());
                cilindradaModeloSeleccionado.set(newModelo.getCilindrada());

                // Asegurarse de que la marca seleccionada coincida con la del modelo
                if (newModelo.getMarcaVehiculo() != null) {
                    marcaSeleccionada.set(newModelo.getMarcaVehiculo());
                }
            } else {
                // Limpiar si no hay modelo seleccionado
                nombreModeloSeleccionado.set("");
                anioModeloSeleccionado.set(null);
                cilindradaModeloSeleccionado.set(null);
            }
        });
    }

    /**
     * Carga los datos de una entidad Vehículo en el ViewModel.
     * Se usa al editar un vehículo existente.
     * @param vehiculo La entidad a cargar.
     */
    public void cargarDesdeEntidad(Vehiculo vehiculo) {
        this.vehiculoModelo = vehiculo;
        this.id.set(vehiculo.getId());
        this.patente.set(vehiculo.getPatente());
        this.nroChasis.set(vehiculo.getNroChasis());
        this.nroMotor.set(vehiculo.getNroMotor());
        this.color.set(vehiculo.getColor());

        if (vehiculo.getModeloVehiculo() != null) {
            // Esto activará los listeners para poblar las propiedades derivadas
            this.modeloSeleccionado.set(vehiculo.getModeloVehiculo());
        }
    }

    /**
     * Carga el ViewModel con los datos de una entidad Vehiculo existente.
     * ESTA ES LA VERSIÓN CORREGIDA.
     *
     * @param vehiculo La entidad a cargar.
     */
    public void loadFromEntity(Vehiculo vehiculo) {
        this.vehiculoModelo = vehiculo; // Guardamos la referencia para el modo edición

        // Poblar campos simples
        this.id.set(vehiculo.getId());
        this.patente.set(vehiculo.getPatente());
        this.nroChasis.set(vehiculo.getNroChasis());
        this.nroMotor.set(vehiculo.getNroMotor());
        this.color.set(vehiculo.getColor());

        // --- ¡AQUÍ ESTÁ LA LÓGICA CORREGIDA! ---
        if (vehiculo.getModeloVehiculo() != null && vehiculo.getModeloVehiculo().getMarcaVehiculo() != null) {

            // 1. Obtenemos la marca "desconectada" del vehículo que queremos editar.
            MarcaVehiculo marcaIncompleta = vehiculo.getModeloVehiculo().getMarcaVehiculo();

            // 2. Buscamos en nuestra lista de marcas "completas" (cargadas con JOIN FETCH)
            //    una que sea igual a la incompleta. Usamos el ID para una comparación fiable.
            Optional<MarcaVehiculo> marcaCompletaOpt = marcasDisponibles.stream()
                    .filter(m -> m.getId().equals(marcaIncompleta.getId()))
                    .findFirst();

            // 3. Si la encontramos, la usamos para establecer la selección.
            //    Esta instancia SÍ tiene su colección de modelosVehiculos inicializada.
            //    Al hacer .set(), el listener se disparará correctamente y sin excepciones.
            if (marcaCompletaOpt.isPresent()) {
                this.marcaSeleccionada.set(marcaCompletaOpt.get());

                // 4. Una vez que la marca está correctamente establecida (y por tanto la lista de modelos
                //    se ha poblado gracias al listener), AHORA podemos seleccionar el modelo.
                //    Es importante hacerlo después de la marca para que la lista de modelos ya exista.
                this.modeloSeleccionado.set(vehiculo.getModeloVehiculo());

            } else {
                // Caso raro: la marca del vehículo no está en la lista de marcas disponibles.
                // Podrías lanzar un error o manejarlo como consideres.
                System.err.println("Advertencia: La marca del vehículo a editar no se encontró en la lista de marcas disponibles.");
                this.marcaSeleccionada.set(null); // Limpiamos para evitar estado inconsistente
                this.modeloSeleccionado.set(null);
            }

        } else {
            // El vehículo no tiene modelo/marca, limpiamos las selecciones.
            this.marcaSeleccionada.set(null);
            this.modeloSeleccionado.set(null);
        }
    }

    /**
     * Devuelve una entidad Vehiculo con los datos actualizados del ViewModel.
     * Si estábamos en modo edición, actualiza la entidad original.
     * Si estábamos en modo creación, crea una nueva entidad.
     *
     * @return La entidad Vehiculo actualizada o nueva.
     */
    public Vehiculo obtenerEntidadActualizada() {
        // 1. Determina si estamos creando o editando.
        //    Si vehiculoModelo no es nulo, estamos editando. De lo contrario, creando.
        Vehiculo vehiculoParaGuardar;

        if (this.vehiculoModelo != null) {
            // MODO EDICIÓN: Usamos la instancia existente que guardamos al principio.
            vehiculoParaGuardar = this.vehiculoModelo;
        } else {
            // MODO CREACIÓN: Creamos una instancia completamente nueva.
            vehiculoParaGuardar = new Vehiculo();
        }

        // 2. Poblamos la entidad (ya sea la nueva o la existente) con los datos del ViewModel.
        //    Ahora `vehiculoParaGuardar` NUNCA es nulo.
        vehiculoParaGuardar.setPatente(patente.get().toUpperCase(Locale.ROOT));
        vehiculoParaGuardar.setNroChasis(nroChasis.get());
        vehiculoParaGuardar.setNroMotor(nroMotor.get());
        vehiculoParaGuardar.setColor(color.get());
        vehiculoParaGuardar.setFechaRegistro(LocalDate.now());
        vehiculoParaGuardar.setEstado(Boolean.TRUE);
        vehiculoParaGuardar.setModeloVehiculo(modeloSeleccionado.get());

        // Estos campos parecen ser nulos por defecto, mantenemos esa lógica.
        vehiculoParaGuardar.setEstadoIngreso(null);
        vehiculoParaGuardar.setCliente(null);

        // NO establezcas el ID a null aquí. Si es una entidad nueva, ya es null.
        // Si es una entidad existente, Hibernate necesita el ID para saber qué fila actualizar.
        // vehiculoParaGuardar.setId(null); // <-- ELIMINAR ESTA LÍNEA SI LA TENÍAS

        return vehiculoParaGuardar;
    }

    /**
     * Para poblar las listas de opciones desde la BD.
     * Esto debería ser llamado por el Controller al inicializar.
     */
    public void cargarListasOpciones() {
        // Aquí iría la llamada a tus servicios/repositorios
        // List<MarcaVehiculo> todasLasMarcas = marcaVehiculoService.findAll();
        // marcasDisponibles.setAll(todasLasMarcas);
        List<MarcaVehiculo> todasLasMarcas = vehiculoServImpl.getAllBrands();
        marcasDisponibles.setAll(todasLasMarcas);
    }

    /**
     * Resetea el estado del ViewModel para comenzar un nuevo proceso de creación.
     */
    public void clear() {
        // Resetear propiedades a sus valores por defecto
        this.vehiculoModelo = null;
        id.set(null);
        patente.set("");
        nroChasis.set("");
        nroMotor.set("");
        color.set("");

        // Limpiar selecciones y listas
        // No limpies 'marcasDisponibles' si es una lista global,
        // pero sí las selecciones y la lista dependiente de modelos.
        marcaSeleccionada.set(null);
        modeloSeleccionado.set(null);
        modelosDisponibles.clear();

        this.vehiculoModelo = null;
    }

    // --- GETTERS PARA LAS PROPIEDADES (Esencial para el Data Binding) ---

    public ObjectProperty<Long> idProperty() { return id; }
    public StringProperty patenteProperty() { return patente; }
    public StringProperty nroChasisProperty() { return nroChasis; }
    public StringProperty nroMotorProperty() { return nroMotor; }
    public StringProperty colorProperty() { return color; }

    public ObservableList<MarcaVehiculo> getMarcasDisponibles() { return marcasDisponibles; }
    public ObjectProperty<MarcaVehiculo> marcaSeleccionadaProperty() { return marcaSeleccionada; }

    public ObservableList<ModeloVehiculo> getModelosDisponibles() { return modelosDisponibles; }
    public ObjectProperty<ModeloVehiculo> modeloSeleccionadoProperty() { return modeloSeleccionado; }

    // Getters para las propiedades de solo lectura
    public ReadOnlyStringProperty nombreModeloSeleccionadoProperty() { return nombreModeloSeleccionado; }
    public ReadOnlyObjectProperty<Year> anioModeloSeleccionadoProperty() { return anioModeloSeleccionado; }
    public ReadOnlyObjectProperty<Double> cilindradaModeloSeleccionadoProperty() { return cilindradaModeloSeleccionado; }
}
