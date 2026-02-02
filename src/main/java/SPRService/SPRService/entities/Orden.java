package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_nota_retiro")
    private NotaRetiro notaRetiro;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Trabajo> trabajos = new HashSet<>();

    @OneToOne(mappedBy = "orden")
    private Service service;

    protected Orden() {
    }

    public Orden(String motivoIngreso, String informeTecnico, EstadoIngreso estadoIngreso) {
        this.motivoIngreso = motivoIngreso;
        this.informeTecnico = informeTecnico;
        this.estadoIngreso = estadoIngreso;
    }

    @PrePersist
    public void prePersist() {
        this.id = null;
        if (this.trabajos == null) {
            this.trabajos = new HashSet<>();
        }
    }

    public void asignarNota(NotaRetiro nota) {
        this.notaRetiro = nota;
        actualizarTotalRepuestos();
        if (this.service != null) {
            this.service.recalcularMontos();
        }
    }

    public void asociarVehiculo(Vehiculo v) {
        if (v != null) {
            this.setVehiculo(v);
            v.getOrdenes().add(this);
        }
    }

    public void agregarTrabajos(Set<Trabajo> trabajos) {
        if (trabajos != null) {
            boolean huboCambios = false;
            for (Trabajo t : trabajos) {
                // Evitar duplicados al modificar
                if (!this.trabajos.contains(t)) {
                    this.trabajos.add(t);
                    this.totalTrabajos = this.totalTrabajos.add(t.getPrecio());
                    huboCambios = true;
                }
            }
            if (huboCambios && this.service != null) {
                this.service.recalcularMontos();
            }
        }
    }

    public void agregarTrabajos(Trabajo t) {
        if (t != null) {
            agregarTrabajos(Set.of(t));
        }
    }

    public void quitarTrabajo(Trabajo t) {
        if (t != null && this.trabajos.contains(t)) {
            this.trabajos.remove(t);

            this.totalTrabajos = this.totalTrabajos.subtract(t.getPrecio());
            if (this.totalTrabajos.compareTo(BigDecimal.ZERO) < 0) {
                this.totalTrabajos = BigDecimal.ZERO;
            }

            if (this.service != null) {
                this.service.recalcularMontos();
            }
        }
    }

    public void agregarRepuestos(List<DetalleRetiro> detalles) {
        if (detalles != null) {
            this.notaRetiro.agregarDetalle(detalles);
            actualizarTotalRepuestos();
            if (this.service != null) {
                this.service.recalcularMontos();
            }
        }
    }

    public void quitarRepuesto(DetalleRetiro detalle) {
        if (detalle != null && this.notaRetiro != null) {
            boolean eliminado = this.notaRetiro.getDetallesRetiroList().remove(detalle);

            if (eliminado) {
                actualizarTotalRepuestos();

                if (this.service != null) {
                    this.service.recalcularMontos();
                }
            }
        }
    }

    private void actualizarTotalRepuestos() {
        if (this.notaRetiro != null) {
            totalRepuestos = BigDecimal.ZERO;
            for (DetalleRetiro d : notaRetiro.getDetallesRetiroList()) {
                totalRepuestos = totalRepuestos.add(d.getSubTotal());
            }
        } else {
            totalRepuestos = BigDecimal.ZERO;
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
        actualizarTotalRepuestos();
        if (this.service != null) {
            this.service.recalcularMontos();
        }
    }

    public Set<Trabajo> getTrabajos() {
        return trabajos;
    }

    /**
     * No usar. Usar operaciónes específicas.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orden orden = (Orden) o;

        return id != null && id.equals(orden.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
