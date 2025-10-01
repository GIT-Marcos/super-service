package SPRService.SPRService;

import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.util.AppModule;
import SPRService.SPRService.util.persistence.PersistenceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Injector injector = Guice.createInjector(new AppModule(), new PersistenceModule());

        AppCoordinator coordinator = injector.getInstance(AppCoordinator.class);
        coordinator.start(primaryStage);

        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.start();
    }

    public static void main(String[] args) {
        launch();
    }
}