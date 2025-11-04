package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

//todo: agregar tiempo estimado
@Entity
@Table(name = "ordenes")
public class Orden implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_orden")
    private Long id;

    @Column(length = 500, name = "motivo_ingreso")
    private String motivoIngreso;

    @Column(length = 500, name = "informe_tecnico")
    private String informeTecnico;

    @Column(nullable = false, name = "total_trabajos")
    private BigDecimal totalTrabajos = BigDecimal.ZERO;

    @Column(name = "total_repuestos")
    private BigDecimal totalRepuestos = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "fk_vehiculo")
    private Vehiculo vehiculo;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "fk_estado_ingreso")
    private EstadoIngreso estadoIngreso;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "fk_nota_retiro")
    private NotaRetiro notaRetiro;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Trabajo> trabajos = new HashSet<>();

    @OneToOne(mappedBy = "orden")
    private Service service;

    public Orden() {
    }

    public Orden(Long id, String motivoIngreso, String informeTecnico, BigDecimal totalTrabajos,
                 BigDecimal totalRepuestos, Vehiculo vehiculo, EstadoIngreso estadoIngreso,
                 NotaRetiro notaRetiro, Set<Trabajo> trabajos, Service service) {
        this.id = id;
        this.motivoIngreso = motivoIngreso;
        this.informeTecnico = informeTecnico;
        this.totalTrabajos = totalTrabajos;
        this.totalRepuestos = totalRepuestos;
        this.vehiculo = vehiculo;
        this.estadoIngreso = estadoIngreso;
        this.notaRetiro = notaRetiro;
        this.trabajos = trabajos;
        this.service = service;
    }

    public void agregarTrabajo(Trabajo t) {
        this.trabajos.add(t);
        this.totalTrabajos = this.totalTrabajos.add(t.getPrecio());
    }

    public void agregarTrabajos(Set<Trabajo> trabajos) {
        for (Trabajo t : trabajos) {
            agregarTrabajo(t);
        }
    }

    private void calcularTotalRepuestos(NotaRetiro n) {
        for (DetalleRetiro d : notaRetiro.getDetallesRetiroList()) {
            totalRepuestos = totalTrabajos.add(d.getSubTotal());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivoIngreso() {
        return motivoIngreso;
    }

    public void setMotivoIngreso(String motivoIngreso) {
        this.motivoIngreso = motivoIngreso;
    }

    public String getInformeTecnico() {
        return informeTecnico;
    }

    public void setInformeTecnico(String informeTecnico) {
        this.informeTecnico = informeTecnico;
    }

    public BigDecimal getTotalTrabajos() {
        return totalTrabajos;
    }

    public void setTotalTrabajos(BigDecimal totalTrabajos) {
        this.totalTrabajos = totalTrabajos;
    }

    public BigDecimal getTotalRepuestos() {
        return totalRepuestos;
    }

    public void setTotalRepuestos(BigDecimal totalRepuestos) {
        this.totalRepuestos = totalRepuestos;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public EstadoIngreso getEstadoIngreso() {
        return estadoIngreso;
    }

    public void setEstadoIngreso(EstadoIngreso estadoIngreso) {
        this.estadoIngreso = estadoIngreso;
    }

    public NotaRetiro getNotaRetiro() {
        return notaRetiro;
    }

    public void setNotaRetiro(NotaRetiro notaRetiro) {
        this.notaRetiro = notaRetiro;
        calcularTotalRepuestos(notaRetiro);
    }

    public Set<Trabajo> getTrabajos() {
        return trabajos;
    }

    /**
     * Usar operación específica: agregarTrabajo(Trabajo t)
     */
    public void setTrabajos(Set<Trabajo> trabajos) {
        this.trabajos = trabajos;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "Orden{" +
                "id=" + id +
                ", motivoIngreso='" + motivoIngreso + '\'' +
                ", informeTecnico='" + informeTecnico + '\'' +
                ", totalTrabajos=" + totalTrabajos +
                ", totalRepuestos=" + totalRepuestos +
                '}';
    }

}
