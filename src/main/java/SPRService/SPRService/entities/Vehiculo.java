package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "vehiculos")
public class Vehiculo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_auto")
    private Long id;

    @Column(nullable = false, unique = true)
    private String patente;

    @Column(name = "nro_chasis")
    private String nroChasis;

    @Column(name = "nro_motor")
    private String nroMotor;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "fk_modelo")
    private ModeloVehiculo modeloVehiculo;

    @ManyToMany(mappedBy = "vehiculos")
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy = "vehiculo")
    private Set<Orden> ordenes = new HashSet<>();

    public Vehiculo() {
    }

    public Vehiculo(String patente, String nroChasis, String nroMotor, String color) {
        this.patente = patente;
        this.nroChasis = nroChasis;
        this.nroMotor = nroMotor;
        this.color = color;
    }

    @PrePersist
    public void prePersist() {
        this.id = null;
        this.estado = true;
        this.fechaRegistro = LocalDateTime.now();
        if (this.clientes == null) this.clientes = new HashSet<>();
        if (this.ordenes == null) this.ordenes = new HashSet<>();
    }

    public void asociarModelo(ModeloVehiculo m) {
        if (m != null) {
            this.modeloVehiculo = m;
            m.getVehiculos().add(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getNroChasis() {
        return nroChasis;
    }

    public void setNroChasis(String nroChasis) {
        this.nroChasis = nroChasis;
    }

    public String getNroMotor() {
        return nroMotor;
    }

    public void setNroMotor(String nroMotor) {
        this.nroMotor = nroMotor;
    }

    public String getColor() {
        return color;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ModeloVehiculo getModeloVehiculo() {
        return modeloVehiculo;
    }

    public void setModeloVehiculo(ModeloVehiculo modeloVehiculo) {
        this.modeloVehiculo = modeloVehiculo;
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Set<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Set<Orden> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(Set<Orden> ordenes) {
        this.ordenes = ordenes;
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "id=" + id +
                ", patente='" + patente + '\'' +
                ", nroChasis='" + nroChasis + '\'' +
                ", nroMotor='" + nroMotor + '\'' +
                ", color='" + color + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", estado=" + estado +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vehiculo vehiculo)) return false;
        return Objects.equals(patente, vehiculo.patente);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(patente);
    }
}
