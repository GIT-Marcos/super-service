package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "marcas_repuestos")
public class MarcaRepuesto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_marca_repuesto")
    private Long id;

    @Column(name = "nombre_marca_repuesto", nullable = false, unique = true, length = 100)
    private String nombreMarca;

    @OneToMany(mappedBy = "marcaRepuesto")
    private Set<Repuesto> repuestos = new HashSet<>();

    public MarcaRepuesto() {
    }

    public MarcaRepuesto(Long id, String nombreMarca, Set<Repuesto> repuestos) {
        this.id = id;
        this.nombreMarca = nombreMarca;
        this.repuestos = repuestos;
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

    public Set<Repuesto> getRepuestos() {
        return repuestos;
    }

    public void setRepuestos(Set<Repuesto> repuestos) {
        this.repuestos = repuestos;
    }

    @Override
    public String toString() {
        return nombreMarca;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MarcaRepuesto that)) return false;
        return Objects.equals(nombreMarca, that.nombreMarca);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombreMarca);
    }
}
