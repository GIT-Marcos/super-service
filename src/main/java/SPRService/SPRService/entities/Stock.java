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

    @Column(nullable = false)
    private String ubicacion;

    @Column
    private String lote;

    @Column
    private String observaciones;

    @Column(nullable = false)
    private Boolean activo;

    public Stock() {
    }

    public Stock(Long id, Double cantidadExistente, Double cantMinima, String unidadMedida, String ubicacion,
                 String lote, String observaciones) {
        this.id = id;
        this.cantidadExistente = cantidadExistente;
        this.cantMinima = cantMinima;
        this.unidadMedida = unidadMedida;
        this.ubicacion = ubicacion;
        this.lote = lote;
        this.observaciones = observaciones;
        this.activo = Boolean.TRUE;
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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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

    @Override
    public String toString() {
        return "Stock{" + "id=" + id + ", cantidad=" + cantidadExistente + ", cantMinima=" + cantMinima + ", unidadMedida=" + unidadMedida + ", ubicacion=" + ubicacion + ", lote=" + lote + ", observaciones=" + observaciones + ", activo=" + activo + '}';
    }

    /**
     * Calcula la nueva cantidad de existente cuando se retira stock.
     */
    public void salidaDeStock(Double cantidadSalida){
        // todo: ver si debe ir una excepción acá
        if (cantidadSalida > cantidadExistente)
            throw new IllegalArgumentException("La cantidad de salida de stock es mayor al existente.");
        if (cantidadSalida < 0) cantidadSalida = 0D;
        Double nuevaCantidad = this.getCantidadExistente() - cantidadSalida;
        // para redondear
        nuevaCantidad = ManejadorInputs.cantidadStock(String.valueOf(nuevaCantidad), true);
        this.setCantidadExistente(nuevaCantidad);
    }

    /**
     * Usado para agregar cantidad existente al stock.
     */
    public void entradaStock(Double cantidadEntrada) {
        this.setCantidadExistente(this.getCantidadExistente() + cantidadEntrada);
    }
    
}
