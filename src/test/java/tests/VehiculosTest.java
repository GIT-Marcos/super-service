package tests;

import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.services.ModeloVehiculoServ;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.persistence.PersistenceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class VehiculosTest {

    private Injector injector;
    private VehiculoServ vehiculoServ;
    private ModeloVehiculoServ modeloVehiculoServ;

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new PersistenceModule());
        injector.getInstance(PersistService.class).start();

        // Inyectamos los servicios necesarios
        this.vehiculoServ = injector.getInstance(VehiculoServ.class);
        this.modeloVehiculoServ = injector.getInstance(ModeloVehiculoServ.class);
    }

    @AfterEach
    void tearDown() {
        if (injector != null) {
            injector.getInstance(PersistService.class).stop();
        }
    }

    @Test
    void poblarVehiculos() {
        System.out.println("--- Iniciando población de vehículos ---");

        // 1. RECUPERAR MODELOS EXISTENTES USANDO EL SERVICIO DE MODELOS
        // Esto trae los datos cargados por el import.sql (Fiat Cronos, Argo, etc.)
        List<ModeloVehiculo> modelosDisponibles = modeloVehiculoServ.verTodos();

        if (modelosDisponibles.isEmpty()) {
            fail("No se encontraron modelos en la base de datos. Verifique que el import.sql se haya ejecutado o que la base de datos tenga datos.");
        }
        System.out.println("Se encontraron " + modelosDisponibles.size() + " modelos base para generar vehículos.");

        Random random = new Random();
        int cantidadVehiculos = 50; // Cantidad de vehículos a generar
        int creadosExitosamente = 0;

        String[] colores = {"Blanco Banchisa", "Negro Vulcano", "Gris Silverstone", "Rojo Montecarlo", "Azul Jazz", "Gris Bari"};

        // 2. GENERACIÓN DE VEHÍCULOS
        for (int i = 0; i < cantidadVehiculos; i++) {

            // A. Seleccionar un modelo aleatorio de la lista recuperada
            ModeloVehiculo modeloSeleccionado = modelosDisponibles.get(random.nextInt(modelosDisponibles.size()));

            // B. Generar datos aleatorios únicos
            // Patente formato: AE 123 CD (Simulado)
            String letrasIni = UUID.randomUUID().toString().substring(0, 2).toUpperCase();
            String numeros = String.format("%03d", random.nextInt(999));
            String letrasFin = UUID.randomUUID().toString().substring(2, 4).toUpperCase();
            String patenteGenerada = letrasIni + numeros + letrasFin;

            String chasis = "8AP" + UUID.randomUUID().toString().substring(0, 14).toUpperCase().replace("-", "");
            String motor = "MOTOR-" + random.nextInt(100000);
            String color = colores[random.nextInt(colores.length)];

            // C. Crear la entidad
            // El Cliente va en null, el servicio lo admite.
            Vehiculo nuevoVehiculo = new Vehiculo(
                    null,
                    patenteGenerada,
                    chasis,
                    motor,
                    color,
                    true, // estado
                    modeloSeleccionado,
                    null // cliente
            );

            // D. Variar la fecha de registro para pruebas de reportes históricos
            nuevoVehiculo.setFechaRegistro(generarFechaAleatoria());

            try {
                // E. Persistir usando el servicio de Vehículos
                // El servicio se encarga de hacer merge del modelo detached
                vehiculoServ.cargarVehiculo(nuevoVehiculo);
                creadosExitosamente++;

                System.out.println("Vehículo guardado: " + patenteGenerada + " (" + modeloSeleccionado.getNombreModelo() + ")");

            } catch (Exception e) {
                System.err.println("Error guardando vehículo " + patenteGenerada + ": " + e.getMessage());
                // No fallamos el test entero si uno falla, pero lo logueamos
            }
        }

        assertEquals(cantidadVehiculos, creadosExitosamente, "Se deberían haber guardado todos los vehículos generados.");
        System.out.println("--- Fin de la población: " + creadosExitosamente + " vehículos creados. ---");
    }

    /**
     * Genera una fecha aleatoria en los últimos 4 años
     */
    private LocalDate generarFechaAleatoria() {
        long minDay = LocalDate.now().minusYears(4).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }
}