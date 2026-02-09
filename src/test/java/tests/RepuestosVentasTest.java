package tests;

import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.MetodosPago;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.services.VentaRepuestoServ;
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

import static org.junit.jupiter.api.Assertions.*;

class RepuestosVentasTest {

    private Injector injector;
    private VentaRepuestoServ ventaRepuestoServ;
    private RepuestoServ repuestoServ;
    private ClienteServ clienteServ;
    private Provider<EntityManager> emProvider;

    // ARRAYS DE DATOS
    private static final String[] AUTOPARTES = {
            "Filtro de aceite", "Filtro de aire", "Filtro de combustible", "Bujías",
            "Pastillas de freno", "Discos de freno", "Amortiguadores", "Batería",
            "Radiador", "Alternador", "Bomba de agua", "Correa de distribución",
            "Correa auxiliar", "Sensor de oxígeno", "Inyectores", "Embrague",
            "Bobina de encendido", "Catalizador", "Turbo", "Faros delanteros", "Luces traseras"
    };

    private static final String[] MARCAS_AUTOPARTES = {
            "Bosch", "ACDelco", "NGK", "Denso", "Mann-Filter", "Mahle", "Monroe",
            "Valeo", "Hella", "Brembo", "TRW", "Sachs", "Continental", "Delphi",
            "Gates", "Magneti Marelli", "Ferodo", "KYB", "Mobil", "Castrol"
    };

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new PersistenceModule());
        injector.getInstance(PersistService.class).start();
        this.ventaRepuestoServ = injector.getInstance(VentaRepuestoServ.class);
        this.repuestoServ = injector.getInstance(RepuestoServ.class);
        this.clienteServ = injector.getInstance(ClienteServ.class);
        this.emProvider = injector.getProvider(EntityManager.class);
    }

    @AfterEach
    void tearDown() {
        if (injector != null) {
            injector.getInstance(PersistService.class).stop();
        }
    }

    @Test
    public void poblarRepuestos() {
        System.out.println("--- Iniciando población de Repuestos Realistas ---");

        Random random = new Random();

        // 1. PREPARAR CACHE DE MARCAS Y UBICACIÓN
        // Usamos un mapa para no crear duplicados de Marcas si sale la misma dos veces
        Map<String, MarcaRepuesto> marcasCache = new HashMap<>();

        // Creamos una única ubicación para este lote
        Ubicacion ubicacionActual = new Ubicacion(null, "DEPOSITO CENTRAL " + UUID.randomUUID().toString().substring(0, 4), new ArrayList<>());

        int cantidadRepuestos = 70;
        int creados = 0;

        // 2. CREAR LOS REPUESTOS
        for (int i = 0; i < cantidadRepuestos; i++) {
            try {
                // Selección Aleatoria de Nombre y Marca
                String nombreParte = AUTOPARTES[random.nextInt(AUTOPARTES.length)];
                String nombreMarca = MARCAS_AUTOPARTES[random.nextInt(MARCAS_AUTOPARTES.length)];

                // Generar nombre compuesto: Ej "Pastillas de freno Brembo"
                String nombreCompleto = nombreParte + " " + nombreMarca;

                // Recuperar marca del cache o crear nueva instancia si no existe
                MarcaRepuesto marcaEntity = marcasCache.getOrDefault(nombreMarca, new MarcaRepuesto(null, nombreMarca, new HashSet<>()));

                // Llamada al auxiliar pasando el nombre específico
                Repuesto repuestoGuardado = crearRepuestoParametrizado(i, nombreCompleto, marcaEntity, ubicacionActual);

                // GESTIÓN DE REFERENCIAS POST-GUARDADO

                // A. Actualizar Cache de Marcas:
                // Si la marca era nueva, ahora tiene ID gracias al Cascade del Repuesto. La guardamos en el mapa.
                if (!marcasCache.containsKey(nombreMarca)) {
                    marcasCache.put(nombreMarca, repuestoGuardado.getMarcaRepuesto());
                }

                // B. Actualizar Ubicación:
                // Solo en la primera iteración necesitamos recuperar la Ubicación con ID asignado
                if (i == 0) {
                    ubicacionActual = repuestoGuardado.getStock().getUbicacion();
                    assertNotNull(ubicacionActual.getId(), "La ubicación debería tener ID tras el primer guardado");
                }

                creados++;
                System.out.println("Repuesto creado [" + creados + "/" + cantidadRepuestos + "]: " + repuestoGuardado.getDetalle() + " (" + repuestoGuardado.getPrecio() + ")");

            } catch (Exception e) {
                System.err.println("Error al crear repuesto índice " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("--- Fin población Repuestos. Total creados: " + creados + " ---");
    }

    @Test
    public void poblarVentas() {
        UnitOfWork uow = injector.getInstance(UnitOfWork.class);
        uow.begin();   // 👈 abrir contexto de trabajo

        try {
            System.out.println("--- Iniciando población de Ventas para todo el año 2024 ---");

            List<Repuesto> listaRepuestos = repuestoServ.verTodos();
            if (listaRepuestos.isEmpty()) {
                fail("No hay repuestos en la base de datos. Ejecuta primero 'poblarRepuestos'.");
            }

            EntityManager em = injector.getProvider(EntityManager.class).get();
            List<Cliente> listaClientes = em.createQuery(
                    "select distinct c from Cliente c left join fetch c.ventas",
                    Cliente.class
            ).getResultList();

            if (listaClientes.isEmpty()) {
                fail("No hay clientes en la base de datos. Ejecuta el test para poblar clientes.");
            }

            Random random = new Random();
            int anio = 2025;
            int totalVentasGeneradas = 0;

            String[] bancos = {"Banco Galicia", "Santander", "BBVA", "Banco Nación", "ICBC", "Macro"};
            String[] marcasTarjetas = {"Visa", "Mastercard", "Amex", "Cabal"};

            // 👇 Generar ventas para todos los meses del 2024
            for (int mes = 1; mes <= 12; mes++) {
                // Número variable de ventas por mes (entre 25 y 45 para tener buena distribución)
                int numVentasEsteMes = 25 + random.nextInt(21);
                int diasEnMes = YearMonth.of(anio, mes).lengthOfMonth();

                System.out.println("Generando " + numVentasEsteMes + " ventas para " +
                        Month.of(mes).name() + " 2024...");

                for (int i = 0; i < numVentasEsteMes; i++) {
                    try {
                        // Distribución de días y horas para simular actividad comercial realista
                        int diaAleatorio = random.nextInt(diasEnMes) + 1;
                        // Horario comercial: principalmente entre 8 AM y 8 PM
                        int horaAleatoria = 8 + random.nextInt(12);
                        int minutoAleatorio = random.nextInt(60);

                        LocalDateTime fechaVenta = LocalDateTime.of(
                                anio, mes, diaAleatorio, horaAleatoria, minutoAleatorio
                        );

                        // Selección aleatoria de repuesto
                        Repuesto repuestoAleatorio = listaRepuestos.get(random.nextInt(listaRepuestos.size()));

                        // Cantidad variable entre 1 y 5 unidades
                        double cantidad = 1.0 + random.nextInt(5);

                        DetalleRetiro detalle = new DetalleRetiro(cantidad, repuestoAleatorio);
                        NotaRetiro notaRetiro = new NotaRetiro(NotaRetiro.TipoUsoRetiro.VENTA, new HashSet<>());
                        notaRetiro.agregarDetalle(detalle);

                        // 60% de probabilidad de tener cliente registrado, 40% consumidor final
                        Cliente cliente = null;
                        if (!listaClientes.isEmpty() && random.nextDouble() < 0.6) {
                            cliente = listaClientes.get(random.nextInt(listaClientes.size()));
                        }

                        VentaRepuesto venta = new VentaRepuesto(notaRetiro);
                        venta.asociarCliente(cliente);
                        venta.setFechaVenta(fechaVenta);

                        // Distribución de métodos de pago más realista
                        MetodosPago metodoSeleccionado;
                        double probMetodo = random.nextDouble();
                        if (probMetodo < 0.35) {
                            metodoSeleccionado = MetodosPago.EFECTIVO;
                        } else if (probMetodo < 0.60) {
                            metodoSeleccionado = MetodosPago.TARJETA_DEBITO;
                        } else if (probMetodo < 0.85) {
                            metodoSeleccionado = MetodosPago.TARJETA_CREDITO;
                        } else {
                            metodoSeleccionado = MetodosPago.TRANSFERENCIA;
                        }

                        String banco = null, marcaTarjeta = null, ultimos4 = null, referencia = null;

                        String identificadorCliente = (cliente != null)
                                ? cliente.getDni()
                                : "CONSUMIDOR_FINAL";

                        switch (metodoSeleccionado) {
                            case TARJETA_CREDITO:
                            case TARJETA_DEBITO:
                                banco = bancos[random.nextInt(bancos.length)];
                                marcaTarjeta = marcasTarjetas[random.nextInt(marcasTarjetas.length)];
                                ultimos4 = String.valueOf(random.nextInt(9000) + 1000);
                                referencia = "REF-2024" + String.format("%02d", mes) +
                                        String.format("%03d", i);
                                break;
                            case TRANSFERENCIA:
                                banco = bancos[random.nextInt(bancos.length)];
                                referencia = "TRF-2024-" + String.format("%02d", mes) + "-" +
                                        UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                                break;
                            case EFECTIVO:
                            default:
                                referencia = "TKT-2024-" + String.format("%02d", mes) + "-" +
                                        String.format("%04d", totalVentasGeneradas + 1);
                                break;
                        }

                        Pago pago = new Pago(identificadorCliente,
                                venta.getMontoTotal(),
                                marcaTarjeta,
                                banco,
                                referencia,
                                BigDecimal.ZERO,
                                ultimos4,
                                null,
                                metodoSeleccionado);

                        pago.setFechaPago(fechaVenta);

                        venta.asociarPago(pago);

                        ventaRepuestoServ.cargarVenta(venta, pago);
                        totalVentasGeneradas++;

                    } catch (Exception e) {
                        System.err.println("Error al generar venta en mes " + mes + ": " + e.getMessage());
                    }
                }

                System.out.println("✓ Mes " + String.format("%02d", mes) + "/2024 completado. " +
                        "Ventas acumuladas: " + totalVentasGeneradas);
            }

            System.out.println("\n===========================================");
            System.out.println("RESUMEN FINAL:");
            System.out.println("- Año procesado: 2024 (completo)");
            System.out.println("- Total de ventas generadas: " + totalVentasGeneradas);
            System.out.println("- Promedio por mes: " + (totalVentasGeneradas / 12));
            System.out.println("===========================================\n");

        } finally {
            uow.end();  // 👈 cerrar contexto
        }
    }

    // MODIFICADO PARA ACEPTAR EL NOMBRE COMPLETO
    private Repuesto crearRepuestoParametrizado(int index, String nombreCompleto, MarcaRepuesto marca, Ubicacion u) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        String codigoBarra = "COD-" + index + "-" + uniqueSuffix;

        // Precio base aleatorio entre 5000 y 50000
        BigDecimal precio = BigDecimal.valueOf(5000 + new Random().nextInt(45000));

        // Stock inicial
        Stock stock = new Stock(null, 100.0, 5.0, "Unidad", "ESTANTE-" + (index % 10 + 1), null, u);

        Repuesto repuesto = new Repuesto(null, codigoBarra, nombreCompleto, precio, marca, stock);

        return repuestoServ.cargarRepuesto(repuesto)
                .orElseThrow(() -> new RuntimeException("Error al guardar repuesto index " + index));
    }

    @Test
    void test() {
        List<Repuesto> rs = repuestoServ.verTodos();
        System.out.println(rs.getFirst().getMarcaRepuesto().getNombreMarca());
    }
}