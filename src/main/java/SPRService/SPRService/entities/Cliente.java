package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_cliente")
    private Long id;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private Boolean activo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_contacto_cliente", nullable = false)
    private DatosContacto contactosCliente;

    @OneToMany(mappedBy = "cliente")
    private List<Vehiculo> vehiculos = new ArrayList<>();

    //RELACIÓN BI 1 A * CON SERVICE
//    @OneToMany(mappedBy = "cliente")
//    private List<Service> services = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(Long id, String dni, String nombre, String apellido, DatosContacto contactosCliente,
                   List<Vehiculo> vehiculos/*, List<Service> services*/) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = Boolean.TRUE;
        this.contactosCliente = contactosCliente;
        this.vehiculos = vehiculos;
//        this.services = services;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public DatosContacto getContactosCliente() {
        return contactosCliente;
    }

    public void setContactosCliente(DatosContacto contactosCliente) {
        this.contactosCliente = contactosCliente;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

//    public List<Service> getServices() {
//        return services;
//    }
//
//    public void setServices(List<Service> services) {
//        this.services = services;
//    }


    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", activo=" + activo +
                '}';
    }
}
