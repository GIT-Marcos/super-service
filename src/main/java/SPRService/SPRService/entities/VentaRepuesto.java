package SPRService.SPRService.entities;

import jakarta.persistence.*;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//TODO: separar el código de la venta (id) y agregar código de factura con letras y números.
@Entity
@Table(name = "ventas_repuestos")
public class VentaRepuesto implements Serializable, Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_venta_repuestos")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(name = "monto_total", precision = 16, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "monto_faltante", precision = 16, scale = 2, nullable = false)
    private BigDecimal montoFaltante;

    @Column(nullable = false)
    private Boolean activo;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "estado_venta")
    private EstadoVentaRepuesto estadoVenta;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_nota_retiro")
    private NotaRetiro notaRetiro;

    @OneToMany(mappedBy = "ventaRepuesto", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Pago> pagos = new HashSet<>();

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_cliente")
    private Cliente cliente;

    public VentaRepuesto() {
    }

    public VentaRepuesto(Long id, NotaRetiro notaRetiro, Set<Pago> pagos, Cliente cliente) {
        this.id = id;
        this.fechaVenta = LocalDateTime.now();
        this.activo = true;
        this.notaRetiro = notaRetiro;
        calculaMontoTotal();
        this.pagos = pagos;
        this.montoFaltante = this.montoTotal;
        calcularEstadoVenta();
        asociarCliente(cliente);
    }

    /**
     * En caso de ser consumidor final pasar null.
     */
    public void asociarCliente(Cliente c) {
        if (c != null) {
            this.cliente = c;
            c.getVentas().add(this);
        }
    }

    public void cancelarVenta() {
        this.activo = Boolean.FALSE;
        this.estadoVenta = EstadoVentaRepuesto.CANCELADO;
    }

    @Override
    public void asociarPago(Pago pago) {
        this.getPagos().add(pago);
        pago.setVentaRepuesto(this);
        this.montoFaltante = this.montoFaltante.subtract(pago.getMontoPagado());
        if (this.montoFaltante.compareTo(BigDecimal.ZERO) < 0) {
            this.montoFaltante = BigDecimal.ZERO;
        }
        calcularEstadoVenta();
    }

    private void calculaMontoTotal() {
        this.montoTotal = BigDecimal.ZERO;
        if (this.notaRetiro != null && this.notaRetiro.getDetallesRetiroList() != null) {
            for (DetalleRetiro d : this.notaRetiro.getDetallesRetiroList()) {
                BigDecimal subTotal = d.getSubTotal();
                this.montoTotal = this.montoTotal.add(subTotal);
            }
        } else {
            throw new NullPointerException("no hay nota de retiro en esta venta.");
        }
    }

    private void calcularEstadoVenta() {
        if (this.getPagos() == null) {
            this.setEstadoVenta(EstadoVentaRepuesto.PRESUPUESTANDO);
        } else {
            if (this.montoFaltante.compareTo(BigDecimal.ZERO) <= 0) {
                this.estadoVenta = EstadoVentaRepuesto.PAGADO;
            } else {
                this.estadoVenta = EstadoVentaRepuesto.PENDIENTE_PAGO;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoFaltante() {
        return montoFaltante;
    }

    public void setMontoFaltante(BigDecimal montoFaltante) {
        this.montoFaltante = montoFaltante;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public EstadoVentaRepuesto getEstadoVenta() {
        return estadoVenta;
    }

    public void setEstadoVenta(EstadoVentaRepuesto estadoVenta) {
        this.estadoVenta = estadoVenta;
    }

    public NotaRetiro getNotaRetiro() {
        return notaRetiro;
    }

    public void setNotaRetiro(NotaRetiro notaRetiro) {
        this.notaRetiro = notaRetiro;
    }

    public Set<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(Set<Pago> pagosList) {
        this.pagos = pagosList;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "VentaRepuesto{" +
                "id=" + id +
                ", fechaVenta=" + fechaVenta +
                ", montoTotal=" + montoTotal +
                ", montoFaltante=" + montoFaltante +
                ", activo=" + activo +
                ", estadoVenta=" + estadoVenta +
                '}';
    }

}
