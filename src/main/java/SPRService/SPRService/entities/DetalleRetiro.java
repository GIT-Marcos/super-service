package SPRService.SPRService.entities;

import jakarta.persistence.*;
import SPRService.SPRService.util.Operador;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_retiros")
public class DetalleRetiro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_detalles_retiros")
    private Long id;

    @Column(length = 10, nullable = false)
    private Double cantidadRetirada = 0D;

    @Column(name = "sub_total", precision = 16, scale = 2, nullable = false)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_repuesto", nullable = false)
    private Repuesto repuesto;

    protected DetalleRetiro() {
    }

    public DetalleRetiro(Double cantidadRetirada, Repuesto repuesto) {
        this.cantidadRetirada = cantidadRetirada;
        this.repuesto = repuesto;
        calcularSubTotal();
    }

    @PrePersist
    public void prePersist() {
        this.id = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCantidadRetirada() {
        return cantidadRetirada;
    }

    public void setCantidadRetirada(Double cantidadRetirada) {
        this.cantidadRetirada = cantidadRetirada;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public void setRepuesto(Repuesto repuesto) {
        this.repuesto = repuesto;
        calcularSubTotal();
    }

    @Override
    public String toString() {
        return "DetalleRetiro{" +
                "id=" + id +
                ", cantidadRetirada=" + cantidadRetirada +
                ", subTotal=" + subTotal +
                '}';
    }

    private void calcularSubTotal() {
        if (this.cantidadRetirada != null && this.repuesto != null) {
            this.subTotal = Operador.multiplicarDineroPorAlgo(this.repuesto.getPrecio(),
                    BigDecimal.valueOf(this.cantidadRetirada));
        } else {
            this.subTotal = BigDecimal.ONE;
        }
    }
}
