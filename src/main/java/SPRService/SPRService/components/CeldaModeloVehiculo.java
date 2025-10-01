package SPRService.SPRService.components;

import SPRService.SPRService.entities.MarcaVehiculo;
import SPRService.SPRService.entities.ModeloVehiculo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CeldaModeloVehiculo extends ListCell<ModeloVehiculo> {
    // Contenedor principal: BorderPane para un layout más compacto
    private final BorderPane contentBox;

    // Componentes visuales
    private final ImageView logoMarcaImageView;
    private static final Map<String, Image> logoCache = new HashMap<>();
    private final Label nombreModeloLabel;
    private final Label anioLabel;
    private final Label cilindradaLabel;

    // Contenedor para la información de la derecha (año y cilindrada)
    private final VBox rightDetailsBox;

    public CeldaModeloVehiculo() {
        super();

        setPadding(Insets.EMPTY);
        // --- Inicialización de componentes gráficos ---
        logoMarcaImageView = new ImageView();
        logoMarcaImageView.setFitWidth(60);
        logoMarcaImageView.setFitHeight(60);
        logoMarcaImageView.setPreserveRatio(true);

        nombreModeloLabel = new Label();
        nombreModeloLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Hacemos el texto secundario un poco más pequeño para que encaje mejor
        anioLabel = new Label();
        anioLabel.setFont(Font.font("Arial", 12));
        anioLabel.setTextFill(Color.DARKSLATEGRAY);

        cilindradaLabel = new Label();
        cilindradaLabel.setFont(Font.font("Arial", 12));
        cilindradaLabel.setTextFill(Color.DARKSLATEGRAY);

        // --- Configuración del nuevo Layout con BorderPane ---

        // 1. Contenedor para los detalles de la derecha (año y cilindrada)
        rightDetailsBox = new VBox(2); // Espacio vertical mínimo entre año y cc
        rightDetailsBox.setAlignment(Pos.CENTER_RIGHT); // ALINEA EL CONTENIDO a la derecha
        rightDetailsBox.getChildren().addAll(anioLabel, cilindradaLabel);

        // 2. Contenedor principal BorderPane
        contentBox = new BorderPane();
        contentBox.setPadding(new Insets(0, 10, 0, 10)); // Margen interior

        // 3. Asignar componentes a las regiones del BorderPane
        contentBox.setLeft(logoMarcaImageView);
        contentBox.setCenter(nombreModeloLabel);
        contentBox.setRight(rightDetailsBox);

        // 4. Añadir márgenes para que los componentes no estén pegados
        BorderPane.setAlignment(nombreModeloLabel, Pos.CENTER_LEFT);
        BorderPane.setMargin(logoMarcaImageView, new Insets(0, 15, 0, 0)); // Margen derecho para el logo
    }

    @Override
    protected void updateItem(ModeloVehiculo modelo, boolean empty) {
        super.updateItem(modelo, empty);

        if (empty || modelo == null) {
            setGraphic(null);
        } else {
            // 1. Establecer la información de texto del modelo
            nombreModeloLabel.setText(modelo.getNombreModelo());
            anioLabel.setText(modelo.getAnio().toString()); // Solo el año, sin "Año:" para ahorrar espacio

            // 2. Lógica para la cilindrada
            if (modelo.getCilindrada() == 0.0) {
                cilindradaLabel.setText("Eléctrico");
                cilindradaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12)); // Negrita para destacar
                cilindradaLabel.setTextFill(Color.rgb(0, 150, 0)); // Un verde oscuro
            } else {
                cilindradaLabel.setText(modelo.getCilindrada() + " cc");
                cilindradaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12)); // Fuente normal
                cilindradaLabel.setTextFill(Color.DARKSLATEGRAY); // Restablecer color
            }

            // --- 3. Cargar la imagen del logo usando la caché ---
            MarcaVehiculo marca = modelo.getMarcaVehiculo();
            Image logo = null;
            if (marca != null && marca.getRutaLogo() != null && !marca.getRutaLogo().isEmpty()) {
                logo = loadImageWithCache(marca.getRutaLogo());
            }
            logoMarcaImageView.setImage(logo); // Asignar la imagen (o null si no hay)

            // 4. Mostrar el contenido en la celda
            setGraphic(contentBox);
        }
    }

    /**
     * Para cargar una imagen desde la caché o desde los recursos.
     * @param rutaLogo La ruta del recurso de la imagen.
     * @return El objeto Image, o null si no se pudo cargar.
     */
    private Image loadImageWithCache(String rutaLogo) {
        // Primero, intentar obtener la imagen de la caché
        Image cachedImage = logoCache.get(rutaLogo);
        if (cachedImage != null) {
            return cachedImage;
        }

        // Si no está en la caché, cargarla desde el recurso
        try (InputStream stream = getClass().getResourceAsStream(rutaLogo)) {
            if (stream != null) {
                Image newImage = new Image(stream);
                // Guardar la imagen recién cargada en la caché para usos futuros
                logoCache.put(rutaLogo, newImage);
                return newImage;
            } else {
                System.err.println("Recurso de imagen no encontrado: " + rutaLogo);
                // Opcional: guardar null en la caché para no intentar cargarla de nuevo
                logoCache.put(rutaLogo, null);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + rutaLogo);
            e.printStackTrace();
            logoCache.put(rutaLogo, null); // Evitar reintentos fallidos
            return null;
        }
    }
}
