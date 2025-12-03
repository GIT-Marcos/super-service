package SPRService.SPRService.viewModels;

import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.services.MarcaRepuestoServ;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects; // Importación necesaria para el mejorado de bindings

public class CargaRepuestoViewModel {

    // --- DEPENDENCIAS ---
    private final RepuestoServ repuestoService;
    private final MarcaRepuestoServ marcaRepuestoService;

    // --- ESTADO INTERNO ---
    private Repuesto repuestoOriginal; // Guarda el estado original para la modificación

    // --- PROPIEDADES PARA BINDING CON LA VISTA (FXML) ---
    // ¡CAMBIADAS A StringProperty!
    private final StringProperty codBarras = new SimpleStringProperty();
    private final StringProperty nombreProducto = new SimpleStringProperty();
    private final StringProperty precio = new SimpleStringProperty(); // Ahora String
    private final ObjectProperty<MarcaRepuesto> marcaSeleccionada = new SimpleObjectProperty<>();

    // Propiedades del Stock
    private final StringProperty cantidadExistente = new SimpleStringProperty(); // Ahora String
    private final StringProperty cantidadMinima = new SimpleStringProperty(); // Ahora String
    private final StringProperty lote = new SimpleStringProperty();
    private final StringProperty observaciones = new SimpleStringProperty();
    private final StringProperty uniMedidaSeleccionada = new SimpleStringProperty();
    private final StringProperty ubicacionSeleccionada = new SimpleStringProperty();


    // --- LISTAS OBSERVABLES PARA POBLAR COMBOBOX ---
    private final ObservableList<MarcaRepuesto> marcasDisponibles = FXCollections.observableArrayList();
    private final ObservableList<String> unidadesDeMedida = FXCollections.observableArrayList();
    private final ObservableList<String> ubicacionesDisponibles = FXCollections.observableArrayList();

    @Inject
    public CargaRepuestoViewModel(RepuestoServ repuestoService, MarcaRepuestoServ marcaRepuestoService) {
        this.repuestoService = repuestoService;
        this.marcaRepuestoService = marcaRepuestoService;
    }

    /**
     * Carga los datos iniciales necesarios para la vista.
     */
    public void inicializar() {
        cargarListaMarcas();
        cargarUnidadesDeMedida();
        cargarUbicaciones();

        if (!unidadesDeMedida.isEmpty()) {
            uniMedidaSeleccionada.set(unidadesDeMedida.getFirst());
        }
    }

    /**
     * Rellena el ViewModel con los datos de un Repuesto existente para su modificación.
     * @param repuesto El repuesto a modificar.
     */
    public void poblarParaModificacion(Repuesto repuesto) {
        this.repuestoOriginal = repuesto;

        codBarras.set(repuesto.getCodBarra());
        nombreProducto.set(repuesto.getDetalle());
        // Se convierten los valores numéricos a String
        precio.set(repuesto.getPrecio().toPlainString());

        // Si la marca no está en la lista, la añade temporalmente para que se muestre
        if (!marcasDisponibles.contains(repuesto.getMarcaRepuesto())) {
            marcasDisponibles.add(repuesto.getMarcaRepuesto());
        }
        marcaSeleccionada.set(repuesto.getMarcaRepuesto());

        Stock stock = repuesto.getStock();
        // Se convierten los valores numéricos a String
        cantidadExistente.set(String.valueOf(stock.getCantidadExistente()));
        cantidadMinima.set(String.valueOf(stock.getCantMinima()));
        lote.set(stock.getLote());
        observaciones.set(stock.getObservaciones());
        uniMedidaSeleccionada.set(stock.getUnidadMedida());

        // Si la ubicación no está en la lista, la añade temporalmente
        if (!ubicacionesDisponibles.contains(stock.getUbicacion())) {
            ubicacionesDisponibles.add(stock.getUbicacion());
        }
        ubicacionSeleccionada.set(stock.getUbicacion());
    }

    /**
     * Valida los inputs y guarda el repuesto en la base de datos.
     * @return El repuesto guardado.
     * @throws IllegalArgumentException si la validación de algún campo falla.
     * @throws DuplicateProductException si ya existe un producto con el mismo código de barras.
     */
    public Repuesto guardarRepuesto() throws IllegalArgumentException, DuplicateProductException {
        validarInputs();
        Repuesto repuestoParaGuardar = construirEntidadDesdeViewModel();

        if (repuestoOriginal == null) {
            return repuestoService.cargarRepuesto(repuestoParaGuardar);
        } else {
            return repuestoService.modificarRepuesto(repuestoParaGuardar);
        }
    }

    /**
     * Crea una nueva marca, la persiste y la selecciona en el ComboBox.
     * @param nombreMarca El nombre para la nueva marca.
     * @return La nueva MarcaRepuesto creada.
     */
    public MarcaRepuesto crearNuevaMarca(String nombreMarca) {
        MarcaRepuesto nuevaMarca = new MarcaRepuesto(null, nombreMarca, new HashSet<>());
        nuevaMarca = marcaRepuestoService.cargarMarca(nuevaMarca);
        marcasDisponibles.addFirst(nuevaMarca);
        marcaSeleccionada.set(nuevaMarca);
        return nuevaMarca;
    }

    /**
     * Limpia todos los campos del ViewModel para prepararlo para una nueva carga.
     */
    public void limpiar() {
        repuestoOriginal = null;
        codBarras.set("");
        nombreProducto.set("");
        precio.set("0.00");
        marcaSeleccionada.set(null);
        cantidadExistente.set("0.0");
        cantidadMinima.set("0.0");
        lote.set("");
        observaciones.set("");
        ubicacionSeleccionada.set(null);

        if (!unidadesDeMedida.isEmpty()) {
            uniMedidaSeleccionada.set(unidadesDeMedida.getFirst());
        } else {
            uniMedidaSeleccionada.set(null);
        }
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private void validarInputs() throws IllegalArgumentException {
        // En el ViewModel se asume que los valores vienen como String (de los TextField)
        // y se validan con el ManejadorInputs.
        // Las validaciones de campos numéricos (precio, stock) también manejan el formato.

        if (marcaSeleccionada.get() == null) {
            throw new IllegalArgumentException("Debe seleccionar una marca para el repuesto.");
        }

        // Uso de ManejadorInputs con los StringProperty
        ManejadorInputs.codBarras(codBarras.getValue(), true);
        ManejadorInputs.textoGenerico(nombreProducto.getValue(), true, "Nombre de repuesto",
                60);
        // ManejadorInputs.dinero realiza validación de formato numérico de String a BigDecimal
        ManejadorInputs.dinero(precio.getValue(), true, false);
        // ManejadorInputs.cantidadStock realiza validación de formato numérico de String a Double
        ManejadorInputs.cantidadStock(cantidadExistente.getValue(), true);
        ManejadorInputs.cantidadStock(cantidadMinima.getValue(), true);

        // Se reemplaza ManejadorInputs para combobox/selecciones por una verificación de nulidad simple si es requerido.
        if (uniMedidaSeleccionada.getValue() == null || uniMedidaSeleccionada.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una unidad de medida.");
        }
        if (ubicacionSeleccionada.getValue() == null || ubicacionSeleccionada.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una ubicación.");
        }

        ManejadorInputs.textoGenerico(lote.getValue(), false, null, 40);
        ManejadorInputs.textoGenerico(observaciones.getValue(), false, null, 100);
    }

    private Repuesto construirEntidadDesdeViewModel() {
        Stock stock;
        Repuesto repuesto;
        if (repuestoOriginal != null) {
            repuesto = repuestoOriginal;
            stock = repuesto.getStock();
        } else {
            repuesto = new Repuesto();
            repuesto.setActivo(Boolean.TRUE);
            stock = new Stock();
            stock.setActivo(Boolean.TRUE);
            repuesto.setStock(stock);
        }

        // Poblar Stock - Conversión de String a Double
        // Los valores se parsean después de la validación
        stock.setCantidadExistente(Double.parseDouble(cantidadExistente.get()));
        stock.setCantMinima(Double.parseDouble(cantidadMinima.get()));
        stock.setUnidadMedida(uniMedidaSeleccionada.get());
        stock.setUbicacion(ubicacionSeleccionada.get());
        stock.setLote(lote.get());
        stock.setObservaciones(observaciones.get());

        // Poblar Repuesto - Conversión de String a BigDecimal
        // Los valores se parsean después de la validación
        repuesto.setCodBarra(codBarras.get());
        repuesto.setDetalle(nombreProducto.get());
        repuesto.setPrecio(new BigDecimal(precio.get()));
        repuesto.setMarcaRepuesto(marcaSeleccionada.get());

        return repuesto;
    }

    private void cargarListaMarcas() {
        marcasDisponibles.setAll(marcaRepuestoService.verTodas());
    }

    private void cargarUnidadesDeMedida() {
        unidadesDeMedida.setAll("Unidad", "Metros", "Kilos", "Gramos", "Litros");
    }

    private void cargarUbicaciones() {
        ubicacionesDisponibles.setAll("Depósito A", "Depósito B", "Depósito C", "Depósito D", "Depósito E");
    }

    // --- GETTERS PARA LAS PROPIEDADES (para que la Vista pueda acceder a ellas) ---
    // Ahora todas las propiedades enlazadas a TextField son StringProperty

    public StringProperty codBarrasProperty() { return codBarras; }
    public StringProperty nombreProductoProperty() { return nombreProducto; }
    public StringProperty precioProperty() { return precio; }
    public ObjectProperty<MarcaRepuesto> marcaSeleccionadaProperty() { return marcaSeleccionada; }
    public StringProperty cantidadExistenteProperty() { return cantidadExistente; }
    public StringProperty cantidadMinimaProperty() { return cantidadMinima; }
    public StringProperty loteProperty() { return lote; }
    public StringProperty observacionesProperty() { return observaciones; }
    public StringProperty uniMedidaSeleccionadaProperty() { return uniMedidaSeleccionada; }
    public StringProperty ubicacionSeleccionadaProperty() { return ubicacionSeleccionada; }

    public ObservableList<MarcaRepuesto> getMarcasDisponibles() { return marcasDisponibles; }
    public ObservableList<String> getUnidadesDeMedida() { return unidadesDeMedida; }
    public ObservableList<String> getUbicacionesDisponibles() { return ubicacionesDisponibles; }
}