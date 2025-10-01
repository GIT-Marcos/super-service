package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "marcas_vehiculos")
public class MarcaVehiculo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_marca_vehiculo")
    private Long id;

    @Column(name = "nombre_marca_vehiculo", nullable = false, unique = true)
    private String nombreMarca;

    @Column(name = "ruta_logo")
    private String rutaLogo;

    @OneToMany(mappedBy = "marcaVehiculo", cascade = CascadeType.MERGE, orphanRemoval = true)
    @OrderBy("nombreModelo ASC")
    private List<ModeloVehiculo> modelosVehiculos = new ArrayList<>();

    public MarcaVehiculo() {
    }

    public MarcaVehiculo(Long id, String nombreMarca, List<ModeloVehiculo> modelosVehiculos, String rutaLogo) {
        this.id = id;
        this.nombreMarca = nombreMarca;
        this.modelosVehiculos = modelosVehiculos;
        this.rutaLogo = rutaLogo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public List<ModeloVehiculo> getModelosVehiculos() {
        return modelosVehiculos;
    }

    public void setModelosVehiculos(List<ModeloVehiculo> modelosVehiculos) {
        this.modelosVehiculos = modelosVehiculos;
    }

    public String getRutaLogo() {
        return rutaLogo;
    }

    public void setRutaLogo(String rutaLogo) {
        this.rutaLogo = rutaLogo;
    }

    @Override
    public String toString() {
        return "MarcaVehiculo{" +
                "id=" + id +
                ", nombreMarca='" + nombreMarca + '\'' +
                ", rutaLogo='" + rutaLogo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MarcaVehiculo that = (MarcaVehiculo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
