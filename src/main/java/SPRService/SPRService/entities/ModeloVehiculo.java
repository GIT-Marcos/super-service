package SPRService.SPRService.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modelos_vehiculos")
public class ModeloVehiculo implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_modelo_vehiculo")
    private Long id;

    @Column(name = "nombre_modelo", nullable = false)
    private String nombreModelo;

    @Column(nullable = false)
    private Year anio;
    
    @Column(nullable = false)
    private Double cilindrada;
    
    @ManyToOne()
    @JoinColumn(nullable = false, name = "fk_marca")
    private MarcaVehiculo marcaVehiculo;
    
    @OneToMany(mappedBy = "modeloVehiculo")
    private List<Vehiculo> vehiculos = new ArrayList<>();

    public ModeloVehiculo() {
    }

    public ModeloVehiculo(Long id, String nombreModelo, Year anio, Double cilindrada,
            MarcaVehiculo marcaVehiculo, List<Vehiculo> vehiculos) {
        this.id = id;
        this.nombreModelo = nombreModelo;
        this.anio = anio;
        this.cilindrada = cilindrada;
        this.marcaVehiculo = marcaVehiculo;
        this.vehiculos = vehiculos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreModelo() {
        return nombreModelo;
    }

    public void setNombreModelo(String nombreModelo) {
        this.nombreModelo = nombreModelo;
    }

    public Year getAnio() {
        return anio;
    }

    public void setAnio(Year anio) {
        this.anio = anio;
    }

    public Double getCilindrada() {
        return cilindrada;
    }

    public void setCilindrada(Double cilindrada) {
        this.cilindrada = cilindrada;
    }

    public MarcaVehiculo getMarcaVehiculo() {
        return marcaVehiculo;
    }

    public void setMarcaVehiculo(MarcaVehiculo marcaVehiculo) {
        this.marcaVehiculo = marcaVehiculo;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    @Override
    public String toString() {
        return "ModeloVehiculo{" + "id=" + id + ", nombreModelo=" + nombreModelo + ", anio=" + anio + ", cilindrada=" + cilindrada + '}';
    }

    

   
    
    
}
