package SPRService.SPRService.entities;

import SPRService.SPRService.util.ManejadorInputs;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "stocks")
public class Stock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_stock")
    private Long id;

    @Column(nullable = false)
    private Double cantidadExistente;

    @Column(name = "cantidad_minima")
    private Double cantMinima;

    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @Column
    private String lote;

    @Column
    private String observaciones;

    @Column(nullable = false)
    private Boolean activo;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ubicacion", nullable = false)
    private Ubicacion ubicacion;

    public Stock() {
        this.activo = Boolean.TRUE;
    }

    public Stock(Long id, Double cantidadExistente, Double cantMinima, String unidadMedida,
                 String lote, String observaciones, Ubicacion ubicacion) {
        this.id = id;
        this.cantidadExistente = cantidadExistente;
        this.cantMinima = cantMinima;
        this.unidadMedida = unidadMedida;
        this.lote = lote;
        this.observaciones = observaciones;
        this.activo = Boolean.TRUE;
        asociarUbicacion(ubicacion);
    }

    public void asociarUbicacion(Ubicacion u) {
        this.ubicacion = u;
        u.getStocks().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCantidadExistente() {
        return cantidadExistente;
    }

    public void setCantidadExistente(Double cantidadExistente) {
        this.cantidadExistente = cantidadExistente;
    }

    public Double getCantMinima() {
        return cantMinima;
    }

    public void setCantMinima(Double cantMinima) {
        this.cantMinima = cantMinima;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", cantidadExistente=" + cantidadExistente +
                ", cantMinima=" + cantMinima +
                ", unidadMedida='" + unidadMedida + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", lote='" + lote + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", activo=" + activo +
                '}';
    }

    /**
     * Público para usar en controladores.
     */
    public boolean haySuficiente(Double cantidadSalida) {
        if (cantidadSalida > this.cantidadExistente) {
            return false;
        }
        return true;
    }

    /**
     * Usar en servicio
     * Usado cuando se debe retirar stock. Calcula la nueva cantidad de existente cuando se retira stock.
     */
    public void salidaDeStock(Double cantidadSalida) {
        if (cantidadSalida.isInfinite() || cantidadSalida.isNaN() || cantidadSalida < 0)
            throw new IllegalArgumentException("Cantidad de stock a agregar en mal formato");
        if (!haySuficiente(cantidadSalida))
            throw new IllegalArgumentException("La cantidad de salida de stock es mayor al existente.");

        Double nuevaCantidad = this.getCantidadExistente() - cantidadSalida;
        // para redondear
        nuevaCantidad = ManejadorInputs.cantidadStock(String.valueOf(nuevaCantidad), true);
        this.setCantidadExistente(nuevaCantidad);
    }

    /**
     * Usar en servicio
     * Usado para agregar cantidad existente al stock.
     */
    public void entradaStock(Double cantidadEntrada) {
        if (cantidadEntrada.isInfinite() || cantidadEntrada.isNaN() || cantidadEntrada < 0)
            throw new IllegalArgumentException("Cantidad de stock a agregar en mal formato");

        Double nuevaCantidad = this.cantidadExistente + cantidadEntrada;
        nuevaCantidad = ManejadorInputs.cantidadStock(String.valueOf(nuevaCantidad), true);

        this.setCantidadExistente(nuevaCantidad);
    }
}
