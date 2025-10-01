package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "repuestos")
public class Repuesto implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_repuesto")
    private Long id;

    @Column(name = "codigo_barra",nullable = false, unique = true)
    private String codBarra;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String detalle;

    @Column(precision = 16, scale = 2, nullable = false)
    private BigDecimal precio;
    
    @Column(nullable = false)
    private Boolean activo;
    
    //RELACIÓN HACIA STOCK 1-1
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "fk_stock", nullable = false)
    private Stock stock;
    
    public Repuesto() {
    }

    public Repuesto(Long id, String codBarra, String marca, String detalle, BigDecimal precio, Stock stock) {
        this.id = id;
        this.codBarra = codBarra;
        this.marca = marca;
        this.detalle = detalle;
        this.precio = precio;
        this.activo = Boolean.TRUE;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodBarra() {
        return codBarra;
    }

    public void setCodBarra(String codBarra) {
        this.codBarra = codBarra;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Repuesto{" + "id=" + id + ", codBarra=" + codBarra + ", marca=" + marca + ", detalle=" + detalle + ", precio=" + precio + ", activo=" + activo + '}';
    }

}
