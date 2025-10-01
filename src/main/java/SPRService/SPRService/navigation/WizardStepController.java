package SPRService.SPRService.navigation;

import java.util.function.BooleanSupplier;

public interface WizardStepController {
    /**
     * Permite al controlador del paso exponer su lógica de validación.
     * @return Un BooleanSupplier que, al ser llamado, devuelve true si el paso es válido.
     */
    BooleanSupplier getValidador();

    /**
     * Muestra los errores de validación específicos de este paso al usuario.
     * Este se llama desde el orquestador del asistente cuando getValidador() devuelve false.
     */
    void mostrarErrores();
}
