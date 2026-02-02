package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.DTOs.filtros.FiltroRepuestoDTO;
import SPRService.SPRService.entities.Repuesto;

import java.time.LocalDate;
import java.util.List;

public interface RepuestoDAO extends GenericDAO<Repuesto, Long> {

    // __     ____   ___ ______ __ __ ____   ___
    // ||    ||     //   | || | || || || \\ // \\
    // ||    ||==  ((      ||   || || ||_// ||=||
    // ||__| ||___  \\__   ||   \\_// || \\ || ||

    /**
     * Trae todos los repuestos con sus datos básicos para la tabla principal.
     */
    List<Repuesto> verTodos();

    List<Repuesto> validarUnicidadCodBarras(Repuesto r);

    /**
     * Cuenta los repuestos que tienen menor stock existente que stock mínimo
     * para avisos en GUI.
     *
     * @return cantidad de repuestos con stock bajo.
     */
    Long cuentaRespBajoStock();

    /**
     * Busca por filtros y trae repuestos con datos simples para tabla principal.
     */
    //todo: paginar
    List<Repuesto> buscarRepuestos(FiltroRepuestoDTO filtro);

    // ____   ____ ____    ___   ____  ______  ____  __
    // || \\ ||    || \\  // \\  || \\ | || | ||    (( \
    // ||_// ||==  ||_// ((   )) ||_//   ||   ||==   \\
    // || \\ ||___ ||     \\_//  || \\   ||   ||___ \_))

    /**
     * Para generar un reporte.
     * Qué productos son los que más veces se retiran entre fechas.
     */
    List<Object[]> masRetiradosParaVenta(Integer cantidad, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Genera reporte del uso de repuestos, si para venta o service.
     */
    ReporteUsoDeRepuestosDTO usoDeRepuestos(LocalDate fechaMin, LocalDate fechaMax);
}
