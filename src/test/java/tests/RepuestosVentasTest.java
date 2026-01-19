package tests;

import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.MetodosPago;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.services.VentaRepuestoServ;
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
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RepuestosVentasTest {

    private Injector injector;
    private VentaRepuestoServ ventaRepuestoServ;
    private RepuestoServ repuestoServ;
    private ClienteServ clienteServ;

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
        System.out.println("--- Iniciando población de Ventas con Pagos Variados ---");

        List<Repuesto> listaRepuestos = repuestoServ.verTodos();
        if (listaRepuestos.isEmpty()) {
            fail("No hay repuestos en la base de datos. Ejecuta primero 'poblarRepuestos'.");
        }

        // Clientes cargados en BD (puede estar vacío: en ese caso todas las ventas serán consumidor final)
        List<Cliente> listaClientes = clienteServ.getAllActive();
        if (listaClientes.isEmpty()) {
            fail("No hay clientes en la base de datos. Ejecuta el test para poblar clientes.");
        }

        Random random = new Random();
        int anioActual = Year.now().getValue();
        int totalVentasGeneradas = 0;

        String[] bancos = {"Banco Galicia", "Santander", "BBVA", "Banco Nación", "ICBC", "Macro"};
        String[] marcasTarjetas = {"Visa", "Mastercard", "Amex", "Cabal"};

        for (int mes = 1; mes <= 12; mes++) {
            int numVentasEsteMes = random.nextInt(40);
            int diasEnMes = YearMonth.of(anioActual, mes).lengthOfMonth();

            if (mes == LocalDate.now().getMonthValue() && anioActual == LocalDate.now().getYear()) {
                diasEnMes = LocalDate.now().getDayOfMonth();
            } else if (mes > LocalDate.now().getMonthValue() && anioActual == LocalDate.now().getYear()) {
                continue;
            }

            for (int i = 0; i < numVentasEsteMes; i++) {
                try {
                    int diaAleatorio = random.nextInt(diasEnMes) + 1;
                    int horaAleatoria = random.nextInt(24);
                    int minutoAleatorio = random.nextInt(60);

                    LocalDateTime fechaVenta = LocalDateTime.of(
                            anioActual, mes, diaAleatorio, horaAleatoria, minutoAleatorio
                    );

                    Repuesto repuestoAleatorio = listaRepuestos.get(random.nextInt(listaRepuestos.size()));

                    // Cantidad entre 1 y 4
                    double cantidad = 1.0 + random.nextInt(4);

                    DetalleRetiro detalle = new DetalleRetiro(null, cantidad, repuestoAleatorio);
                    NotaRetiro notaRetiro = new NotaRetiro(
                            null,
                            NotaRetiro.TipoUsoRetiro.VENTA,
                            new ArrayList<>(List.of(detalle))
                    );

                    // Cliente aleatorio o consumidor final (null)
                    // Aproximadamente 50% de ventas con cliente y 50% consumidor final
                    Cliente cliente = null;
                    if (!listaClientes.isEmpty() && random.nextBoolean()) {
                        cliente = listaClientes.get(random.nextInt(listaClientes.size()));
                    }

                    // Constructor nuevo de VentaRepuesto: calcula montoTotal, montoFaltante, estado, etc.
                    VentaRepuesto venta = new VentaRepuesto(null, notaRetiro, new HashSet<>(), cliente);
                    venta.setFechaVenta(fechaVenta);

                    MetodosPago metodoSeleccionado = MetodosPago.values()[random.nextInt(MetodosPago.values().length)];

                    String banco = null, marcaTarjeta = null, ultimos4 = null, referencia = null;

                    // Si hay cliente, usamos su DNI; si no, marcamos como consumidor final
                    String identificadorCliente = (cliente != null)
                            ? cliente.getDni()
                            : "CONSUMIDOR_FINAL";

                    switch (metodoSeleccionado) {
                        case TARJETA_CREDITO:
                        case TARJETA_DEBITO:
                            banco = bancos[random.nextInt(bancos.length)];
                            marcaTarjeta = marcasTarjetas[random.nextInt(marcasTarjetas.length)];
                            ultimos4 = String.valueOf(random.nextInt(9000) + 1000);
                            referencia = "REF-" + random.nextInt(999999);
                            break;
                        case TRANSFERENCIA:
                            banco = bancos[random.nextInt(bancos.length)];
                            referencia = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                            break;
                        case EFECTIVO:
                        default:
                            referencia = "TKT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                            break;
                    }

                    // Pago por el monto total de la venta (queda como PAGADO)
                    Pago pago = new Pago(
                            null,
                            identificadorCliente,
                            venta.getMontoTotal(),
                            marcaTarjeta,
                            banco,
                            referencia,
                            BigDecimal.ZERO,   // descuentos u otros importes, si los hubiera
                            ultimos4,
                            null,
                            metodoSeleccionado,
                            null,
                            null
                    );
                    pago.setFechaPago(fechaVenta);

                    // Actualiza montoFaltante y estadoVenta internamente
                    venta.asociarPago(pago);

                    ventaRepuestoServ.cargarVenta(venta);
                    totalVentasGeneradas++;

                } catch (Exception e) {
                    System.err.println("Error al generar venta: " + e.getMessage());
                }
            }
            System.out.println("Mes " + mes + " procesado. Ventas acumuladas: " + totalVentasGeneradas);
        }
        System.out.println("--- Fin población Ventas. Total generadas: " + totalVentasGeneradas + " ---");
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
}