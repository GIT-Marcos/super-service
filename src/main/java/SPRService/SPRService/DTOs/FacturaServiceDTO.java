package SPRService.SPRService.DTOs;

import SPRService.SPRService.entities.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record FacturaServiceDTO(
        String idService,
        BigDecimal montoTotal,
        BigDecimal montoFaltante,
        BigDecimal montoPagado,
        BigDecimal totalTrabajos,
        BigDecimal totalRepuestos,
        Set<Trabajo> trabajos,
        Set<DetalleRetiro> detalles,
        LocalDateTime fechaCarga,
        LocalDateTime fechaEntrega,
        String motivoIngreso,
        String informeTecnico,
        String patente,
        String nroChasis,
        String nroMotor,
        String color,
        String modeloAnioCilindrada,
        String marca,
        String rutaImgLogoMarca,
        String dniNombreApellidoCliente
) {

    // Constructor personalizado que acepta la entidad Service
    public FacturaServiceDTO(Service service) {
        this(
                // idService
                String.valueOf(service.getId()),

                // montos service
                service.getMontoTotal(),
                service.getMontoFaltante(),

                // monto pagado
                service.getPagos().stream()
                        .map(Pago::getMontoPagado)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),

                // montos orden
                service.getOrden().getTotalTrabajos(),
                service.getOrden().getTotalRepuestos(),

                // trabajos (ya es un Set en la entidad)
                service.getOrden().getTrabajos(),

                // detalles (En Orden es NotaRetiro -> List, aquí pide Set. Validamos null)
                (service.getOrden().getNotaRetiro() != null)
                        ? new HashSet<>(service.getOrden().getNotaRetiro().getDetallesRetiroList())
                        : new HashSet<>(),

                // fechas
                service.getFechaCarga(),
                service.getFechaEntrega(),

                // datos orden
                service.getOrden().getMotivoIngreso(),
                service.getOrden().getInformeTecnico(),

                // datos vehículo
                service.getOrden().getVehiculo().getPatente(),
                service.getOrden().getVehiculo().getNroChasis(),
                service.getOrden().getVehiculo().getNroMotor(),
                service.getOrden().getVehiculo().getColor(),

                // Concatenación Modelo + Año + Cilindrada
                generarModeloAnioCilindrada(service.getOrden().getVehiculo().getModeloVehiculo()),

                // Marca
                service.getOrden().getVehiculo().getModeloVehiculo().getMarcaVehiculo().getNombreMarca(),
                service.getOrden().getVehiculo().getModeloVehiculo().getMarcaVehiculo().getRutaLogo(),

                // Concatenación Cliente
                generarInfoCliente(service.getCliente())
        );
    }

    // Métodos auxiliares privados para mantener el constructor limpio y legible

    private static String generarModeloAnioCilindrada(ModeloVehiculo modelo) {
        if (modelo == null) return "Desconocido";
        return modelo.getNombreModelo() + " " + modelo.getAnio() + " (" + modelo.getCilindrada() + "cc)";
    }

    private static String generarInfoCliente(Cliente cliente) {
        if (cliente == null) return "Sin Cliente";
        return cliente.getDni() + " - " + cliente.getNombre() + " " + cliente.getApellido();
    }
}