package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private Boolean estado;

    @ManyToOne()
    @JoinColumn(nullable = false, name = "fk_modelo")
    private ModeloVehiculo modeloVehiculo;

    @ManyToOne()
    @JoinColumn(name = "fk_cliente")
    private Cliente cliente;

    @OneToMany(mappedBy = "vehiculo")
    private List<Orden> ordenes = new ArrayList<>();

    public Vehiculo() {
    }

    public Vehiculo(Long id, String patente, String nroChasis, String nroMotor, String color, Boolean estado,
                    ModeloVehiculo modeloVehiculo, Cliente cliente, List<Orden> ordenes) {
        this.id = id;
        this.patente = patente;
        this.nroChasis = nroChasis;
        this.nroMotor = nroMotor;
        this.color = color;
        this.fechaRegistro = LocalDate.now();
        this.estado = estado;
        this.modeloVehiculo = modeloVehiculo;
        this.cliente = cliente;
        asociarOrden(ordenes);
    }

    protected void asociarOrden(Orden o) {
        if (o != null) {
            this.ordenes.add(o);
            o.setVehiculo(this);
        }
    }

    protected void asociarOrden(List<Orden> oList) {
        if (oList == null)
            oList = new ArrayList<>();

        for (Orden o : oList) {
            asociarOrden(o);
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

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public List<Orden> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<Orden> ordenes) {
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
