package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_ventas")
public class AuditoriaVenta implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_auditoria")
    private Long id;
    
    @Column(name = "tipo_registro", nullable = false)
    private String tipoRegistro;
    
    @Column(length = 250, nullable = false)
    private String moitivo;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    //RELACIÓN CON USUARIO
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;

    public AuditoriaVenta() {
    }

    public AuditoriaVenta(Long id, String tipoRegistro, String moitivo, LocalDateTime fechaRegistro, Usuario usuario) {
        this.id = id;
        this.tipoRegistro = tipoRegistro;
        this.moitivo = moitivo;
        this.fechaRegistro = fechaRegistro;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getMoitivo() {
        return moitivo;
    }

    public void setMoitivo(String moitivo) {
        this.moitivo = moitivo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "AuditoriaVenta{" + "id=" + id + ", tipoRegistro=" + tipoRegistro + ", moitivo=" + moitivo + ", fechaRegistro=" + fechaRegistro + '}';
    }
    
}
