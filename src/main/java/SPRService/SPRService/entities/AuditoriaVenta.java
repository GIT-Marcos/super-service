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
    private String motivo;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;

    protected AuditoriaVenta() {
    }

    public AuditoriaVenta(String tipoRegistro, String motivo, Usuario usuario) {
        this.tipoRegistro = tipoRegistro;
        this.motivo = motivo;
        this.usuario = usuario;
    }

    @PrePersist
    public void prePersist() {
        this.id = null;
        this.fechaRegistro = LocalDateTime.now();
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String moitivo) {
        this.motivo = moitivo;
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
        return "AuditoriaVenta{" + "id=" + id + ", tipoRegistro=" + tipoRegistro + ", moitivo=" + motivo + ", fechaRegistro=" + fechaRegistro + '}';
    }
    
}
