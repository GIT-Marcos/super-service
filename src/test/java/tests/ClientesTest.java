package tests;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.DatosContacto;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.persistence.PersistenceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClientesTest {

    private Injector injector;
    private ClienteServ clienteServ;
    private final String[] NOMBRES = {
            // Aymaras
            "Nayra",    // mirada
            "Inti",     // sol
            "Suma",     // bondad
            "Awki",     // anciano respetado
            "Jacha",    // grande
            "Wara",     // estrella
            "Jach’a Uru", // gran día
            "Ch’aska",  // lucero

            // Quechuas
            "Kusi",     // alegría
            "Killa",    // luna
            "Yaku",     // agua
            "Sisa",     // flor
            "Atiq",     // vencedor
            "Illari",   // amanecer
            "Amaru",    // serpiente sagrada
            "Ñusta",    // princesa

            // Guaraníes
            "Arami",    // cielo
            "Tupã",     // dios del trueno
            "Arandú",   // sabio
            "Karu",     // comida
            "Yvoty",    // flor
            "Pora",     // linda(o)
            "Guarasy",  // sol
    };
    private final String[] APELLIDOS = {
            // Aymaras
            "Aruquipa",
            "Acarapi",
            "Quenta",
            "Callisaya",
            "Chura",
            "Pacajes",
            "Cocarico",
            "Patzi",
            // Quechuas
            "Quisbert",
            "Quillca",
            "Apaza",
            "Llusco",
            "Chambi",
            "Cutipa",
            // Guaraníes
            "Velasco",
            "Nina",
            "Ayala",
            "Cabral",
            "Arapi",
            "Bogado"
    };

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new PersistenceModule());
        injector.getInstance(PersistService.class).start();
        this.clienteServ = injector.getInstance(ClienteServ.class);
    }

    @AfterEach
    void tearDown() {
        if (injector != null) {
            injector.getInstance(PersistService.class).stop();
        }
    }

    @Test
    void poblarClientes() {
        System.out.println("--- Iniciando población de Clientes ---");

        Random random = new Random();
        int cantidadClientes = 50;
        int guardadosExitosamente = 0;

        String[] dominios = {"gmail.com", "hotmail.com", "outlook.com", "yahoo.com"};

        for (int i = 0; i < cantidadClientes; i++) {
            try {
                // 1. Generar Datos Personales
                String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
                String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];

                // Generamos un DNI aleatorio entre 20M y 50M.
                // Sumamos 'i' para reducir chance de colisión en bucle rápido.
                String dni = String.valueOf(20_000_000 + random.nextInt(30_000_000) + i);

                // 2. Crear Datos de Contacto (Obligatorio por el CascadeType.ALL)
                Set<String> telefonos = new HashSet<>();
                telefonos.add("351-" + (4000000 + random.nextInt(999999))); // Número fijo simulado
                if (random.nextBoolean()) {
                    telefonos.add("351-15" + (4000000 + random.nextInt(999999))); // Celular simulado
                }

                Set<String> emails = new HashSet<>();
                String email = nombre.toLowerCase() + "." + apellido.toLowerCase() + i + "@" + dominios[random.nextInt(dominios.length)];
                // Eliminamos acentos básicos del email para realismo
                email = email.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n");
                emails.add(email);

                DatosContacto datosContacto = new DatosContacto(null, telefonos, emails);

                // 3. Crear Cliente
                // Pasamos Sets vacíos (o null según tu indicación, pero HashSet vacío es más seguro) para vehículos y services
                Cliente nuevoCliente = new Cliente(
                        null,
                        dni,
                        nombre, // El servicio lo capitalizará (poneMayus)
                        apellido,
                        datosContacto,
                        new HashSet<>(), // Vehículos
                        new HashSet<>(), // Ventas
                        new HashSet<>()  // Services
                );

                // 4. Guardar mediante el servicio
                // El servicio se encarga de la lógica de negocio (Mayúsculas) y DAO guarda en cascada los contactos
                Cliente clienteGuardado = clienteServ.saveClient(nuevoCliente);

                assertNotNull(clienteGuardado.getId(), "El ID del cliente no debería ser nulo tras guardar");
                guardadosExitosamente++;

                System.out.println("Cliente guardado: " + clienteGuardado.getApellido() + ", " + clienteGuardado.getNombre() + " [DNI: " + clienteGuardado.getDni() + "]");

            } catch (Exception e) {
                // Capturamos excepción (ej: DNI duplicado si el random repite) para no detener el test
                System.err.println("Error guardando cliente iteración " + i + ": " + e.getMessage());
            }
        }

        assertEquals(cantidadClientes, guardadosExitosamente, "Se deberían haber guardado todos los clientes generados.");
        System.out.println("--- Población finalizada. Total: " + guardadosExitosamente + " ---");
    }



}