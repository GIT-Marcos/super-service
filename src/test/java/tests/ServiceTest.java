package tests;

import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.MetodosPago;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.persistence.PersistenceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class ServiceTest {

    private Injector injector;
    private ServiceServ serviceServ;
    private ClienteServ clienteServ;
    private VehiculoServ vehiculoServ;
    private RepuestoServ repuestoServ;

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new PersistenceModule());
        injector.getInstance(PersistService.class).start();

        this.serviceServ = injector.getInstance(ServiceServ.class);
        this.clienteServ = injector.getInstance(ClienteServ.class);
        this.vehiculoServ = injector.getInstance(VehiculoServ.class);
        this.repuestoServ = injector.getInstance(RepuestoServ.class);
    }

    @AfterEach
    void tearDown() {
        if (injector != null) {
            injector.getInstance(PersistService.class).stop();
        }
    }

    @Test
    void poblarServices() {
        System.out.println("--- Iniciando población de Services ---");

        // 1. OBTENER ENTIDADES EXISTENTES
        List<Cliente> clientesFull = clienteServ.getAllActive();
        List<Vehiculo> vehiculos = vehiculoServ.verTodosActivos();
        List<Repuesto> repuestos = repuestoServ.verTodos();

        if (clientesFull.isEmpty() || vehiculos.isEmpty()) {
            fail("Faltan datos previos (Clientes o Vehículos). Ejecuta los tests de población anteriores.");
        }

        // 2. ASIGNAR VEHÍCULOS A CLIENTES (ManyToMany)
        System.out.println("--- Asignando vehículos a clientes ---");
        Random random = new Random();

        // Crear map de vehículos por ID para acceso rápido
        Map<Long, Vehiculo> mapaVehiculos = vehiculos.stream()
                .collect(Collectors.toMap(Vehiculo::getId, v -> v));

        // Asignar vehículos a cada cliente
        for (Cliente cliente : clientesFull) {
            try {
                // Determinar cuántos vehículos asignar a este cliente (1-3)
                int cantVehiculos = random.nextInt(3) + 1;
                Set<Long> vehiculosAsignados = new HashSet<>();

                for (int i = 0; i < cantVehiculos && !vehiculos.isEmpty(); i++) {
                    // Intentar asignar un vehículo que no tenga ya este cliente
                    Vehiculo vehiculoSeleccionado = null;
                    int intentos = 0;

                    while (intentos < 10 && vehiculoSeleccionado == null) {
                        Vehiculo candidato = vehiculos.get(random.nextInt(vehiculos.size()));

                        // Verificar que no se repita para este cliente
                        if (!vehiculosAsignados.contains(candidato.getId())) {
                            vehiculoSeleccionado = candidato;
                            vehiculosAsignados.add(candidato.getId());
                        }
                        intentos++;
                    }

                    if (vehiculoSeleccionado != null) {
                        cliente.asociarVehiculo(vehiculoSeleccionado);
                        System.out.println("Asociado vehículo " + vehiculoSeleccionado.getPatente() +
                                " al cliente " + cliente.getNombre() + " " + cliente.getApellido());
                    }
                }

                // Persistir la asociación actualizando el cliente
                //clienteServ.editClient(cliente);

            } catch (Exception e) {
                System.err.println("Error al asociar vehículos al cliente " + cliente.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("--- Asociaciones completadas ---");

        // 3. RECARGAR CLIENTES CON VEHÍCULOS ACTUALIZADOS
        clientesFull = clienteServ.getAllActive();

        // Map para búsqueda rápida de Cliente por ID
        Map<Long, Cliente> mapaClientesFull = clientesFull.stream()
                .collect(Collectors.toMap(Cliente::getId, c -> c));

        // 4. CREAR SERVICES
        int cantidadServices = 70;
        int creados = 0;

        List<EstadoService> estadosPosibles = Arrays.stream(EstadoService.values())
                .filter(e -> e != EstadoService.PAGADO)
                .toList();

        String[] motivos = {"Falla en arranque", "Service 10.000km", "Ruido tren delantero",
                "Cambio pastillas freno", "Revisión aire acondicionado",
                "Pérdida de aceite", "Control de fluidos", "Cambio de correa"};

        String[] trabajosLista = {"Mano de obra mecánica", "Diagnóstico computarizado",
                "Alineación", "Balanceo", "Limpieza de inyectores",
                "Cambio de filtros", "Regulación de frenos"};

        for (int i = 0; i < cantidadServices; i++) {
            try {
                // 5. SELECCIONAR CLIENTE Y VEHÍCULO
                Cliente clienteAsignado = clientesFull.get(random.nextInt(clientesFull.size()));

                // Seleccionar un vehículo de los que tiene asignados este cliente
                Vehiculo vehiculoAsignado = null;

                if (!clienteAsignado.getVehiculos().isEmpty()) {
                    // Convertir Set a List para acceder por índice
                    List<Vehiculo> vehiculosCliente = new ArrayList<>(clienteAsignado.getVehiculos());
                    vehiculoAsignado = vehiculosCliente.get(random.nextInt(vehiculosCliente.size()));
                } else {
                    // Si el cliente no tiene vehículos (no debería pasar), usar uno aleatorio
                    System.out.println("ADVERTENCIA: Cliente " + clienteAsignado.getId() + " sin vehículos asignados");
                    vehiculoAsignado = vehiculos.get(random.nextInt(vehiculos.size()));
                }

                // 6. CREAR ESTADO INGRESO
                EstadoIngreso estadoIngreso = new EstadoIngreso(
                        null,
                        "Sin observaciones",
                        "Rueda auxilio, Cricket",
                        random.nextInt(200000),
                        random.nextInt(100)
                );

                // 7. CREAR ORDEN
                Orden orden = new Orden(
                        null,
                        motivos[random.nextInt(motivos.length)],
                        "Informe técnico generado automáticamente",
                        estadoIngreso,
                        null
                );
                orden.asociarVehiculo(vehiculoAsignado);

                // 8. AGREGAR TRABAJOS
                int cantTrabajos = random.nextInt(3) + 1;
                Set<Trabajo> trabajos = new HashSet<>();
                for (int j = 0; j < cantTrabajos; j++) {
                    trabajos.add(new Trabajo(
                            null,
                            trabajosLista[random.nextInt(trabajosLista.length)],
                            BigDecimal.valueOf(random.nextInt(45000) + 4000)
                    ));
                }
                orden.agregarTrabajos(trabajos);

                // 9. AGREGAR REPUESTOS
                NotaRetiro notaRetiro = new NotaRetiro(null, NotaRetiro.TipoUsoRetiro.SERVICE, new ArrayList<>());

                if (!repuestos.isEmpty() && random.nextDouble() > 0.4) {
                    int cantRepuestos = random.nextInt(2) + 1;
                    List<DetalleRetiro> detalles = new ArrayList<>();

                    for (int k = 0; k < cantRepuestos; k++) {
                        Repuesto rep = repuestos.get(random.nextInt(repuestos.size()));
                        detalles.add(new DetalleRetiro(null, (double) (random.nextInt(2) + 1), rep));
                    }
                    notaRetiro.agregarDetalle(detalles);
                }

                orden.setNotaRetiro(notaRetiro);

                // 10. CONFIGURAR FECHAS Y PRIORIDAD
                LocalDateTime fechaCarga = generarFechaAleatoriaEnElAnio();
                LocalDateTime fechaEntrega = fechaCarga.plusDays(random.nextInt(15));
                PrioridadService prioridad = PrioridadService.values()[random.nextInt(PrioridadService.values().length)];

                // 11. CREAR SERVICE
                Service service = new Service(fechaEntrega, prioridad, clienteAsignado, orden);
                service.setFechaCarga(fechaCarga);
                service.setEstadoService(estadosPosibles.get(random.nextInt(estadosPosibles.size())));

                // 12. GUARDAR
                serviceServ.cargarService(service);
                creados++;
                System.out.println("Service guardado [" + creados + "/" + cantidadServices +
                        "] | Cliente: " + clienteAsignado.getApellido() +
                        " | Vehículo: " + vehiculoAsignado.getPatente() +
                        " | Fecha: " + fechaCarga.toLocalDate());

            } catch (Exception e) {
                fail("Error al guardar service " + (i+1) + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("--- Fin población Services. Total creados: " + creados + " ---");
    }

    @Test
    void poblarPagos() {
        System.out.println("--- Iniciando población de Pagos para Services ---");

        // Tu DAO serviceServ.verTodos() ya tiene JOIN FETCH s.pagos, así que esto es seguro.
        List<Service> todosLosServices = serviceServ.verTodos();

        List<Service> candidatosAPagar = todosLosServices.stream()
                .filter(s -> s.getEstadoService() != EstadoService.CANCELADO &&
                        s.getEstadoService() != EstadoService.PAGADO &&
                        s.getMontoFaltante().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        if (candidatosAPagar.isEmpty()) {
            System.out.println("No hay services pendientes de pago para procesar.");
            return;
        }

        Collections.shuffle(candidatosAPagar);
        Random random = new Random();

        int cantidadObjetivo = 40;
        int cantidadAPagar = Math.min(cantidadObjetivo, candidatosAPagar.size());
        int pagosRealizados = 0;

        System.out.println("Se procederá a pagar " + cantidadAPagar + " services.");

        String[] bancos = {"Banco Galicia", "Santander", "BBVA", "Banco Nación", "ICBC"};
        String[] tarjetas = {"Visa", "Mastercard", "Amex"};

        for (int i = 0; i < cantidadAPagar; i++) {
            Service service = candidatosAPagar.get(i);

            try {
                // 2. DEFINIR MONTO
                boolean pagoTotal = random.nextDouble() > 0.2;
                BigDecimal montoAPagar;

                if (pagoTotal) {
                    montoAPagar = service.getMontoFaltante();
                } else {
                    double porcentaje = 0.1 + (0.8 * random.nextDouble());
                    montoAPagar = service.getMontoFaltante().multiply(BigDecimal.valueOf(porcentaje));
                    montoAPagar = montoAPagar.setScale(2, java.math.RoundingMode.HALF_UP);
                }

                // 3. DEFINIR DETALLES PAGO
                MetodosPago metodo = MetodosPago.values()[random.nextInt(MetodosPago.values().length)];
                String banco = null, marcaTarjeta = null, ultimos4 = null, referencia = null;

                if (metodo == MetodosPago.TARJETA_CREDITO || metodo == MetodosPago.TARJETA_DEBITO) {
                    banco = bancos[random.nextInt(bancos.length)];
                    marcaTarjeta = tarjetas[random.nextInt(tarjetas.length)];
                    ultimos4 = String.valueOf(random.nextInt(9000) + 1000);
                    referencia = "REF-" + random.nextInt(999999);
                } else if (metodo == MetodosPago.TRANSFERENCIA) {
                    banco = bancos[random.nextInt(bancos.length)];
                    referencia = "TRF-" + random.nextInt(99999999);
                } else {
                    // Efectivo
                    referencia = "TKT-" + random.nextInt(999999);
                }

                String dniCliente = service.getCliente().getDni();

                // 4. CREAR PAGO
                // Corregido: Ajuste de nulls para evitar ambigüedad según constructor de Pago si es necesario
                // Asumimos constructor (UUID, DNI, Monto, Marca, Banco, Ref, Descuento, U4, Metodo, Venta, Service)
                Pago nuevoPago = new Pago(
                        null,
                        dniCliente,
                        montoAPagar,
                        marcaTarjeta,
                        banco,
                        referencia,
                        BigDecimal.ZERO,
                        ultimos4,
                        null,
                        metodo,
                        null, // VentaRepuesto
                        null  // Service se asigna luego con asociarService
                );

                LocalDate fechaEntrega = service.getFechaEntrega().toLocalDate();
                if (fechaEntrega.isBefore(LocalDate.now())) {
                    nuevoPago.setFechaPago(fechaEntrega.atTime(LocalTime.NOON));
                } else {
                    nuevoPago.setFechaPago(LocalDateTime.now());
                }

                // 5. VINCULAR Y PERSISTIR
                service.asociarPago(nuevoPago);
                serviceServ.modificarService(service);
                pagosRealizados++;

                System.out.println("Pago registrado [" + pagosRealizados + "/" + cantidadAPagar + "] Service ID: " + service.getId());

            } catch (Exception e) {
                System.err.println("Error al registrar pago: " + e.getMessage());
            }
        }

        System.out.println("--- Fin de población de Pagos. Total procesados: " + pagosRealizados + " ---");
    }

    private LocalDateTime generarFechaAleatoriaEnElAnio() {
        int currentYear = Year.now().getValue();
        long minDay = LocalDate.of(currentYear, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(currentYear, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        int hour = ThreadLocalRandom.current().nextInt(8, 18);
        int minute = ThreadLocalRandom.current().nextInt(0, 59);
        return LocalDate.ofEpochDay(randomDay).atTime(hour, minute);
    }
}