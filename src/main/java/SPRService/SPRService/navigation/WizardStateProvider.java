package SPRService.SPRService.navigation;

import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.viewModels.VehiculoVM;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Tiene como responsabilidad mantener la instancia del ViewModel durante el wizard activo.
 */
@Singleton
public class WizardStateProvider {
    private VehiculoVM currentViewModel;
    private final Provider<VehiculoVM> viewModelProvider;

    // Inyectamos un Provider<T> en lugar de la T directamente.
    // Esto nos permite crear NUEVAS instancias de VehiculoViewModel cuando queramos.
    @Inject
    public WizardStateProvider(Provider<VehiculoVM> viewModelProvider) {
        this.viewModelProvider = viewModelProvider;
    }

    /**
     * Inicia un nuevo flujo de wizard. Crea una instancia FRESCA del ViewModel.
     */
    public void startNewVehicleWizard() {
        this.currentViewModel = viewModelProvider.get(); // Guice crea una nueva instancia
        this.currentViewModel.clear(); // Nos aseguramos de que esté limpio
    }

    /**
     * Inicia un nuevo flujo de wizard para EDITAR un vehículo existente.
     * Crea una instancia FRESCA del ViewModel y la carga con datos.
     *
     * @param vehiculoToEdit La entidad Vehiculo con los datos a editar.
     */
    public void startEditVehicleWizard(Vehiculo vehiculoToEdit) {
        if (vehiculoToEdit == null) {
            throw new IllegalArgumentException("El vehículo a editar no puede ser nulo.");
        }
        this.currentViewModel = viewModelProvider.get(); // Guice crea una nueva instancia
        this.currentViewModel.loadFromEntity(vehiculoToEdit); // ¡El paso clave!
    }

    /**
     * Devuelve la instancia del ViewModel para el wizard actual.
     *
     * @return El ViewModel activo.
     * @throws IllegalStateException si no se ha iniciado ningún wizard.
     */
    public VehiculoVM getViewModel() {
        if (currentViewModel == null) {
            throw new IllegalStateException("El wizard no se ha iniciado. Llama a startNewVehicleWizard() primero.");
        }
        return currentViewModel;
    }

    /**
     * Limpia el estado cuando el wizard termina (se completa o se cancela).
     */
    public void endWizard() {
        this.currentViewModel = null; // Permite que el recolector de basura lo elimine.
    }
}
