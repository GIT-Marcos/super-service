package tests;

import SPRService.SPRService.entities.*;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.services.ModeloVehiculoServ;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.persistence.PersistenceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public class VehiculosTest {

    private Injector injector;
    private VehiculoServ vehiculoServ;
    private ClienteServ clienteServ;
    private ModeloVehiculoServ modeloVehiculoServ;

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new PersistenceModule());
        injector.getInstance(PersistService.class).start();

        this.vehiculoServ = injector.getInstance(VehiculoServ.class);
        this.clienteServ = injector.getInstance(ClienteServ.class);
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
        System.out.println("--- Iniciando población de Vehículos (Basado en Modelos de import.sql) ---");

        // 1. OBTENER DATOS EXISTENTES
        // Se asume que los modelos ya se cargaron via import.sql al levantar el PersistenceModule
        List<ModeloVehiculo> modelosDisponibles = modeloVehiculoServ.verTodos();
        //List<Cliente> clientes = clienteServ.getAllActive();

        if (modelosDisponibles.isEmpty()) {
            fail("No se encontraron Modelos. Verifica que 'import.sql' se esté ejecutando correctamente.");
        }
//        if (clientes.isEmpty()) {
//            fail("No hay clientes cargados. Ejecuta primero el test de Clientes.");
//        }

        System.out.println("Modelos disponibles: " + modelosDisponibles.size());
        //System.out.println("Clientes disponibles: " + clientes.size());

        Random random = new Random();
        int totalObjetivo = 70;
        int totalCreados = 0;

        // Set para asegurar unicidad de patentes en esta ejecución
        Set<String> patentesGeneradas = new HashSet<>();

        // 2. BUCLE DE GENERACIÓN
        while (totalCreados < totalObjetivo) {

            // A. Seleccionar un Modelo al azar de la lista cargada desde la BD
            ModeloVehiculo modeloSeleccionado = modelosDisponibles.get(random.nextInt(modelosDisponibles.size()));

            // B. Determinar tamaño del lote (batch) para este modelo (Entre 1 y 20)
            int cantidadLote = random.nextInt(20) + 1;

            // Ajustar si el lote excede lo que falta para llegar a 70
            if (totalCreados + cantidadLote > totalObjetivo) {
                cantidadLote = totalObjetivo - totalCreados;
            }

            System.out.println("-> Generando lote de " + cantidadLote + " vehículos del modelo: "
                    + modeloSeleccionado.getNombreModelo() + " (" + modeloSeleccionado.getAnio() + ")");

            // C. Crear los vehículos del lote
            for (int i = 0; i < cantidadLote; i++) {
                try {
                    //Cliente clienteAsignado = clientes.get(random.nextInt(clientes.size()));

                    // Generar datos aleatorios únicos
                    String patente = generarPatenteUnica(random, patentesGeneradas);
                    String chasis = generarAlfanumerico(17);
                    String motor = generarAlfanumerico(12);
                    String color = obtenerColorAleatorio(random);

                    // Instanciar Vehículo según el constructor de tu Entidad
//                    Vehiculo vehiculo = new Vehiculo(
//                            null,               // ID
//                            patente,            // Patente
//                            chasis,             // Nro Chasis
//                            motor,              // Nro Motor
//                            color,              // Color
//                            true,               // Estado (Boolean)
//                            modeloSeleccionado, // Entidad ModeloVehiculo
//                            clienteAsignado,     // Entidad Cliente
//                            null                // Ordenes
//                    );
                    Vehiculo vehiculo = new Vehiculo(null, patente, chasis, motor, color, modeloSeleccionado);
                    // Nota: fechaRegistro se asigna automáticamente a LocalDate.now() en el constructor

                    // Guardar en BD
                    vehiculoServ.cargarVehiculo(vehiculo);

                    patentesGeneradas.add(patente);
                    totalCreados++;

                } catch (Exception e) {
                    fail("Error al guardar vehículo lote: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("--- Fin población Vehículos. Total creados: " + totalCreados + " ---");
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Genera patentes con formato "AA 123 BB" (Nuevo) o "AAA 123" (Viejo)
     */
    private String generarPatenteUnica(Random random, Set<String> existentes) {
        String patente;
        do {
            boolean esFormatoNuevo = random.nextBoolean();
            if (esFormatoNuevo) {
                // AA 123 BB
                char l1 = (char) (random.nextInt(26) + 'A');
                char l2 = (char) (random.nextInt(26) + 'A');
                int num = random.nextInt(1000);
                char l3 = (char) (random.nextInt(26) + 'A');
                char l4 = (char) (random.nextInt(26) + 'A');
                patente = String.format("%c%c %03d %c%c", l1, l2, num, l3, l4);
            } else {
                // AAA 123
                char l1 = (char) (random.nextInt(26) + 'A');
                char l2 = (char) (random.nextInt(26) + 'A');
                char l3 = (char) (random.nextInt(26) + 'A');
                int num = random.nextInt(1000);
                patente = String.format("%c%c%c %03d", l1, l2, l3, num);
            }
        } while (existentes.contains(patente));
        return patente;
    }

    private String generarAlfanumerico(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < longitud; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String obtenerColorAleatorio(Random random) {
        String[] colores = {
                "Blanco Banchisa", "Negro Vulcano", "Rojo Montecarlo",
                "Gris Silverstone", "Gris Scandium", "Azul Jazz", "Bordó"
        };
        return colores[random.nextInt(colores.length)];
    }
}