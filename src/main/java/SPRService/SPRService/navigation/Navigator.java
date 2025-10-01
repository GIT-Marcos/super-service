package SPRService.SPRService.navigation;

import javafx.scene.layout.Pane;

import java.util.Optional;

public interface Navigator {

    void bind(Pane contentPane);

    void setOwnerWindow(javafx.stage.Window window);

    void navigateTo(Views view);

    <T> void navigateTo(Views view, T data);

    <T, R> Optional<R> openModal(Views view, String title, T data);

    /**
     * Obtiene el controlador asociado a una vista que ya ha sido cargada
     * y está en el caché del navegador.
     *
     * @param view La vista cuyo controlador se desea obtener.
     * @return Un Optional que contiene el controlador si existe, o un Optional vacío.
     */
    Optional<Object> getControllerFor(Views view);
}
