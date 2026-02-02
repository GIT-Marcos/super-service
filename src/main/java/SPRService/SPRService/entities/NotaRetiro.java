package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notas_retiros")
public class NotaRetiro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_nota_retiro")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_uso", nullable = false)
    private TipoUsoRetiro tipoUso;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_nota_retiro")
    private Set<DetalleRetiro> detalleRetiro = new HashSet<>();

    protected NotaRetiro() {
    }

    public NotaRetiro(TipoUsoRetiro tipoUso, Set<DetalleRetiro> detalleRetiroList) {
        this.tipoUso = tipoUso;
        agregarDetalle(detalleRetiroList);
    }

    @PrePersist
    public void prePersist() {
        this.id = null;
        this.fecha = LocalDateTime.now();
        this.activo = true;
        if (this.detalleRetiro == null) this.detalleRetiro = new HashSet<>();
    }

    public void agregarDetalle(DetalleRetiro d) {
        if (d != null) this.detalleRetiro.add(d);
    }

    public void agregarDetalle(Set<DetalleRetiro> detalleRetiros) {
        if (detalleRetiros == null) detalleRetiros = new HashSet<>();

        detalleRetiros.forEach(this::agregarDetalle);
    }

    /**
     * Una nota de retiro representa una cantidad de stock que se ha restado para fines comerciales. Si la nota es
     * cancelada, las cantidades RETIRADAS si o si se deben restablecer.
     */
    public void cancelarNota() {
        this.activo = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoUsoRetiro getTipoUso() {
        return tipoUso;
    }

    public void setTipoUso(TipoUsoRetiro tipoUso) {
        this.tipoUso = tipoUso;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Set<DetalleRetiro> getDetallesRetiro() {
        return detalleRetiro;
    }

    public void setDetallesRetiro(Set<DetalleRetiro> detallesRetiro) {
        this.detalleRetiro = detallesRetiro;
    }

    @Override
    public String toString() {
        return "NotaRetiro{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", activo=" + activo +
                '}';
    }

    /**
     * Indica si el retiro de repuestos se realizó para una Venta directa o para un Service.
     */
    public enum TipoUsoRetiro {
        VENTA,
        SERVICE,
        OTRO
    }
}
