package SPRService.SPRService.viewModels.tablas;

import SPRService.SPRService.entities.NotaRetiro;
import javafx.beans.property.*;

import java.time.format.DateTimeFormatter;

public class NotaRetiroViewModel {

    // Referencia a la entidad original (útil para pasarla a Servicios)
    private final NotaRetiro notaOriginal;

    // Propiedades JavaFX para la Tabla
    private final LongProperty idNota;
    private final StringProperty tipo;
    private final StringProperty fecha;
    private final StringProperty estado;
    private final BooleanProperty esAnulada; // Para lógica de colores/botones sin comparar strings

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotaRetiroViewModel(NotaRetiro n) {
        this.notaOriginal = n;

        this.idNota = new SimpleLongProperty(n.getId());
        this.tipo = new SimpleStringProperty(n.getTipoUso() != null ? n.getTipoUso().toString() : "-");
        this.fecha = new SimpleStringProperty(n.getFecha() != null ? n.getFecha().format(FORMATTER) : "");

        // Lógica de presentación del estado
        boolean activa = n.getActivo() != null && n.getActivo();
        this.estado = new SimpleStringProperty(activa ? "ACTIVA" : "ANULADA");
        this.esAnulada = new SimpleBooleanProperty(!activa);
    }

    public NotaRetiro getNotaOriginal() {
        return notaOriginal;
    }

    // --- Getters de Propiedades (Patrón JavaFX) ---

    public LongProperty idNotaProperty() { return idNota; }
    public StringProperty tipoProperty() { return tipo; }
    public StringProperty fechaProperty() { return fecha; }
    public StringProperty estadoProperty() { return estado; }
    public BooleanProperty esAnuladaProperty() { return esAnulada; }
}