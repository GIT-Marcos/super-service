package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notas_retiros")
public class NotaRetiro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_nota_retiro")
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_uso", nullable = false)
    private TipoUsoRetiro tipoUso;

    @Column(nullable = false)
    private Boolean activo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_nota_retiro")
    private List<DetalleRetiro> detalleRetiroList = new ArrayList<>();

    public NotaRetiro() {
        this.activo = Boolean.TRUE;
    }

    public NotaRetiro(Long id, TipoUsoRetiro tipoUso, List<DetalleRetiro> detalleRetiroList) {
        this.id = id;
        this.fecha = LocalDate.now();
        this.tipoUso = tipoUso;
        this.activo = Boolean.TRUE;
        this.detalleRetiroList = detalleRetiroList;
    }

    public void agregarDetalle(List<DetalleRetiro> detalleRetiros) {
        if (detalleRetiros != null) {
            for (DetalleRetiro d : detalleRetiros) {
                // Evitar duplicados al cargar
                if (!this.detalleRetiroList.contains(d)) {
                    this.detalleRetiroList.add(d);
                }
            }
        }
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
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

    public List<DetalleRetiro> getDetallesRetiroList() {
        return detalleRetiroList;
    }

    public void setDetallesRetiroList(List<DetalleRetiro> detallesRetiro) {
        this.detalleRetiroList = detallesRetiro;
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
        SERVICE
    }
}
