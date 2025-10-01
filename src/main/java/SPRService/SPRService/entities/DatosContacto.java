package SPRService.SPRService.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "datos_contacto")
public class DatosContacto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_cliente")
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tel_contact", joinColumns = @JoinColumn(name = "tel_pk"))
    @Column(name = "tel_number")
    private Set<String> nroTelefonoSet = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "emails_contact", joinColumns = @JoinColumn(name = "contact_pk"))
    @Column(name = "email")
    private Set<String> emailSet = new HashSet<>();

    public DatosContacto() {
    }

    public DatosContacto(Long id, Set<String> nroTelefonoSet, Set<String> emailSet) {
        this.id = id;
        this.nroTelefonoSet = nroTelefonoSet;
        this.emailSet = emailSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getEmailSet() {
        return emailSet;
    }

    public void setEmailSet(Set<String> emailSet) {
        this.emailSet = emailSet;
    }

    public Set<String> getNroTelefonoSet() {
        return nroTelefonoSet;
    }

    public void setNroTelefonoSet(Set<String> nroTelefonoSet) {
        this.nroTelefonoSet = nroTelefonoSet;
    }

    @Override
    public String toString() {
        return "DatosContacto{" +
                "id=" + id +
                ", nroTelefonoSet=" + nroTelefonoSet +
                ", emailSet=" + emailSet +
                '}';
    }
}
