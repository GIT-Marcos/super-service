package SPRService.SPRService.viewModels;

import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.entities.Ubicacion;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.services.MarcaRepuestoServ;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.services.UbicacionServ;
import SPRService.SPRService.util.ManejadorInputs;
import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

public class CargaRepuestoViewModel {

    private final RepuestoServ repuestoService;
    private final MarcaRepuestoServ marcaRepuestoService;
    private final UbicacionServ ubicacionService;

    private Repuesto repuestoOriginal;

    // Propiedades
    private final StringProperty codBarras = new SimpleStringProperty();
    private final StringProperty nombreProducto = new SimpleStringProperty();
    private final StringProperty precio = new SimpleStringProperty();
    private final ObjectProperty<MarcaRepuesto> marcaSeleccionada = new SimpleObjectProperty<>();

    private final StringProperty cantidadExistente = new SimpleStringProperty();
    private final StringProperty cantidadMinima = new SimpleStringProperty();
    private final StringProperty lote = new SimpleStringProperty();
    private final StringProperty observaciones = new SimpleStringProperty();
    private final StringProperty uniMedidaSeleccionada = new SimpleStringProperty();
    private final ObjectProperty<Ubicacion> ubicacionSeleccionada = new SimpleObjectProperty<>();

    // Listas
    private final ObservableList<MarcaRepuesto> marcasDisponibles = FXCollections.observableArrayList();
    private final ObservableList<String> unidadesDeMedida = FXCollections.observableArrayList();
    private final ObservableList<Ubicacion> ubicacionesDisponibles = FXCollections.observableArrayList();

    @Inject
    public CargaRepuestoViewModel(RepuestoServ repuestoService,
                                  MarcaRepuestoServ marcaRepuestoService,
                                  UbicacionServ ubicacionService) {
        this.repuestoService = repuestoService;
        this.marcaRepuestoService = marcaRepuestoService;
        this.ubicacionService = ubicacionService;
    }

    public void inicializar() {
        cargarListaMarcas();
        cargarUnidadesDeMedida();
        cargarUbicaciones();
        if (!unidadesDeMedida.isEmpty()) uniMedidaSeleccionada.set(unidadesDeMedida.getFirst());
    }

    public void poblarParaModificacion(Repuesto repuesto) {
        this.repuestoOriginal = repuesto;

        codBarras.set(repuesto.getCodBarra());
        nombreProducto.set(repuesto.getDetalle());
        precio.set(repuesto.getPrecio().toPlainString());

        // Lógica para Marcas (revisar si necesita la misma corrección que Ubicación)
        MarcaRepuesto marcaDelRepuesto = repuesto.getMarcaRepuesto();
        MarcaRepuesto marcaEnLista = marcasDisponibles.stream()
                .filter(m -> m.getId() != null && m.getId().equals(marcaDelRepuesto.getId()))
                .findFirst()
                .orElse(null);

        if (marcaEnLista != null) {
            marcaSeleccionada.set(marcaEnLista);
        } else {
            marcasDisponibles.add(marcaDelRepuesto);
            marcaSeleccionada.set(marcaDelRepuesto);
        }

        // --- STOCK ---
        Stock stock = repuesto.getStock();
        cantidadExistente.set(String.valueOf(stock.getCantidadExistente()));
        cantidadMinima.set(String.valueOf(stock.getCantMinima()));
        lote.set(stock.getLote());
        observaciones.set(stock.getObservaciones());
        uniMedidaSeleccionada.set(stock.getUnidadMedida());

        // --- SOLUCIÓN AL PROBLEMA DEL CONTAINS DE UBICACIÓN ---
        Ubicacion ubicacionDelStock = stock.getUbicacion();

        if (ubicacionDelStock != null) {
            // 1. Buscamos en la lista actual si hay alguna ubicación con el MISMO ID
            Ubicacion ubicacionEnLista = ubicacionesDisponibles.stream()
                    .filter(u -> u.getId() != null && u.getId().equals(ubicacionDelStock.getId()))
                    .findFirst()
                    .orElse(null);

            if (ubicacionEnLista != null) {
                // 2. Si existe, seleccionamos LA INSTANCIA DE LA LISTA.
                // Esto asegura que el ComboBox la ilumine correctamente.
                ubicacionSeleccionada.set(ubicacionEnLista);
            } else {
                // 3. Si no existe (ej: estaba archivada y no vino en el verTodas), la agregamos y seleccionamos.
                ubicacionesDisponibles.add(ubicacionDelStock);
                ubicacionSeleccionada.set(ubicacionDelStock);
            }
        }
    }

    public Optional<Repuesto> guardarRepuesto() throws IllegalArgumentException, DuplicateProductException {
        validarInputs();
        Repuesto repuestoParaGuardar = construirEntidadDesdeViewModel();
        if (repuestoOriginal == null) {
            return repuestoService.cargarRepuesto(repuestoParaGuardar);
        } else {
            return repuestoService.modificarRepuesto(repuestoParaGuardar);
        }
    }

    public void crearNuevaMarca(String nombreMarca) {
        MarcaRepuesto nuevaMarca = new MarcaRepuesto(null, nombreMarca, new HashSet<>());
        marcaRepuestoService.cargarMarca(nuevaMarca)
                .ifPresent(m -> {
                    marcasDisponibles.addFirst(nuevaMarca);
                    marcaSeleccionada.set(nuevaMarca);
                });
    }

    public void crearNuevaUbicacion(String nombreUbicacion) {
        Ubicacion nuevaUbicacion = new Ubicacion(null, nombreUbicacion, new ArrayList<>());
        ubicacionService.cargarNueva(nuevaUbicacion)
                .ifPresent(u -> {
                    ubicacionesDisponibles.addFirst(u);
                    ubicacionSeleccionada.set(u);
                });
    }

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
        if (!unidadesDeMedida.isEmpty()) uniMedidaSeleccionada.set(unidadesDeMedida.getFirst());
        else uniMedidaSeleccionada.set(null);
    }

    private void validarInputs() throws IllegalArgumentException {
        if (marcaSeleccionada.get() == null) throw new IllegalArgumentException("Debe seleccionar una marca.");
        ManejadorInputs.codBarras(codBarras.getValue(), true);
        ManejadorInputs.textoGenerico(nombreProducto.getValue(), true, "Nombre de repuesto", 60);
        ManejadorInputs.dinero(precio.getValue(), true, false);
        ManejadorInputs.cantidadStock(cantidadExistente.getValue(), true);
        ManejadorInputs.cantidadStock(cantidadMinima.getValue(), true);
        if (uniMedidaSeleccionada.getValue() == null) throw new IllegalArgumentException("Seleccione unidad de medida.");
        if (ubicacionSeleccionada.get() == null) throw new IllegalArgumentException("Seleccione una ubicación.");
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
        stock.setCantidadExistente(Double.parseDouble(cantidadExistente.get()));
        stock.setCantMinima(Double.parseDouble(cantidadMinima.get()));
        stock.setUnidadMedida(uniMedidaSeleccionada.get());
        stock.setUbicacion(ubicacionSeleccionada.get());
        stock.setLote(lote.get());
        stock.setObservaciones(observaciones.get());
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
        ubicacionesDisponibles.setAll(ubicacionService.verTodas());
    }

    // Getters
    public StringProperty codBarrasProperty() { return codBarras; }
    public StringProperty nombreProductoProperty() { return nombreProducto; }
    public StringProperty precioProperty() { return precio; }
    public ObjectProperty<MarcaRepuesto> marcaSeleccionadaProperty() { return marcaSeleccionada; }
    public StringProperty cantidadExistenteProperty() { return cantidadExistente; }
    public StringProperty cantidadMinimaProperty() { return cantidadMinima; }
    public StringProperty loteProperty() { return lote; }
    public StringProperty observacionesProperty() { return observaciones; }
    public StringProperty uniMedidaSeleccionadaProperty() { return uniMedidaSeleccionada; }
    public ObjectProperty<Ubicacion> ubicacionSeleccionadaProperty() { return ubicacionSeleccionada; }
    public ObservableList<MarcaRepuesto> getMarcasDisponibles() { return marcasDisponibles; }
    public ObservableList<String> getUnidadesDeMedida() { return unidadesDeMedida; }
    public ObservableList<Ubicacion> getUbicacionesDisponibles() { return ubicacionesDisponibles; }
}