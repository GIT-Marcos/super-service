package SPRService.SPRService.DTOs;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.entities.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketRetiroServiceDTO(

        String idService,
        BigDecimal montoTotal,
        LocalDateTime fechaCarga,
        LocalDateTime fechaEntrega,
        String patente,
        String modeloAnioCilindrada,
        String marca,
        String dniNombreApellidoCliente

) {

    public TicketRetiroServiceDTO(Service service) {
        this(
                String.valueOf(service.getId()),
                service.getMontoTotal(),
                service.getFechaCarga(),
                service.getFechaEntrega(),
                service.getOrden().getVehiculo().getPatente(),
                generarModeloAnioCilindrada(service.getOrden().getVehiculo().getModeloVehiculo()),
                service.getOrden().getVehiculo().getModeloVehiculo().getMarcaVehiculo().getNombreMarca(),
                generarInfoCliente(service.getCliente())
        );
    }

    private static String generarModeloAnioCilindrada(ModeloVehiculo modelo) {
        if (modelo == null) return "Desconocido";
        return modelo.getNombreModelo() + " " + modelo.getAnio() + " (" + modelo.getCilindrada() + "cc)";
    }

    private static String generarInfoCliente(Cliente cliente) {
        if (cliente == null) return "Sin Cliente";
        return cliente.getDni() + " - " + cliente.getNombre() + " " + cliente.getApellido();
    }
}
