package tests;

import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.MetodosPago;
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
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

//TODO: SEPARAR ESTAS GENERACIONES. REPUESTOS Y VENTAS POR SEPARADOS
class RepuestosVentasTest {

    private Injector injector;
    private VentaRepuestoServ ventaRepuestoServ;
    private RepuestoServ repuestoServ;

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new PersistenceModule());
        injector.getInstance(PersistService.class).start();
        this.ventaRepuestoServ = injector.getInstance(VentaRepuestoServ.class);
        this.repuestoServ = injector.getInstance(RepuestoServ.class);
    }

    @AfterEach
    void tearDown() {
        if (injector != null) {
            injector.getInstance(PersistService.class).stop();
        }
    }

    @Test
    public void testPoblarVentasDeUnAnioCompleto() {
        System.out.println("--- Iniciando test de población para un año completo ---");
        Random random = new Random();

        // 1. PREPARAR LA MARCA (Inicialmente sin ID)
        String nombreMarcaUnica = "Marca Test " + UUID.randomUUID().toString().substring(0, 8);
        MarcaRepuesto marcaActual = new MarcaRepuesto(null, nombreMarcaUnica, new HashSet<>());

        System.out.println("Generando 50 repuestos de prueba...");
        List<Repuesto> listaRepuestos = new ArrayList<>();
        int cantidadRepuestos = 50;

        // 2. CREAR LOS REPUESTOS Y GESTIONAR LA MARCA
        for (int i = 0; i < cantidadRepuestos; i++) {

            // Llamada al método auxiliar corregido
            Repuesto repuestoGuardado = crearRepuestoParametrizado(i, marcaActual);
            listaRepuestos.add(repuestoGuardado);

            // Actualización de la marca con la que tiene el ID de la base de datos
            if (i == 0) {
                marcaActual = repuestoGuardado.getMarcaRepuesto();
                assertNotNull(marcaActual.getId(), "La marca ya debería tener ID asignado");
            }
        }

        assertEquals(50, listaRepuestos.size(), "Se deberían haber creado 50 repuestos");

        // 3. LÓGICA DE VENTAS
        int anioActual = Year.now().getValue();
        int totalVentasGeneradas = 0;

        for (int mes = 1; mes <= 12; mes++) {
            int numVentasEsteMes = random.nextInt(40);
            // System.out.println("Mes " + mes + ": generando " + numVentasEsteMes + " ventas."); // Comentado para menos ruido

            int diasEnMes = YearMonth.of(anioActual, mes).lengthOfMonth();

            for (int i = 0; i < numVentasEsteMes; i++) {
                int diaAleatorio = random.nextInt(diasEnMes) + 1;
                LocalDate fechaVenta = LocalDate.of(anioActual, mes, diaAleatorio);

                Repuesto repuestoAleatorio = listaRepuestos.get(random.nextInt(listaRepuestos.size()));
                double cantidad = 1.0 + random.nextInt(5);

                DetalleRetiro detalle = new DetalleRetiro(null, cantidad, repuestoAleatorio);
                NotaRetiro notaRetiro = new NotaRetiro(null, NotaRetiro.TipoUsoRetiro.VENTA,
                        new ArrayList<>(List.of(detalle)));
                VentaRepuesto venta = new VentaRepuesto(null, notaRetiro, new HashSet<>());
                venta.setFechaVenta(fechaVenta);

                String ticket = "TKT-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

                Pago pago = new Pago(null, ticket, venta.getMontoTotal(), null, null,
                        null, null, null, MetodosPago.EFECTIVO, null, null);

                venta.asociarPago(pago);
                ventaRepuestoServ.cargarVenta(venta);
            }
            totalVentasGeneradas += numVentasEsteMes;
        }
        System.out.println("Fin. Total ventas generadas: " + totalVentasGeneradas);
    }

    private Repuesto crearRepuestoParametrizado(int index, MarcaRepuesto marca) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String codigoBarra = "COD-" + index + "-" + uniqueSuffix;
        String nombre = "Repuesto " + index + " " + uniqueSuffix;

        BigDecimal precio = new BigDecimal("10000.00").add(new BigDecimal(index));
        Stock stock = new Stock(null, 10000.0, 5.0, "u", "A1", null, null);

        // IMPORTANTE: Usamos la 'marca' que recibimos por parámetro.
        Repuesto repuesto = new Repuesto(null, codigoBarra, nombre, precio, marca, stock);

        // --- CORRECCIÓN PARA EL OPTIONAL ---
        // Como el servicio ahora devuelve Optional<Repuesto>, usamos .orElseThrow()
        // Esto devuelve el Repuesto si existe, o lanza un error si vino vacío (fallo el test)
        return repuestoServ.cargarRepuesto(repuesto)
                .orElseThrow(() -> new RuntimeException("Error en test: El servicio devolvió un Optional vacío al guardar repuesto index " + index));
    }
}