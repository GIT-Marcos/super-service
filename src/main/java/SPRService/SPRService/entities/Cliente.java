package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private Boolean activo = true;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_contacto_cliente", nullable = false)
    private DatosContacto contactosCliente;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "cliente_vehiculo",
            joinColumns = @JoinColumn(name = "pk_cliente"),
            inverseJoinColumns = @JoinColumn(name = "pk_vehiculo"))
    private Set<Vehiculo> vehiculos = new HashSet<>();

    @OneToMany(mappedBy = "cliente")
    private Set<VentaRepuesto> ventas = new HashSet<>();

    @OneToMany(mappedBy = "cliente")
    private Set<Service> services = new HashSet<>();

    public Cliente() {
    }

    //TODO: como en este caso, quitar relaciones de los constructores; conviene dejar las 1-1 obligatorias
    // usando un helper en el dueño SI ES BI-DIRECCIONAL.
    // Que el dueño o padre asocie al otro con helper. Inicializar los atributos booleanos de estado en su declaración.
    public Cliente(Long id, String dni, String nombre, String apellido, DatosContacto contactosCliente) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.contactosCliente = contactosCliente;
    }

    public void asociarVehiculo(Vehiculo v) {
        if (v != null) {
            this.vehiculos.add(v);
            v.getClientes().add(this);
        }
    }

    public void asociarVehiculo(Set<Vehiculo> vSet) {
        if (vSet == null)
            vSet = new HashSet<>();

        vSet.forEach(this::asociarVehiculo);
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

    public Set<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(Set<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public Set<VentaRepuesto> getVentas() {
        return ventas;
    }

    public void setVentas(Set<VentaRepuesto> ventas) {
        this.ventas = ventas;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(dni, cliente.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dni);
    }
}
