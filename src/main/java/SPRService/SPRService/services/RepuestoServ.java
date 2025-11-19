package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.entities.Repuesto;

import java.time.LocalDate;
import java.util.List;

public interface RepuestoServ {

    List<Repuesto> verTodos();

    Long contarStockBajo();

    List<Repuesto> buscarConCriteria(String codBarras, String nombreProd, String marcaProd,
                                     Boolean verStockNormal, Boolean verStockBajo,
                                     String colParaOrdenar, Integer tipoOrden);

    Repuesto cargarRepuesto(Repuesto repuesto);

    Repuesto modificarRepuesto(Repuesto repuesto);

    void borrarRepuesto(Repuesto repuesto);

    // ____   ____ ____    ___   ____  ______  ____  __
    // || \\ ||    || \\  // \\  || \\ | || | ||    (( \
    // ||_// ||==  ||_// ((   )) ||_//   ||   ||==   \\
    // || \\ ||___ ||     \\_//  || \\   ||   ||___ \_))

    List<RepuestoRetiradoReporteDTO> repuestosMasRetiradosParaVenta(Integer cantidad, LocalDate fechaMin,
                                                                    LocalDate fechaMax);

}
