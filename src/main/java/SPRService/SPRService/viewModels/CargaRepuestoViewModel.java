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

public class CargaRepuestoViewModel {

    // --- DEPENDENCIAS ---
    private final RepuestoServ repuestoService;
    private final MarcaRepuestoServ marcaRepuestoService;

    // --- ESTADO INTERNO ---
    private Repuesto repuestoOriginal; // Guarda el estado original para la modificación

    // --- PROPIEDADES PARA BINDING CON LA VISTA (FXML) ---

    // Propiedades del Repuesto
    private final StringProperty codBarras = new SimpleStringProperty("");
    private final StringProperty nombreProducto = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> precio = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<MarcaRepuesto> marcaSeleccionada = new SimpleObjectProperty<>();

    // Propiedades del Stock
    private final DoubleProperty cantidadExistente = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cantidadMinima = new SimpleDoubleProperty(0.0);
    private final StringProperty lote = new SimpleStringProperty("");
    private final StringProperty observaciones = new SimpleStringProperty("");
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
        precio.set(repuesto.getPrecio());

        // Si la marca no está en la lista, la añade temporalmente para que se muestre
        if (!marcasDisponibles.contains(repuesto.getMarcaRepuesto())) {
            marcasDisponibles.add(repuesto.getMarcaRepuesto());
        }
        marcaSeleccionada.set(repuesto.getMarcaRepuesto());

        Stock stock = repuesto.getStock();
        cantidadExistente.set(stock.getCantidadExistente());
        cantidadMinima.set(stock.getCantMinima());
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
        precio.set(BigDecimal.ZERO);
        marcaSeleccionada.set(null);
        cantidadExistente.set(0.0);
        cantidadMinima.set(0.0);
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
        if (marcaSeleccionada.get() == null) {
            throw new IllegalArgumentException("Debe seleccionar una marca para el repuesto.");
        }
        ManejadorInputs.codBarras(codBarras.getValue(), true);
        ManejadorInputs.textoGenerico(nombreProducto.getValue(), true, "Nombre de repuesto",
                60);
        ManejadorInputs.dinero(precio.getValue().toString(), true, false);
        ManejadorInputs.cantidadStock(cantidadExistente.getValue().toString(), true);
        ManejadorInputs.cantidadStock(cantidadMinima.getValue().toString(), true);
        // La conversión de String a BigDecimal/Double se haría en el Controller,
        // pero idealmente se usarían TextFormatters para evitar inputs inválidos.
        // Aquí asumimos que los bindings ya han poblado las propiedades correctamente.
        ManejadorInputs.textoGenerico(uniMedidaSeleccionada.getValue(), true, null, 20);
        ManejadorInputs.textoGenerico(ubicacionSeleccionada.getValue(), true, null, 20);
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
            stock = new Stock();
            repuesto.setStock(stock);
        }

        // Poblar Stock
        stock.setCantidadExistente(cantidadExistente.get());
        stock.setCantMinima(cantidadMinima.get());
        stock.setUnidadMedida(uniMedidaSeleccionada.get());
        stock.setUbicacion(ubicacionSeleccionada.get());
        stock.setLote(lote.get());
        stock.setObservaciones(observaciones.get());

        // Poblar Repuesto
        repuesto.setCodBarra(codBarras.get());
        repuesto.setDetalle(nombreProducto.get());
        repuesto.setPrecio(precio.get());
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

    public StringProperty codBarrasProperty() { return codBarras; }
    public StringProperty nombreProductoProperty() { return nombreProducto; }
    public ObjectProperty<BigDecimal> precioProperty() { return precio; }
    public ObjectProperty<MarcaRepuesto> marcaSeleccionadaProperty() { return marcaSeleccionada; }
    public DoubleProperty cantidadExistenteProperty() { return cantidadExistente; }
    public DoubleProperty cantidadMinimaProperty() { return cantidadMinima; }
    public StringProperty loteProperty() { return lote; }
    public StringProperty observacionesProperty() { return observaciones; }
    public StringProperty uniMedidaSeleccionadaProperty() { return uniMedidaSeleccionada; }
    public StringProperty ubicacionSeleccionadaProperty() { return ubicacionSeleccionada; }

    public ObservableList<MarcaRepuesto> getMarcasDisponibles() { return marcasDisponibles; }
    public ObservableList<String> getUnidadesDeMedida() { return unidadesDeMedida; }
    public ObservableList<String> getUbicacionesDisponibles() { return ubicacionesDisponibles; }
}