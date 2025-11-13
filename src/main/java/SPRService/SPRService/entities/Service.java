package SPRService.SPRService.entities;

import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

//todo: agregarle usuario que la registra
@Entity
@Table(name = "services")
public class Service implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_service")
    private Long id;

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

    public Service() {
        this.fechaCarga = LocalDateTime.now();
    }

    public Service(LocalDateTime fechaEntrega, PrioridadService prioridad,
                   Cliente cliente, Orden orden) {
        this.fechaCarga = LocalDateTime.now();
        this.fechaEntrega = fechaEntrega;
        this.estadoService = EstadoService.PENDIENTE;
        this.prioridad = prioridad;
        this.cliente = cliente;
        this.orden = orden;
    }

    public void asignarCliente(Cliente c) {
        this.cliente = c;
        c.getServices().add(this);
    }

    public void asignarOrden(Orden o) {
        this.orden = o;
        o.setService(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                ", fechaInicio=" + fechaCarga +
                ", fechaFin=" + fechaEntrega +
                ", estadoService=" + estadoService +
                ", prioridad=" + prioridad +
                '}';
    }
}