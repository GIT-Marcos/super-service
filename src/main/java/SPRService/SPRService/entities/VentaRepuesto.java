package SPRService.SPRService.entities;

import jakarta.persistence.*;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TODO: ver si quitar los setters q no se usan rompe hibernate
//TODO: separar el código de la venta (id) y agregar código de factura con letras y números.
@Entity
@Table(name = "ventas_repuestos")
public class VentaRepuesto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_venta_repuestos")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fechaVenta;

    @Column(name = "monto_total", precision = 16, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "monto_faltante", precision = 16, scale = 2, nullable = false)
    private BigDecimal montoFaltante;

    @Column(nullable = false)
    private Boolean activo;

    //ENUM ESTADO DE VENTA
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "estado_venta")
    private EstadoVentaRepuesto estadoVenta;

    //RELACIÓN CON NOTA DE RETIRO
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_nota_retiro")
    private NotaRetiro notaRetiro;

    //RELACIÓN BI 1 A * CON PAGO
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_venta")
    private List<Pago> pagosList = new ArrayList<>();

    public VentaRepuesto() {
    }

    public VentaRepuesto(Long id, NotaRetiro notaRetiro, List<Pago> pagosList) {
        this.id = id;
        this.fechaVenta = LocalDate.now();
        this.activo = true;
        this.notaRetiro = notaRetiro;
        calculaMontoTotal();
        this.pagosList = pagosList;
        this.montoFaltante = this.montoTotal;
        calcularEstadoVenta();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
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

    public List<Pago> getPagosList() {
        return pagosList;
    }

    public void setPagosList(List<Pago> pagosList) {
        this.pagosList = pagosList;
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

    public void cancelarVenta(boolean restablecerStocks) {
        this.activo = Boolean.FALSE;
        this.estadoVenta = EstadoVentaRepuesto.CANCELADO;
        for (Pago p : this.pagosList){
            p.cancelar();
        }
        if (restablecerStocks) this.notaRetiro.cancelarNota();
    }

    public void asociarPago(Pago pago) {
        this.getPagosList().add(pago);
        pago.setVentaRepuesto(this);

        this.montoFaltante = this.montoFaltante.subtract(pago.getMontoPagado());
        calcularEstadoVenta();
        if (this.montoFaltante.compareTo(BigDecimal.ZERO) < 0) {
            this.montoFaltante = BigDecimal.ZERO;
        }
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
        if (this.getPagosList() == null) {
            this.setEstadoVenta(EstadoVentaRepuesto.PRESUPUESTANDO);
        } else {
            if (this.montoFaltante.compareTo(BigDecimal.ZERO) <= 0) {
                this.estadoVenta = EstadoVentaRepuesto.PAGADO;
            } else {
                this.estadoVenta = EstadoVentaRepuesto.PENDIENTE_PAGO;
            }
        }
    }
}
