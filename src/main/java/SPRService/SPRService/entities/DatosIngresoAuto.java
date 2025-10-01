/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SPRService.SPRService.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

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
    
    @Column(name = "obs_ingreso_auto")
    private String observacionesIngreso;

    public DatosIngresoAuto() {
    }

    public DatosIngresoAuto(Long id, Double kilometraje, Double combustible, String observacionesIngreso) {
        this.id = id;
        this.kilometraje = kilometraje;
        this.combustible = combustible;
        this.observacionesIngreso = observacionesIngreso;
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

    public String getObservacionesIngreso() {
        return observacionesIngreso;
    }

    public void setObservacionesIngreso(String observacionesIngreso) {
        this.observacionesIngreso = observacionesIngreso;
    }

    @Override
    public String toString() {
        return "DatosIngresoAuto{" + "id=" + id + ", kilometraje=" + kilometraje + ", combustible=" + combustible + ", observacionesIngreso=" + observacionesIngreso + '}';
    }
    
    
}
