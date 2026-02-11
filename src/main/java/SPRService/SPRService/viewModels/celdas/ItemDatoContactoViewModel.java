package SPRService.SPRService.viewModels.celdas;

import SPRService.SPRService.entities.DatosContacto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ViewModel para representar un dato de contacto individual en la UI
 */
public class ItemDatoContactoViewModel {

    public enum TipoContacto {
        EMAIL("Email", "✉"),
        TELEFONO("Teléfono", "📞");

        private final String nombre;
        private final String icono;

        TipoContacto(String nombre, String icono) {
            this.nombre = nombre;
            this.icono = icono;
        }

        public String getNombre() {
            return nombre;
        }

        public String getIcono() {
            return icono;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private TipoContacto tipo;
    private String valor;

    public ItemDatoContactoViewModel(TipoContacto tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    // Getters y Setters
    public TipoContacto getTipo() {
        return tipo;
    }

    public void setTipo(TipoContacto tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public boolean isEmail() {
        return tipo == TipoContacto.EMAIL;
    }

    public boolean isTelefono() {
        return tipo == TipoContacto.TELEFONO;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemDatoContactoViewModel that = (ItemDatoContactoViewModel) obj;
        return tipo == that.tipo && valor.equalsIgnoreCase(that.valor);
    }

    @Override
    public int hashCode() {
        return tipo.hashCode() + valor.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", tipo.getNombre(), valor);
    }

    // ==================== MÉTODOS DE CONVERSIÓN ====================

    /**
     * Convierte una lista de ItemDatoContactoViewModel a entidad DatosContacto
     */
    public static DatosContacto toEntity(List<ItemDatoContactoViewModel> items) {
        DatosContacto entity = new DatosContacto();

        for (ItemDatoContactoViewModel item : items) {
            if (item.isEmail()) {
                entity.getEmailSet().add(item.getValor());
            } else {
                entity.getNroTelefonoSet().add(item.getValor());
            }
        }

        return entity;
    }

    /**
     * Convierte una entidad DatosContacto a lista de ItemDatoContactoViewModel
     */
    public static List<ItemDatoContactoViewModel> fromEntity(DatosContacto entity) {
        List<ItemDatoContactoViewModel> items = new ArrayList<>();

        if (entity == null) return items;

        // Convertir emails
        Set<String> emails = entity.getEmailSet();
        if (emails != null) {
            for (String email : emails) {
                items.add(new ItemDatoContactoViewModel(TipoContacto.EMAIL, email));
            }
        }

        // Convertir teléfonos
        Set<String> telefonos = entity.getNroTelefonoSet();
        if (telefonos != null) {
            for (String telefono : telefonos) {
                items.add(new ItemDatoContactoViewModel(TipoContacto.TELEFONO, telefono));
            }
        }

        return items;
    }

    /**
     * Actualiza una entidad existente con los items de la lista
     */
    public static void updateEntity(DatosContacto entity, List<ItemDatoContactoViewModel> items) {
        // Limpiar sets existentes
        entity.getEmailSet().clear();
        entity.getNroTelefonoSet().clear();

        // Agregar nuevos valores
        for (ItemDatoContactoViewModel item : items) {
            if (item.isEmail()) {
                entity.getEmailSet().add(item.getValor());
            } else {
                entity.getNroTelefonoSet().add(item.getValor());
            }
        }
    }
}