package SPRService.SPRService.navigation;

import java.util.Optional;

/**
 * Si la ventana modal devuelve algo debe usar esta interfaz.
 * @param <R> Tipo de dato que devuelve.
 */
public interface ModalController<R>{
    Optional<R> getResult();
}
