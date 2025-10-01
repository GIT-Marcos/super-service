package SPRService.SPRService.navigation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

@Singleton
public class AppCoordinator {

    // Usamos Provider para obtener nuevas instancias de FXMLLoader
    private final Provider<FXMLLoader> fxmlLoaderProvider;
    private Stage primaryStage;
    private final Navigator mainNavigator;

//    @Inject
//    public AppCoordinator(Provider<FXMLLoader> fxmlLoaderProvider, Navigator mainNavigator) {
//        this.fxmlLoaderProvider = fxmlLoaderProvider;
//        this.mainNavigator = mainNavigator;
//    }

    @Inject
    public AppCoordinator(Provider<FXMLLoader> fxmlLoaderProvider) {
        this.fxmlLoaderProvider = fxmlLoaderProvider;
        this.mainNavigator = new DefaultNavigator(fxmlLoaderProvider);
    }

    public Navigator getMainNavigator() {
        return mainNavigator;
    }

    /**
     * Inicia el flujo de la aplicación, comenzando por la pantalla de login.
     *
     * @param primaryStage La ventana principal proporcionada por JavaFX.
     */
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScreen();
    }

    /**
     * Se llama desde el LoginController cuando el login es exitoso.
     * Cierra la ventana de login y lanza la ventana principal.
     */
    public void onLoginSuccess() {
        primaryStage.close(); // Cierra la ventana de login
        showMainScreen();
    }

    public void closeSesion() {
        primaryStage.close();
        showLoginScreen();
    }

    private void showLoginScreen() {
        try {
            FXMLLoader loader = fxmlLoaderProvider.get();
            loader.setLocation(Objects.requireNonNull(getClass().getResource(Views.LOGIN.getFxmlPath())));
            Parent root = loader.load();

            primaryStage.setTitle("Inicio de Sesión");
            primaryStage.setScene(new Scene(root));
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/icon.png")));
            primaryStage.getIcons().add(icon);

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista de login", e);
        }
    }

    private void showMainScreen() {
        try {
            Stage mainStage = new Stage();

            FXMLLoader loader = fxmlLoaderProvider.get();
            loader.setLocation(Objects.requireNonNull(getClass().getResource(Views.MAIN.getFxmlPath())));
            Parent root = loader.load();

            // ¡CONFIGURACIÓN CLAVE!
            // Le decimos al Navigator cuál es su ventana propietaria.
            // Esto es crucial para que los modales abiertos desde el Navigator
            // sepan quién es su "padre".
            mainNavigator.setOwnerWindow(mainStage);

            // La lógica de vinculación del Pane y la navegación inicial siguen
            // dentro de MainController.initialize(), lo cual es correcto.

            mainStage.setTitle("SuperService");
            mainStage.setScene(new Scene(root));
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/icon.png")));
            mainStage.getIcons().add(icon);

            mainStage.show();

        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista principal", e);
        }
    }
}

