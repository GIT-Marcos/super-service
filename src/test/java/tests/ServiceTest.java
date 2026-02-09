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
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
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
    private Provider<EntityManager> emProvider;
    private final int ANIO_GENERACION = 2025;

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new PersistenceModule());
        injector.getInstance(PersistService.class).start();

        this.serviceServ = injector.getInstance(ServiceServ.class);
        this.clienteServ = injector.getInstance(ClienteServ.class);
        this.vehiculoServ = injector.getInstance(VehiculoServ.class);
        this.repuestoServ = injector.getInstance(RepuestoServ.class);

        this.emProvider = injector.getProvider(EntityManager.class);
    }

    @AfterEach
    void tearDown() {
        if (injector != null) {
            injector.getInstance(PersistService.class).stop();
        }
    }

    @Test
    void poblarServices() {
        System.out.println("--- Iniciando población de Services (Año 2025 completo) ---");

        UnitOfWork uow = injector.getInstance(UnitOfWork.class);
        uow.begin();

        try {
            EntityManager em = injector.getProvider(EntityManager.class).get();

            // 1. OBTENER ENTIDADES EXISTENTES
            List<Cliente> clientesFull = em.createQuery("select distinct c from Cliente c " +
                            "left join fetch c.vehiculos",
                    Cliente.class).getResultList();

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
                    int cantVehiculos = random.nextInt(3) + 1;
                    Set<Long> vehiculosAsignados = new HashSet<>();

                    for (int i = 0; i < cantVehiculos && !vehiculos.isEmpty(); i++) {
                        Vehiculo vehiculoSeleccionado = null;
                        int intentos = 0;

                        while (intentos < 10 && vehiculoSeleccionado == null) {
                            Vehiculo candidato = vehiculos.get(random.nextInt(vehiculos.size()));

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

                } catch (Exception e) {
                    System.err.println("Error al asociar vehículos al cliente " + cliente.getId() + ": " + e.getMessage());
                }
            }
            System.out.println("--- Asociaciones completadas ---\n");

            // 3. RECARGAR CLIENTES CON VEHÍCULOS ACTUALIZADOS
            clientesFull = clienteServ.verTodosActivos();

            // Map para búsqueda rápida de Cliente por ID
            Map<Long, Cliente> mapaClientesFull = clientesFull.stream()
                    .collect(Collectors.toMap(Cliente::getId, c -> c));

            // 4. CONFIGURACIÓN PARA AÑO 2025
            int totalServicesCreados = 0;

            String[] motivos = {
                    "Falla en arranque", "Service 10.000km", "Ruido tren delantero",
                    "Cambio pastillas freno", "Revisión aire acondicionado",
                    "Pérdida de aceite", "Control de fluidos", "Cambio de correa",
                    "Service 20.000km", "Cambio de embrague", "Falla en transmisión",
                    "Reparación sistema eléctrico", "Cambio de amortiguadores",
                    "Alineación y balanceo", "Diagnóstico general", "Falla sensor oxígeno"
            };

            String[] trabajosLista = {
                    "Mano de obra mecánica", "Diagnóstico computarizado",
                    "Alineación", "Balanceo", "Limpieza de inyectores",
                    "Cambio de filtros", "Regulación de frenos", "Cambio de aceite",
                    "Reparación eléctrica", "Soldadura", "Pintura", "Pulido",
                    "Instalación de accesorios", "Revisión de suspensión"
            };

            System.out.println("===========================================");
            System.out.println("Generando Services para todo el año 2025");
            System.out.println("===========================================\n");

            // 5. GENERAR SERVICES POR CADA MES DEL 2025
            for (int mes = 1; mes <= 12; mes++) {
                // Número variable de services por mes (entre 5 y 10 para un total de ~90 services)
                int numServicesEsteMes = 5 + random.nextInt(6);
                int diasEnMes = YearMonth.of(ANIO_GENERACION, mes).lengthOfMonth();

                System.out.println("📅 Procesando " + Month.of(mes).name() + " 2025 - Generando " +
                        numServicesEsteMes + " services...");

                for (int i = 0; i < numServicesEsteMes; i++) {
                    try {
                        // 6. GENERAR FECHA ALEATORIA EN EL MES
                        int diaAleatorio = random.nextInt(diasEnMes) + 1;
                        // Horario de taller: 8 AM - 6 PM
                        int horaAleatoria = 8 + random.nextInt(10);
                        int minutoAleatorio = random.nextInt(60);

                        LocalDateTime fechaCarga = LocalDateTime.of(
                                ANIO_GENERACION, mes, diaAleatorio, horaAleatoria, minutoAleatorio
                        );

                        // Fecha de entrega: entre 3 y 20 días después de la carga
                        int diasParaEntrega = 3 + random.nextInt(18);
                        LocalDateTime fechaEntrega = fechaCarga.plusDays(diasParaEntrega);

                        // 7. SELECCIONAR CLIENTE Y VEHÍCULO
                        Cliente clienteAsignado = clientesFull.get(random.nextInt(clientesFull.size()));

                        Vehiculo vehiculoAsignado = null;

                        if (!clienteAsignado.getVehiculos().isEmpty()) {
                            List<Vehiculo> vehiculosCliente = new ArrayList<>(clienteAsignado.getVehiculos());
                            vehiculoAsignado = vehiculosCliente.get(random.nextInt(vehiculosCliente.size()));
                        } else {
                            System.out.println("  ⚠️ ADVERTENCIA: Cliente " + clienteAsignado.getId() +
                                    " sin vehículos asignados");
                            vehiculoAsignado = vehiculos.get(random.nextInt(vehiculos.size()));
                        }

                        // 8. CREAR ESTADO INGRESO
                        String[] observaciones = {
                                "Sin observaciones", "Rayón en puerta derecha",
                                "Golpe menor en paragolpes", "Luz de check engine encendida",
                                "Cliente reporta vibración", "Ruido al frenar",
                                "Pérdida de potencia", "Consumo excesivo de combustible"
                        };

                        String[] herramientas = {
                                "Rueda auxilio, Cricket", "Rueda auxilio, Cricket, Matafuegos",
                                "Rueda auxilio", "Cricket, Llave de rueda",
                                "Kit completo de herramientas", "Rueda auxilio, Triángulo"
                        };

                        EstadoIngreso estadoIngreso = new EstadoIngreso(
                                null,
                                observaciones[random.nextInt(observaciones.length)],
                                herramientas[random.nextInt(herramientas.length)],
                                random.nextInt(200000),
                                20 + random.nextInt(80)  // Nivel de combustible entre 20% y 100%
                        );

                        Orden orden = new Orden(
                                motivos[random.nextInt(motivos.length)],
                                "Informe técnico generado automáticamente para service del " +
                                        fechaCarga.toLocalDate() + ".",
                                estadoIngreso
                        );

                        orden.asociarVehiculo(vehiculoAsignado);

                        // 9. AGREGAR TRABAJOS (1 a 4 trabajos por orden)
                        int cantTrabajos = 1 + random.nextInt(4);
                        Set<Trabajo> trabajos = new HashSet<>();
                        Set<String> trabajosUsados = new HashSet<>();

                        for (int j = 0; j < cantTrabajos; j++) {
                            String trabajoDescripcion;
                            int intentos = 0;
                            do {
                                trabajoDescripcion = trabajosLista[random.nextInt(trabajosLista.length)];
                                intentos++;
                            } while (trabajosUsados.contains(trabajoDescripcion) && intentos < 10);

                            if (!trabajosUsados.contains(trabajoDescripcion)) {
                                trabajosUsados.add(trabajoDescripcion);
                                trabajos.add(new Trabajo(
                                        null,
                                        trabajoDescripcion,
                                        BigDecimal.valueOf(5000 + random.nextInt(50000))
                                ));
                            }
                        }
                        orden.agregarTrabajos(trabajos);

                        // 10. AGREGAR REPUESTOS (60% de probabilidad)
                        NotaRetiro notaRetiro = new NotaRetiro(NotaRetiro.TipoUsoRetiro.SERVICE, new HashSet<>());

                        if (!repuestos.isEmpty() && random.nextDouble() < 0.6) {
                            int cantRepuestos = 1 + random.nextInt(3);
                            Set<DetalleRetiro> detalles = new HashSet<>();
                            Set<Long> repuestosUsados = new HashSet<>();

                            for (int k = 0; k < cantRepuestos; k++) {
                                Repuesto rep;
                                int intentos = 0;
                                do {
                                    rep = repuestos.get(random.nextInt(repuestos.size()));
                                    intentos++;
                                } while (repuestosUsados.contains(rep.getId()) && intentos < 10);

                                if (!repuestosUsados.contains(rep.getId())) {
                                    repuestosUsados.add(rep.getId());
                                    detalles.add(new DetalleRetiro(
                                            (double) (1 + random.nextInt(3)),
                                            rep
                                    ));
                                }
                            }
                            notaRetiro.agregarDetalle(detalles);
                        }

                        orden.setNotaRetiro(notaRetiro);

                        // 11. CONFIGURAR PRIORIDAD basada en el motivo
                        PrioridadService prioridad;
                        String motivoSeleccionado = orden.getMotivoIngreso();

                        if (motivoSeleccionado.contains("Falla") || motivoSeleccionado.contains("Pérdida")) {
                            // Mayor probabilidad de alta prioridad para fallas
                            prioridad = random.nextDouble() < 0.7 ?
                                    PrioridadService.ALTA : PrioridadService.MEDIA;
                        } else if (motivoSeleccionado.contains("Service") || motivoSeleccionado.contains("Control")) {
                            // Services de rutina suelen ser de prioridad baja o media
                            prioridad = random.nextDouble() < 0.6 ?
                                    PrioridadService.BAJA : PrioridadService.MEDIA;
                        } else {
                            prioridad = PrioridadService.values()[random.nextInt(PrioridadService.values().length)];
                        }

                        // 12. CREAR SERVICE
                        // El estado se asigna automáticamente al cargar el service
                        Service service = new Service(fechaEntrega, prioridad);
                        service.asignarCliente(clienteAsignado);
                        service.asignarOrden(orden);
                        service.setFechaCarga(fechaCarga);

                        // 13. GUARDAR
                        serviceServ.cargarService(service);
                        totalServicesCreados++;

                        System.out.println("  ✓ Service #" + totalServicesCreados +
                                " | " + fechaCarga.toLocalDate() +
                                " | Cliente: " + clienteAsignado.getApellido() +
                                " | Vehículo: " + vehiculoAsignado.getPatente() +
                                " | Prioridad: " + prioridad);

                    } catch (Exception e) {
                        System.err.println("  ❌ Error al guardar service: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                System.out.println("  📊 Mes " + String.format("%02d", mes) + "/2025 completado. " +
                        "Total acumulado: " + totalServicesCreados + "\n");
            }

            // RESUMEN FINAL
            System.out.println("\n===========================================");
            System.out.println("RESUMEN FINAL DE SERVICES:");
            System.out.println("- Año procesado: 2025 (completo)");
            System.out.println("- Total de services creados: " + totalServicesCreados);
            System.out.println("- Promedio por mes: " + (totalServicesCreados / 12));
            System.out.println("- Clientes utilizados: " + clientesFull.size());
            System.out.println("- Vehículos disponibles: " + vehiculos.size());
            System.out.println("===========================================\n");

        } catch (RuntimeException e) {
            e.printStackTrace();
            fail("Error durante la población de services: " + e.getMessage());
        } finally {
            uow.end();  // 👈 cerrar contexto
        }
    }

    @Test
    void poblarPagos() {
        System.out.println("--- Iniciando población de Pagos para Services (Año 2025) ---");

        UnitOfWork uow = injector.getInstance(UnitOfWork.class);
        uow.begin();
        try {
            EntityManager em = injector.getProvider(EntityManager.class).get();
            List<Service> todosLosServices = em.createQuery("select distinct s from Service s " +
                            "left join fetch s.cliente " +
                            "left join fetch s.pagos ",
                    Service.class).getResultList();

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

            int totalPagosGenerados = 0;

            String[] bancos = {"Banco Galicia", "Santander", "BBVA", "Banco Nación", "ICBC", "Macro", "HSBC"};
            String[] tarjetas = {"Visa", "Mastercard", "Amex", "Cabal", "Naranja"};

            System.out.println("Services disponibles para pagar: " + candidatosAPagar.size());
            System.out.println("Generando pagos distribuidos durante todo el año 2025...\n");

            // Distribuir pagos a lo largo del año 2025
            for (int mes = 1; mes <= 12; mes++) {
                // Número variable de pagos por mes (entre 30 y 50)
                int numPagosEsteMes = 30 + random.nextInt(21);
                int diasEnMes = YearMonth.of(ANIO_GENERACION, mes).lengthOfMonth();

                System.out.println("Procesando " + Month.of(mes).name() + " 2025 - Generando " +
                        numPagosEsteMes + " pagos...");

                for (int i = 0; i < numPagosEsteMes && !candidatosAPagar.isEmpty(); i++) {
                    Service service = candidatosAPagar.get(random.nextInt(candidatosAPagar.size()));

                    try {
                        // Generar fecha aleatoria en el mes
                        int diaAleatorio = random.nextInt(diasEnMes) + 1;
                        // Horario bancario típico: 9 AM - 5 PM
                        int horaAleatoria = 9 + random.nextInt(8);
                        int minutoAleatorio = random.nextInt(60);

                        LocalDateTime fechaPago = LocalDateTime.of(
                                ANIO_GENERACION, mes, diaAleatorio, horaAleatoria, minutoAleatorio
                        );

                        // DEFINIR MONTO DEL PAGO
                        boolean pagoTotal = random.nextDouble() > 0.25; // 75% pagos totales, 25% parciales
                        BigDecimal montoAPagar;

                        if (pagoTotal) {
                            montoAPagar = service.getMontoFaltante();
                        } else {
                            // Pago parcial: entre 20% y 90% del monto faltante
                            double porcentaje = 0.2 + (0.7 * random.nextDouble());
                            montoAPagar = service.getMontoFaltante().multiply(BigDecimal.valueOf(porcentaje));
                            montoAPagar = montoAPagar.setScale(2, java.math.RoundingMode.HALF_UP);
                        }

                        // DEFINIR PAGO con distribución realista
                        MetodosPago metodo;
                        double probMetodo = random.nextDouble();
                        if (probMetodo < 0.30) {
                            metodo = MetodosPago.EFECTIVO;
                        } else if (probMetodo < 0.55) {
                            metodo = MetodosPago.TARJETA_DEBITO;
                        } else if (probMetodo < 0.80) {
                            metodo = MetodosPago.TARJETA_CREDITO;
                        } else {
                            metodo = MetodosPago.TRANSFERENCIA;
                        }

                        String banco = null, marcaTarjeta = null, ultimos4 = null, referencia = null;

                        switch (metodo) {
                            case TARJETA_CREDITO:
                            case TARJETA_DEBITO:
                                banco = bancos[random.nextInt(bancos.length)];
                                marcaTarjeta = tarjetas[random.nextInt(tarjetas.length)];
                                ultimos4 = String.valueOf(random.nextInt(9000) + 1000);
                                referencia = "REF-2025-" + String.format("%02d", mes) + "-" +
                                        String.format("%04d", totalPagosGenerados + 1);
                                break;
                            case TRANSFERENCIA:
                                banco = bancos[random.nextInt(bancos.length)];
                                referencia = "TRF-2025-" + String.format("%02d", mes) + "-" +
                                        UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                                break;
                            case EFECTIVO:
                            default:
                                referencia = "TKT-2025-" + String.format("%02d", mes) + "-" +
                                        String.format("%05d", totalPagosGenerados + 1);
                                break;
                        }

                        String dniCliente = service.getCliente().getDni();

                        // CREAR PAGO
                        Pago nuevoPago = new Pago(dniCliente,
                                montoAPagar,
                                marcaTarjeta,
                                banco,
                                referencia,
                                BigDecimal.ZERO,
                                ultimos4,
                                null,
                                metodo);

                        nuevoPago.setFechaPago(fechaPago);

                        // VINCULAR Y PERSISTIR
                        // El estado del service se actualiza automáticamente al asociar el pago
                        service.asociarPago(nuevoPago);
                        serviceServ.modificarService(service);
                        totalPagosGenerados++;

                        // Si el service está completamente pagado, lo removemos de la lista
                        if (service.getMontoFaltante().compareTo(BigDecimal.ZERO) <= 0) {
                            candidatosAPagar.remove(service);
                            System.out.println("  ✓ Service ID: " + service.getId() +
                                    " - PAGADO COMPLETAMENTE (Estado: " + service.getEstadoService() + ")");
                        } else {
                            System.out.println("  → Service ID: " + service.getId() +
                                    " - Pago parcial registrado (Faltante: $" +
                                    service.getMontoFaltante().setScale(2, java.math.RoundingMode.HALF_UP) + ")");
                        }

                    } catch (Exception e) {
                        System.err.println("Error al registrar pago en mes " + mes + ": " + e.getMessage());
                    }
                }

                System.out.println("Mes " + String.format("%02d", mes) + "/2025 completado. " +
                        "Total pagos acumulados: " + totalPagosGenerados + "\n");
            }

            // RESUMEN FINAL
            System.out.println("\n===========================================");
            System.out.println("RESUMEN FINAL DE PAGOS:");
            System.out.println("- Año procesado: 2025 (completo)");
            System.out.println("- Total de pagos generados: " + totalPagosGenerados);
            System.out.println("- Promedio por mes: " + (totalPagosGenerados / 12));

            // Estadísticas adicionales
            long servicesPagadosCompletos = todosLosServices.stream()
                    .filter(s -> s.getEstadoService() == EstadoService.PAGADO)
                    .count();
            long servicesConPagosParciales = todosLosServices.stream()
                    .filter(s -> s.getMontoFaltante().compareTo(BigDecimal.ZERO) > 0 &&
                            s.getPagos() != null && !s.getPagos().isEmpty())
                    .count();

            System.out.println("- Services pagados completamente: " + servicesPagadosCompletos);
            System.out.println("- Services con pagos parciales: " + servicesConPagosParciales);
            System.out.println("===========================================\n");

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            uow.end();
        }
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