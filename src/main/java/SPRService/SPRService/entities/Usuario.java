package SPRService.SPRService.entities;

import jakarta.persistence.*;
import SPRService.SPRService.enums.PrivilegioUsuario;

import java.io.Serializable;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_usuario")
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(value = EnumType.STRING)
    @Column(name = "privilegio")
    private PrivilegioUsuario privilegio;

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String password, PrivilegioUsuario privilegio) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
        this.privilegio = privilegio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PrivilegioUsuario getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(PrivilegioUsuario privilegio) {
        this.privilegio = privilegio;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nombre=" + nombre + ", password=" + password + ", privilegio=" + privilegio + '}';
    }
    
    
    
}
