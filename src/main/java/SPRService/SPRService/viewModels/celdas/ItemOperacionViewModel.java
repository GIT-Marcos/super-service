package SPRService.SPRService.viewModels.celdas;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class ItemOperacionViewModel {

    private final StringProperty tipo = new SimpleStringProperty();
    private final LongProperty codigo = new SimpleLongProperty();
    private final ObjectProperty<BigDecimal> total = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> faltante = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> fecha = new SimpleObjectProperty<>();

    public ItemOperacionViewModel(String tipo, Long codigo, BigDecimal total, BigDecimal faltante, LocalDateTime fecha) {
        this.tipo.set(tipo);
        this.codigo.set(codigo);
        this.total.set(total);
        this.faltante.set(faltante);
        this.fecha.set(fecha);
    }

    public String getTipo() {
        return tipo.get();
    }

    public StringProperty tipoProperty() {
        return tipo;
    }

    public long getCodigo() {
        return codigo.get();
    }

    public LongProperty codigoProperty() {
        return codigo;
    }

    public BigDecimal getTotal() {
        return total.get();
    }

    public ObjectProperty<BigDecimal> totalProperty() {
        return total;
    }

    public BigDecimal getFaltante() {
        return faltante.get();
    }

    public ObjectProperty<BigDecimal> faltanteProperty() {
        return faltante;
    }

    public LocalDateTime getFecha() {
        return fecha.get();
    }

    public ObjectProperty<LocalDateTime> fechaProperty() {
        return fecha;
    }
}
