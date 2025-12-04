package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ubicacion")
public class Ubicacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_ubicacion")
    private Long id;

    @Column(nullable = false, unique = true)
    private String ubicacion;

    @OneToMany(mappedBy = "ubicacion")
    private List<Stock> stocks = new ArrayList<>();

    public Ubicacion() {
    }

    public Ubicacion(Long id, String ubicacion, List<Stock> stocks) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.stocks = stocks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    @Override
    public String toString() {
        return "Ubicacion{" +
                "id=" + id +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ubicacion that = (Ubicacion) o;
        if (this.ubicacion == null || that.ubicacion == null) {
            return false;
        }
        return ubicacion.equals(that.ubicacion);
    }

    @Override
    public int hashCode() {
        // Si 'ubicacion' es null, se devuelve un valor predecible (por ejemplo, 0)
        // para cumplir con el contrato de hashCode.
        return ubicacion != null ? Objects.hash(ubicacion) : 0;
    }
}