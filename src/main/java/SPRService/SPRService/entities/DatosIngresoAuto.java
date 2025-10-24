package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "datos_ingreso_autos")
public class DatosIngresoAuto implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_datos_ingreso")
    private Long id;

    @Column(nullable = false)
    private Double kilometraje;
    
    @Column(nullable = false)
    private Double combustible;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "inventario", joinColumns = @JoinColumn(name = "pk_inventario"))
    private Set<String> inventario = new HashSet<>();

    public DatosIngresoAuto() {
    }

    public DatosIngresoAuto(Long id, Double kilometraje, Double combustible, Set<String> inventario) {
        this.id = id;
        this.kilometraje = kilometraje;
        this.combustible = combustible;
        this.inventario = inventario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Double kilometraje) {
        this.kilometraje = kilometraje;
    }

    public Double getCombustible() {
        return combustible;
    }

    public void setCombustible(Double combustible) {
        this.combustible = combustible;
    }

    public Set<String> getInventario() {
        return inventario;
    }

    public void setInventario(Set<String> inventario) {
        this.inventario = inventario;
    }

    @Override
    public String toString() {
        return "DatosIngresoAuto{" +
                "id=" + id +
                ", kilometraje=" + kilometraje +
                ", combustible=" + combustible +
                ", inventario=" + inventario +
                '}';
    }
}
