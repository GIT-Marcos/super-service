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

    @Column(nullable = false)
    private Boolean activo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_nota_retiro")
    private List<DetalleRetiro> detalleRetiroList = new ArrayList<>();

    public NotaRetiro() {
    }

    public NotaRetiro(Long id, List<DetalleRetiro> detalleRetiroList) {
        this.id = id;
        this.fecha = LocalDate.now();
        this.activo = Boolean.TRUE;
        this.detalleRetiroList = detalleRetiroList;
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
     * Una nota de retiro representa una cantidad de stock que se ha restado para fines comerciales. Si la nota es
     * cancelada, las cantidades RETIRADAS se deben restablecer.
     * Usado cuando se debe cancelar una nota de retiro por cualquier motivo. Restablece los existentes de stock.
     */
    public void cancelarNota() {
        this.activo = Boolean.FALSE;
        for (DetalleRetiro d : this.detalleRetiroList) {
            d.devolverStock();
        }
    }
}
