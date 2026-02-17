package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.ClientesMasIngresosDTO;
import SPRService.SPRService.navigation.DataReceiver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class EstadisticasClienteController implements Initializable, DataReceiver<ClientesMasIngresosDTO> {

    @FXML
    private VBox rootPane;
    @FXML
    private Label lblNombreCliente, lblDocumento, lblCantidadServices, lblTotalServices, lblCantidadVentas,
            lblTotalVentas, lblTotalIngresos, lblPorcentajeServices, lblTotalOperaciones, lblPorcentajeVentas,
            lblSinIngresos;
    @FXML
    private GridPane gridDetalles;
    @FXML
    private HBox totalesBox;
    @FXML
    private HBox porcentajesBox;

    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));

    @Override
    public void receiveData(ClientesMasIngresosDTO data) {
        // Información del cliente
        lblNombreCliente.setText("👤 Cliente: " + data.getNombreCompleto());
        lblDocumento.setText("Documento: " + data.documentoCliente());

        // Cantidades
        lblCantidadServices.setText(String.valueOf(data.cantidadServices()));
        lblCantidadVentas.setText(String.valueOf(data.cantidadVentas()));

        // Totales monetarios
        lblTotalServices.setText(formatoMoneda.format(data.totalServices()));
        lblTotalVentas.setText(formatoMoneda.format(data.totalVentas()));
        lblTotalIngresos.setText(formatoMoneda.format(data.totalIngresos()));

        // Total operaciones
        lblTotalOperaciones.setText(String.valueOf(data.totalTransacciones()));

        // Calcular y mostrar porcentajes
        calcularPorcentajes(data);

        // Mostrar/ocultar mensaje sin ingresos
        boolean tieneIngresos = data.totalIngresos().compareTo(BigDecimal.ZERO) > 0;
        lblSinIngresos.setVisible(!tieneIngresos);
        lblSinIngresos.setManaged(!tieneIngresos);

        gridDetalles.setVisible(tieneIngresos);
        gridDetalles.setManaged(tieneIngresos);
        totalesBox.setVisible(tieneIngresos);
        totalesBox.setManaged(tieneIngresos);
        porcentajesBox.setVisible(tieneIngresos);
        porcentajesBox.setManaged(tieneIngresos);
    }

    private void calcularPorcentajes(ClientesMasIngresosDTO data) {
        BigDecimal totalIngresos = data.totalIngresos();

        if (totalIngresos.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal porcentajeServices = data.totalServices()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalIngresos, 1, RoundingMode.HALF_UP);

            BigDecimal porcentajeVentas = data.totalVentas()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalIngresos, 1, RoundingMode.HALF_UP);

            lblPorcentajeServices.setText("📋 Services: " + porcentajeServices + "%");
            lblPorcentajeVentas.setText("🛒 Ventas: " + porcentajeVentas + "%");
        } else {
            lblPorcentajeServices.setText("📋 Services: 0.0%");
            lblPorcentajeVentas.setText("🛒 Ventas: 0.0%");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}