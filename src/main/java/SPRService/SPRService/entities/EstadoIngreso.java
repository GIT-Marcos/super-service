package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "estado_ingresos")
public class EstadoIngreso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_estado_ingreso")
    private Long id;

    @Column(length = 500)
    private String observaciones;

    @Column(length = 500)
    private String inventario;

    @Column(nullable = false, precision = 2)
    private Integer kilometraje;

    @Column(nullable = false)
    private Integer combustible;

    public EstadoIngreso() {
    }

    public EstadoIngreso(Long id, String observaciones, String inventario, Integer kilometraje, Integer combustible) {
        this.id = id;
        this.observaciones = observaciones;
        this.inventario = inventario;
        this.kilometraje = kilometraje;
        this.combustible = combustible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getInventario() {
        return inventario;
    }

    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Integer kilometraje) {
        this.kilometraje = kilometraje;
    }

    public Integer getCombustible() {
        return combustible;
    }

    public void setCombustible(Integer combustible) {
        this.combustible = combustible;
    }

    @Override
    public String toString() {
        return "EstadoIngreso{" +
                "id=" + id +
                ", observaciones='" + observaciones + '\'' +
                ", inventario='" + inventario + '\'' +
                ", kilometraje=" + kilometraje +
                ", combustible=" + combustible +
                '}';
    }
}
