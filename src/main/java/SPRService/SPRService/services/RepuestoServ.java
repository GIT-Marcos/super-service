package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.DTOs.filtros.FiltroRepuestoDTO;
import SPRService.SPRService.entities.Repuesto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RepuestoServ {

    List<Repuesto> verTodos();

    Long contarStockBajo();

    List<Repuesto> buscarRepuestos(FiltroRepuestoDTO filtro);

    Optional<Repuesto> cargarRepuesto(Repuesto repuesto);

    Optional<Repuesto> modificarRepuesto(Repuesto repuesto);

    void borrarRepuesto(Repuesto repuesto);

    // ____   ____ ____    ___   ____  ______  ____  __
    // || \\ ||    || \\  // \\  || \\ | || | ||    (( \
    // ||_// ||==  ||_// ((   )) ||_//   ||   ||==   \\
    // || \\ ||___ ||     \\_//  || \\   ||   ||___ \_))

    List<RepuestoRetiradoReporteDTO> repuestosMasRetiradosParaVenta(Integer cantidad, LocalDate fechaMin,
                                                                    LocalDate fechaMax);

    /**
     * Genera reporte del uso de repuestos, si para venta o service.
     */
    ReporteUsoDeRepuestosDTO usoDeRepuestos(LocalDate fechaMin, LocalDate fechaMax);
}
