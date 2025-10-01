package SPRService.SPRService.navigation;

/**
 * Interfaz genérica para controladores que pueden recibir datos al ser navegados.
 * @param <T> El tipo de dato que el controlador espera recibir.
 */
public interface DataReceiver<T> {
    void receiveData(T data);
}
