package SPRService.SPRService.controllers.charts;

import SPRService.SPRService.DTOs.ClientesMasIngresosDTO;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.GeneradorImagenes;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.apache.commons.mail.EmailException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ChartClientesMasIngresosController implements Initializable {

    private final Navigator navigator;
    private final ClienteServ clienteServ;
    private final EMailSender eMailSender;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.ROOT);

    // Mapa para almacenar los datos de cada cliente y acceder desde el popup
    private Map<String, ClientesMasIngresosDTO> datosClientesMap = new HashMap<>();

    @FXML
    private BorderPane rootPane;
    @FXML
    private BarChart<Number, String> chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private CategoryAxis yAxis;
    @FXML
    private DatePicker dpFechaMin, dpFechaMax;
    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private Label lblTotalGeneral, lblTotalServices, lblTotalVentas, lblTotalTransacciones;

    @Inject
    public ChartClientesMasIngresosController(AppCoordinator coordinator, ClienteServ clienteServ, EMailSender eMailSender) {
        this.navigator = coordinator.getMainNavigator();
        this.clienteServ = clienteServ;
        this.eMailSender = eMailSender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 10);
        spinner.setValueFactory(valueFactory);
        configurarDatePickers();
    }

    private void configurarDatePickers() {
        dpFechaMax.setValue(LocalDate.now());
        dpFechaMin.setValue(LocalDate.now().minusYears(1));
    }

    @FXML
    private void generar() {
        if (dpFechaMin.getValue() == null) {
            dpFechaMin.setValue(LocalDate.now().minusYears(5));
        }
        if (dpFechaMax.getValue() == null) {
            dpFechaMax.setValue(LocalDate.now());
        }

        LocalDate fechaMin = dpFechaMin.getValue();
        LocalDate fechaMax = dpFechaMax.getValue();
        Integer cantidad = spinner.getValue();

        if (fechaMin.isAfter(fechaMax)) {
            NotificationHelper.mostrarAdvertencia("Rango Incorrecto",
                    "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        try {
            // Siempre incluir ambos tipos de ingresos
            List<ClientesMasIngresosDTO> datos = clienteServ.generarReporteClientesMasIngresos(
                    cantidad, fechaMin, fechaMax);

            if (datos.isEmpty()) {
                NotificationHelper.mostrarAdvertencia("Sin datos",
                        "No se encontraron registros para los filtros seleccionados.");
                limpiarGrafico();
                return;
            }

            // Limpiar mapa y poblar con nuevos datos
            datosClientesMap.clear();
            datos.forEach(dto -> datosClientesMap.put(generarClaveCliente(dto), dto));

            generarGrafico(datos);
            actualizarResumen(datos);

            NotificationHelper.mostrarExito("Reporte generado",
                    "Se ha generado el reporte con " + datos.size() + " cliente(s).\nHaga clic en una barra para ver detalles.");

        } catch (Exception e) {
            NotificationHelper.mostrarError("Error al generar gráfico",
                    "No se pudieron cargar los datos de clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generarClaveCliente(ClientesMasIngresosDTO dto) {
        return dto.idCliente() + "_" + dto.documentoCliente();
    }

    private void generarGrafico(List<ClientesMasIngresosDTO> datos) {
        chart.getData().clear();
        chart.layout();

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("Ingresos Totales");

        // Iterar en reversa para que el TOP 1 quede arriba
        for (int i = datos.size() - 1; i >= 0; i--) {
            ClientesMasIngresosDTO dto = datos.get(i);

            String labelEjeY = String.format("%s (%s)",
                    dto.getNombreCompleto(),
                    dto.documentoCliente());

            XYChart.Data<Number, String> data = new XYChart.Data<>(dto.totalIngresos(), labelEjeY);

            // Configurar tooltip y evento de clic
            configurarNodoBarra(data, dto);

            series.getData().add(data);
        }

        chart.getData().add(series);
    }

    private void configurarNodoBarra(XYChart.Data<Number, String> data, ClientesMasIngresosDTO dto) {
        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                // Tooltip rápido
                String textoTooltip = String.format(
                        "💰 Total: %s\n📊 Clic para ver detalles",
                        currencyFormat.format(dto.totalIngresos())
                );
                Tooltip tooltip = new Tooltip(textoTooltip);
                tooltip.setStyle("-fx-font-size: 12px;");
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(newNode, tooltip);

                // Estilo del cursor
                newNode.setStyle("-fx-cursor: hand;");

                // Evento de clic para mostrar popup
                newNode.setOnMouseClicked(event -> mostrarPopupDetalles(dto));

                // Efecto hover
                newNode.setOnMouseEntered(e -> newNode.setOpacity(0.8));
                newNode.setOnMouseExited(e -> newNode.setOpacity(1.0));
            }
        });
    }

    private void mostrarPopupDetalles(ClientesMasIngresosDTO dto) {
        navigator.openModal(Views.STATS_CLIENTE, "Estadísticas de cliente", dto);
    }

    private void actualizarResumen(List<ClientesMasIngresosDTO> datos) {
        BigDecimal totalGeneral = datos.stream()
                .map(ClientesMasIngresosDTO::totalIngresos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalServices = datos.stream()
                .map(ClientesMasIngresosDTO::totalServices)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVentas = datos.stream()
                .map(ClientesMasIngresosDTO::totalVentas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalTransacciones = datos.stream()
                .mapToInt(ClientesMasIngresosDTO::totalTransacciones)
                .sum();

        lblTotalGeneral.setText("Total General: " + currencyFormat.format(totalGeneral));
        lblTotalServices.setText("Total Services: " + currencyFormat.format(totalServices));
        lblTotalVentas.setText("Total Ventas: " + currencyFormat.format(totalVentas));
        lblTotalTransacciones.setText("Total Transacciones: " + totalTransacciones);
    }

    private void limpiarGrafico() {
        chart.getData().clear();
        datosClientesMap.clear();
        lblTotalGeneral.setText("Total General: $0.00");
        lblTotalServices.setText("Total Services: $0.00");
        lblTotalVentas.setText("Total Ventas: $0.00");
        lblTotalTransacciones.setText("Total Transacciones: 0");
    }

    @FXML
    private void exportarJPG(ActionEvent event) {
        if (chart.getData().isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Sin datos",
                    "Primero debe generar un reporte para exportar.");
            return;
        }
        GeneradorImagenes.exportarJPEG(event, rootPane, "Reporte_Clientes_Mas_Ingresos");
    }

    @FXML
    private void enviarMail() {
        if (chart.getData().isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Sin datos",
                    "Primero debe generar un reporte para enviar.");
            return;
        }

        String destinatario = SimpleDialogs.pedirMailParaEnviarReporte();
        if (destinatario == null || destinatario.isBlank()) return;

        try {
            ManejadorInputs.eMail(destinatario, true);
            enviarSnapshotPorCorreo(destinatario);
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Dirección de correo inválida", e.getMessage());
        }
    }

    private void enviarSnapshotPorCorreo(String destinatario) {
        try {
            File tempFile = GeneradorImagenes.tomarScreenshotTemporalDeVista(rootPane);

            String asunto = "Reporte de Clientes con Mayores Ingresos";
            String cuerpo = String.format("""
                            Estimado,
                            
                            Adjunto encontrará el gráfico de los clientes con mayores ingresos registrados en el sistema.
                            
                            📅 Período: %s al %s
                            👥 Top: %d clientes
                            📊 Incluye: Services y Ventas Particulares
                            
                            Resumen:
                            %s
                            %s
                            %s
                            %s
                            
                            Saludos cordiales.
                            """,
                    dpFechaMin.getValue(),
                    dpFechaMax.getValue(),
                    spinner.getValue(),
                    lblTotalGeneral.getText(),
                    lblTotalServices.getText(),
                    lblTotalVentas.getText(),
                    lblTotalTransacciones.getText()
            );

            eMailSender.enviarEmailApache(destinatario, asunto, cuerpo, tempFile);

            NotificationHelper.mostrarExito("Reporte enviado",
                    "El reporte se envió correctamente a " + destinatario);

            tempFile.deleteOnExit();

        } catch (IOException e) {
            NotificationHelper.mostrarError("Error de IO",
                    "No se pudo generar la imagen temporal: " + e.getMessage());
            e.printStackTrace();
        } catch (EmailException e) {
            NotificationHelper.mostrarError("Error Mail",
                    "Fallo al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}