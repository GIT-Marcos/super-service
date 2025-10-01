package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Repuesto;

import java.time.LocalDate;
import java.util.List;

public interface RepuestoDAO extends GenericDAO<Repuesto, Long> {

    /////////////////////LECTURA

    List<Repuesto> allActiveProducts();

    /**
     * Cuenta los repuestos que tienen menor stock existente que stock mínimo
     * para avisos en GUI.
     *
     * @return cantidad de repuestos con stock bajo.
     */
    Long cuentaRespBajoStock();

    /**
     * Para saber como proceder en la carga o modificación de un producto.
     *
     * @param codBarra a consultar.
     * @return null: si el producto que se quiere cargar no existe ya; true: si
     * el producto ya existe y está activo; false: si el producto ya existe pero
     * con borrado lógico.
     */
    List<Boolean> consultaEstado(String codBarra);

    Long consultarId(String codBarra);

    /**
     * @param stockNormal si el stock existente es MAYOR q el mínimo
     * @param stockBajo   si el stock existente es MENOR q el mínimo
     */
    @Deprecated
    List<Repuesto> buscarConFiltros(String inputParaBuscar, Integer opcionBusqueda,
                                    Boolean stockNormal, Boolean stockBajo, String nombreColumnaOrnenar, Integer tipoOrden);

    List<Repuesto> buscarConCriteria(String codBarras, String nombreProd, String marcaProd,
                                     Boolean verStockNormal, Boolean verStockBajo, String colParaOrdenar,
                                     Integer tipoOrden);

    /**
     * Para generar un reporte.
     * Qué productos son los que más veces se retiran entre fechas.
     */
    List<Object[]> masRetiradosParaVenta(Integer cantidad, LocalDate fechaInicio, LocalDate fechaFin);

    //////////////////////ESCRITURA

    void borradoLogico(Repuesto repuesto);
}
