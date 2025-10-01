package SPRService.SPRService.util.persistence;

import SPRService.SPRService.DAOs.*;
import SPRService.SPRService.DAOs.impl.*;
import SPRService.SPRService.services.*;
import SPRService.SPRService.services.impl.*;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.persist.jpa.JpaPersistModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PersistenceModule extends AbstractModule {

    private static final String PROPERTIES_PATH = "/db.properties";

    @Override
    protected void configure() {
        Properties dbProperties = loadProperties();
        install(new JpaPersistModule("spr-service-jpa").properties(dbProperties));

        bind(VehiculoServ.class).to(VehiculoServImpl.class).in(Scopes.SINGLETON);
        bind(VehiculoDAO.class).to(VehiculoDAOImpl.class).in(Scopes.SINGLETON);
        bind(MarcaVehiculoServ.class).to(MarcaVehiculoServImpl.class).in(Scopes.SINGLETON);
        bind(MarcaVehiculoDAO.class).to(MarcaVehiculoDAOImpl.class).in(Scopes.SINGLETON);
        bind(ModeloVehiculoServ.class).to(ModeloVehiculoServImpl.class).in(Scopes.SINGLETON);
        bind(ModeloVehiculoDAO.class).to(ModeloVehiculoDAOImpl.class).in(Scopes.SINGLETON);
        bind(RepuestoServ.class).to(RepuestoServImpl.class).in(Scopes.SINGLETON);
        bind(RepuestoDAO.class).to(RepuestoDAOImpl.class).in(Scopes.SINGLETON);
        bind(StockServ.class).to(StockServImpl.class).in(Scopes.SINGLETON);
        bind(StockDAO.class).to(StockDAOImpl.class).in(Scopes.SINGLETON);
        bind(VentaRepuestoServ.class).to(VentaRepuestoServImpl.class).in(Scopes.SINGLETON);
        bind(VentaRepuestoDAO.class).to(VentaRepuestoDAOImpl.class).in(Scopes.SINGLETON);
        bind(NotaRetiroServ.class).to(NotaRetiroServImpl.class).in(Scopes.SINGLETON);
        bind(NotaRetiroDAO.class).to(NotaRetiroDAOImpl.class).in(Scopes.SINGLETON);
        bind(UsuarioServ.class).to(UsuarioServImpl.class).in(Scopes.SINGLETON);
        bind(UsuarioDAO.class).to(UsuarioDAOImpl.class).in(Scopes.SINGLETON);
        bind(ClienteServ.class).to(ClienteServImpl.class).in(Scopes.SINGLETON);
        bind(ClienteDAO.class).to(ClienteDAOImpl.class).in(Scopes.SINGLETON);
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = PersistenceModule.class.getResourceAsStream(PROPERTIES_PATH)) {
            if (input == null) {
                System.err.println("⚠️ No se pudo encontrar el archivo " + PROPERTIES_PATH);
                throw new RuntimeException("El archivo de configuración de la base de datos no fue encontrado" +
                        " en el classpath: " + PROPERTIES_PATH);
            }
            properties.load(input);
            System.out.println("✅ Propiedades de la base de datos cargadas desde " + PROPERTIES_PATH);
        } catch (IOException ex) {
            System.err.println("❌ Error al cargar las propiedades de la base de datos.");
            throw new RuntimeException("Error de I/O al leer el archivo de propiedades de la base de datos", ex);
        }
        return properties;
    }

    // Guice persist no necesita que se le provea la SessionFactory.
     /*
    @Provides
    @Singleton
    public SessionFactory provideSessionFactory() {
        Properties props = loadProperties();

        try {
            Configuration configuration = new Configuration().configure(); // Carga hibernate.cfg.xml
            configuration.addProperties(props);

            //agregar entidades
            configuration.addAnnotatedClass(Repuesto.class);
            configuration.addAnnotatedClass(Stock.class);
            configuration.addAnnotatedClass(DetalleRetiro.class);
            configuration.addAnnotatedClass(NotaRetiro.class);
            configuration.addAnnotatedClass(Usuario.class);
            configuration.addAnnotatedClass(VentaRepuesto.class);
            configuration.addAnnotatedClass(Pago.class);
            configuration.addAnnotatedClass(AuditoriaVenta.class);
            configuration.addAnnotatedClass(Cliente.class);
            configuration.addAnnotatedClass(DatosContacto.class);
            configuration.addAnnotatedClass(Vehiculo.class);
            configuration.addAnnotatedClass(ModeloVehiculo.class);
            configuration.addAnnotatedClass(MarcaVehiculo.class);
            configuration.addAnnotatedClass(DatosIngresoAuto.class);

            SessionFactory factory = configuration.buildSessionFactory();
            System.out.println("✅ (Guice) SessionFactory creado exitosamente.");

            // Registrar un hook para cerrar la factory cuando la JVM se apague
            Runtime.getRuntime().addShutdownHook(new Thread(factory::close));

            return factory;
        } catch (Throwable ex) {
            System.err.println("❌ (Guice) Error al crear SessionFactory: " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }*/
}
