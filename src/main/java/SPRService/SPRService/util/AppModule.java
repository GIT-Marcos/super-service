package SPRService.SPRService.util;

import SPRService.SPRService.viewModels.DepositoViewModel;
import SPRService.SPRService.viewModels.VehiculoVM;
import SPRService.SPRService.navigation.WizardStateProvider;
import com.google.inject.*;
import javafx.fxml.FXMLLoader;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WizardStateProvider.class).in(Scopes.SINGLETON);
        bind(VehiculoVM.class);
        bind(DepositoViewModel.class);
    }

    // Provee un FXMLLoader ya configurado con la fábrica de controladores de Guice.
    // FXMLLoader en sí no es un singleton, así que cada vez que se inyecte
    // un Provider<FXMLLoader>, Guice llamará a este para crear uno nuevo.
    // JavaFx necesita un constructor vacío en el controlador, esto le indica otra forma de hacerlo.
    @Provides
    FXMLLoader provideFxmlLoader(Injector injector) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(injector::getInstance);
        return loader;
    }

}
