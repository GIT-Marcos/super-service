package SPRService.SPRService.viewModels.tablas;

import SPRService.SPRService.entities.Service;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ServiceRowViewModel {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Service service;

    private final LongProperty codigo;
    private final StringProperty fechaCarga;
    private final StringProperty fechaEntrega;
    private final StringProperty estado;
    private final StringProperty prioridad;
    private final ObjectProperty<BigDecimal> montoFaltante;
    private final ObjectProperty<BigDecimal> montoTotal;

    public ServiceRowViewModel(Service s) {
        this.service = s;
        this.codigo = new SimpleLongProperty(s.getId());
        this.fechaCarga = new SimpleStringProperty(formatearFecha(s.getFechaCarga()));
        this.fechaEntrega = new SimpleStringProperty(formatearFecha(s.getFechaEntrega()));
        this.estado = new SimpleStringProperty(s.getEstadoService().toString());
        this.prioridad = new SimpleStringProperty(s.getPrioridad().toString());
        this.montoFaltante = new SimpleObjectProperty<>(s.getMontoFaltante());
        this.montoTotal = new SimpleObjectProperty<>(s.getMontoTotal());
    }

    /**
     * Para formatear la fecha o devolver un texto por defecto si es nula.
     *
     * @param dateTime La fecha y hora a formatear.
     * @return El string formateado o "-" si la fecha es nula.
     */
    private String formatearFecha(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(FORMATTER);
    }

    public Service getService() {
        return service;
    }

    public long getCodigo() {
        return codigo.get();
    }

    public LongProperty codigoProperty() {
        return codigo;
    }

    public String getFechaCarga() {
        return fechaCarga.get();
    }

    public StringProperty fechaCargaProperty() {
        return fechaCarga;
    }

    public String getFechaEntrega() {
        return fechaEntrega.get();
    }

    public StringProperty fechaEntregaProperty() {
        return fechaEntrega;
    }

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public String getPrioridad() {
        return prioridad.get();
    }

    public StringProperty prioridadProperty() {
        return prioridad;
    }

    public BigDecimal getMontoFaltante() {
        return montoFaltante.get();
    }

    public ObjectProperty<BigDecimal> montoFaltanteProperty() {
        return montoFaltante;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal.get();
    }

    public ObjectProperty<BigDecimal> montoTotalProperty() {
        return montoTotal;
    }
}
