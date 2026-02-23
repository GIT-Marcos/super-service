package SPRService.SPRService.util;

import javafx.application.HostServices;

public final class ServicioNavegador {

    private static HostServices hostServices;

    private ServicioNavegador() {
    }

    public static void inicializar(HostServices hs) {
        if (hs == null) throw new IllegalArgumentException("HostServices no puede ser null");
        hostServices = hs;
    }

    public static void abrirUri(String uri) {
        verificarInicializacion();
        hostServices.showDocument(uri);
    }

    /**
     * Abre el cliente de correo por defecto del SO
     * (Outlook, Thunderbird, etc.)
     */
    public static void abrirEmail(String email) {
        abrirUri("mailto:" + email);
    }

    /**
     * Abre la app de teléfono/VoIP configurada
     * (Skype, Teams, etc.)
     */
    public static void abrirTelefono(String telefono) {
        abrirUri("tel:" + sanitizarTelefono(telefono));
    }

    /**
     * Abre WhatsApp Web o Desktop con chat directo
     * El número debe incluir código de país
     */
    public static void abrirWhatsApp(String telefono) {
        String numeroLimpio = sanitizarTelefono(telefono);
        abrirUri("https://wa.me/" + numeroLimpio);
    }

    /**
     * Abre WhatsApp con un mensaje predefinido
     */
    public static void abrirWhatsApp(String telefono, String mensaje) {
        String numeroLimpio = sanitizarTelefono(telefono);
        String mensajeCodificado = java.net.URLEncoder.encode(mensaje,
                java.nio.charset.StandardCharsets.UTF_8);
        abrirUri("https://wa.me/" + numeroLimpio + "?text=" + mensajeCodificado);
    }

    public static void abrirWeb(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        abrirUri(url);
    }

    /**
     * Elimina +, espacios, guiones y paréntesis del número
     * "+54 9 (11) 1234-5678" → "5491112345678"
     */
    private static String sanitizarTelefono(String telefono) {
        return telefono.replaceAll("[^\\d]", "");
    }

    public static boolean estaInicializado() {
        return hostServices != null;
    }

    private static void verificarInicializacion() {
        if (hostServices == null) {
            throw new IllegalStateException(
                    "ServicioNavegador no inicializado. " +
                            "Llamar a ServicioNavegador.inicializar(getHostServices()) en Application.start()"
            );
        }
    }
}