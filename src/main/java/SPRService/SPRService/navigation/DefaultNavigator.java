package SPRService.SPRService.navigation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultNavigator implements Navigator {

    private Pane contentPane;
    private Window ownerWindow;
    private final Provider<FXMLLoader> fxmlLoaderProvider;

    // Caché para las vistas (Node) y sus controladores
    private final Map<Views, Node> viewCache = new HashMap<>();
    private final Map<Views, Object> controllerCache = new HashMap<>();

    @Inject // ¡Inyección por constructor!
    public DefaultNavigator(Provider<FXMLLoader> fxmlLoaderProvider) {
        this.fxmlLoaderProvider = fxmlLoaderProvider;
    }

    @Override
    public void setOwnerWindow(Window window) {
        this.ownerWindow = window;
    }

    @Override
    public void bind(Pane contentPane) {
        this.contentPane = contentPane;
    }

    @Override
    public void navigateTo(Views view) {
        navigateTo(view, null);
    }

    @Override
    public <T> void navigateTo(Views view, T data) {
        try {
            if (!viewCache.containsKey(view)) {
                loadView(view);
            }
            contentPane.getChildren().setAll(viewCache.get(view));
            passDataToController(view, data);
        } catch (IOException e) {
            throw new RuntimeException("Fallo al navegar a la vista: " + view, e);
        }
    }

    @Override
    public <T, R> Optional<R> openModal(Views view, String title, T data) {
        try {
            // Obtenemos un nuevo loader configurado desde el proveedor
            FXMLLoader loader = fxmlLoaderProvider.get();
            loader.setLocation(getClass().getResource(view.getFxmlPath()));

            Parent root = loader.load();
            Object controller = loader.getController();

            // Pasar los datos de entrada al controlador del modal
            passDataToController(controller, data);

            Stage modalStage = new Stage();
            modalStage.setTitle(title);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(this.ownerWindow);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            // Después del cierre, verificamos si el controlador puede devolver un resultado
            if (controller instanceof ModalController) {
                // El cast aquí es seguro gracias al patrón.
                return ((ModalController<R>) controller).getResult();
            }

        } catch (IOException e) {
            throw new NavigationException("Error al abrir la ventana modal: " + view, e);
        }

        // Si el controlador no es IModalController o si hubo un error, devolvemos un Optional vacío.
        return Optional.empty();
    }

    @Override
    public Optional<Object> getControllerFor(Views view) {
        return Optional.ofNullable(controllerCache.get(view));
    }

    private <T> void passDataToController(Views view, T data) {
        if (data == null) {
            return; // No hay datos que pasar
        }

        Object controller = controllerCache.get(view);

        if (controller == null) {
            // Esto podría ocurrir si la vista no se cargó correctamente.
            System.err.println("Advertencia: Se intentó pasar datos a un controlador nulo para la vista " + view);
            return;
        }

        if (controller instanceof DataReceiver) {
            try {
                // Hacemos el cast. La advertencia de "unchecked cast" es inevitable
                // con genéricos, pero el patrón la hace segura.
                ((DataReceiver<T>) controller).receiveData(data);
            } catch (ClassCastException e) {
                // Este error es muy útil para depuración. Significa que el tipo de dato enviado (T)
                // no coincide con el tipo que el IDataReceiver<T> esperaba.
                throw new NavigationException(
                        "Error de tipo al pasar datos a " + controller.getClass().getSimpleName() +
                                ". Se esperaba un tipo compatible con el IDataReceiver, pero se recibió " +
                                data.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * Sobrecarga de passDataToController para usar con controladores
     * que no están en el caché (como los de los modales).
     */
    private <T> void passDataToController(Object controller, T data) {
        if (data != null && controller instanceof DataReceiver) {
            ((DataReceiver<T>) controller).receiveData(data);
        }
    }

    private void loadView(Views view) throws IOException {
        // Obtenemos un nuevo loader configurado desde el proveedor
        FXMLLoader loader = fxmlLoaderProvider.get();
        loader.setLocation(getClass().getResource(view.getFxmlPath()));

        Node viewNode = loader.load();
        viewCache.put(view, viewNode);
        controllerCache.put(view, loader.getController());
    }

    private void fadeTransition(Node newNode, Node oldNode) {
        if (oldNode != null) {
            oldNode.setOpacity(1); // Asegúrate de que esté completamente visible antes de empezar
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), oldNode);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                contentPane.getChildren().setAll(newNode);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), newNode);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
            fadeOut.play();
        } else {
            // Si no hay nodo anterior, simplemente muestra el nuevo con una transición de fade
            newNode.setOpacity(0);
            contentPane.getChildren().setAll(newNode);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), newNode);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
    }

    /**
     * Excepción personalizada para errores de navegación.
     */
    public static class NavigationException extends RuntimeException {
        public NavigationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
