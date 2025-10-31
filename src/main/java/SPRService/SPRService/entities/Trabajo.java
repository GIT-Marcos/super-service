package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "trabajos")
public class Trabajo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_trabajo")
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    public Trabajo() {
    }

    public Trabajo(Long id, String descripcion, BigDecimal precio) {
        this.id = id;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Trabajo{" +
                "id=" + id +
                ", nombreTrabajo='" + descripcion + '\'' +
                ", precio=" + precio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trabajo trabajo)) return false;
        return Objects.equals(descripcion, trabajo.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descripcion);
    }
}
