package SPRService.SPRService.entities;

import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//todo: agregarle usuario que la registra
@Entity
@Table(name = "services")
public class Service implements Serializable, Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_service")
    private Long id;

    @Column(name = "monto_total", precision = 16, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "monto_faltante", precision = 16, scale = 2, nullable = false)
    private BigDecimal montoFaltante;

    @Column(nullable = false)
    private LocalDateTime fechaCarga;

    @Column(nullable = false)
    private LocalDateTime fechaEntrega;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "estado_service")
    private EstadoService estadoService;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PrioridadService prioridad;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "fk_cliente")
    private Cliente cliente;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "fk_orden")
    private Orden orden;

    @OneToMany(mappedBy = "service", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Pago> pagos = new HashSet<>();

    public Service() {
        this.fechaCarga = LocalDateTime.now();
    }

    public Service(LocalDateTime fechaEntrega, PrioridadService prioridad,
                   Cliente cliente, Orden orden) {
        this.fechaCarga = LocalDateTime.now();
        this.fechaEntrega = fechaEntrega;
        this.estadoService = EstadoService.PENDIENTE;
        this.prioridad = prioridad;
        asignarCliente(cliente);
        asignarOrden(orden);
        actualizarMontos();
    }

    public void asignarCliente(Cliente c) {
        this.cliente = c;
        c.getServices().add(this);
    }

    public void asignarOrden(Orden o) {
        this.orden = o;
        o.setService(this);
    }

    @Override
    public void asociarPago(Pago p) {
        this.pagos.add(p);
        p.setService(this);
        if (this.montoFaltante != null) {
            this.montoFaltante = this.montoFaltante.subtract(p.getMontoPagado());
            if (this.montoFaltante.compareTo(BigDecimal.ZERO) < 0) {
                this.montoFaltante = BigDecimal.ZERO;
            }
        }
        calcularEstadoSaldo();
    }

    private void calcularEstadoSaldo() {
        if (pagos != null && !pagos.isEmpty()) {
            if (this.montoFaltante.compareTo(BigDecimal.ZERO) <= 0) {
                estadoService = EstadoService.PAGADO;
            } else {
                estadoService = EstadoService.PAGO_PENDIENTE;
            }
        }
    }

    public void actualizarMontos() {
        if (this.orden != null) {
            // 1. Actualizar el Monto Total sumando trabajos y repuestos de la orden
            this.montoTotal = this.orden.getTotalTrabajos().add(this.orden.getTotalRepuestos());
        }

        BigDecimal totalPagado = BigDecimal.ZERO;
        if (this.pagos != null) {
            for (Pago p : this.pagos) {
                totalPagado = totalPagado.add(p.getMontoPagado());
            }
        }
        this.montoFaltante = this.montoTotal.subtract(totalPagado);

        if (this.montoFaltante.compareTo(BigDecimal.ZERO) < 0) {
            this.montoFaltante = BigDecimal.ZERO;
        }

        calcularEstadoSaldo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EstadoService getEstadoService() {
        return estadoService;
    }

    public void setEstadoService(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    public PrioridadService getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadService prioridad) {
        this.prioridad = prioridad;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", montoTotal=" + montoTotal +
                ", montoFaltante=" + montoFaltante +
                ", fechaCarga=" + fechaCarga +
                ", fechaEntrega=" + fechaEntrega +
                ", estadoService=" + estadoService +
                ", prioridad=" + prioridad +
                '}';
    }
}