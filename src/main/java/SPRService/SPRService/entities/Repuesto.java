package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "repuestos")
public class Repuesto implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_repuesto")
    private Long id;

    @Column(name = "codigo_barras",nullable = false, unique = true)
    private String codBarra;

    @Column(nullable = false)
    private String detalle;

    @Column(precision = 16, scale = 2, nullable = false)
    private BigDecimal precio;
    
    @Column(nullable = false)
    private Boolean activo;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_marca_repuesto", nullable = false)
    private MarcaRepuesto marcaRepuesto;
    
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_stock", nullable = false)
    private Stock stock;
    
    public Repuesto() {
    }

    public Repuesto(Long id, String codBarra, String detalle, BigDecimal precio,
                    MarcaRepuesto marcaRepuesto, Stock stock) {
        this.id = id;
        this.codBarra = codBarra;
        this.detalle = detalle;
        this.precio = precio;
        this.marcaRepuesto = marcaRepuesto;
        this.marcaRepuesto.getRepuestos().add(this);
        this.stock = stock;
        this.activo = Boolean.TRUE;
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

    public MarcaRepuesto getMarcaRepuesto() {
        return marcaRepuesto;
    }

    /**
     * Usar vincularRepuestoYMarca(MarcaRepuesto m) en lugar de esta.
     */
    public void setMarcaRepuesto(MarcaRepuesto marcaRepuesto) {
        this.marcaRepuesto = marcaRepuesto;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Repuesto{" +
                "id=" + id +
                ", codBarra='" + codBarra + '\'' +
                ", detalle='" + detalle + '\'' +
                ", precio=" + precio +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Repuesto repuesto)) return false;
        return Objects.equals(codBarra, repuesto.codBarra);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codBarra);
    }

    public void vincularRepuestoYMarca(MarcaRepuesto m) {
        this.marcaRepuesto = m;
        m.getRepuestos().add(this);
    }
}
