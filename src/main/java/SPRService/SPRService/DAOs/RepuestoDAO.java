package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Repuesto;

import java.time.LocalDate;
import java.util.List;

public interface RepuestoDAO extends GenericDAO<Repuesto, Long> {

    // __     ____   ___ ______ __ __ ____   ___
    // ||    ||     //   | || | || || || \\ // \\
    // ||    ||==  ((      ||   || || ||_// ||=||
    // ||__| ||___  \\__   ||   \\_// || \\ || ||

    List<Repuesto> todosProductosActivos();

    /**
     * Cuenta los repuestos que tienen menor stock existente que stock mínimo
     * para avisos en GUI.
     *
     * @return cantidad de repuestos con stock bajo.
     */
    Long cuentaRespBajoStock();

    //todo: usar dto filtro y paginar
    List<Repuesto> buscarConCriteria(String codBarras, String nombreProd, String marcaProd,
                                     Boolean verStockNormal, Boolean verStockBajo, String colParaOrdenar,
                                     Integer tipoOrden);

    // ____   ____ ____    ___   ____  ______  ____  __
    // || \\ ||    || \\  // \\  || \\ | || | ||    (( \
    // ||_// ||==  ||_// ((   )) ||_//   ||   ||==   \\
    // || \\ ||___ ||     \\_//  || \\   ||   ||___ \_))

    /**
     * Para generar un reporte.
     * Qué productos son los que más veces se retiran entre fechas.
     */
    List<Object[]> masRetiradosParaVenta(Integer cantidad, LocalDate fechaInicio, LocalDate fechaFin);
}
