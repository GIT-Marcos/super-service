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

    List<RepuestoRetiradoReporteDTO> repuestosMasRetiradosParaVenta(Integer cantidad, LocalDate fechaMin,
                                                                    LocalDate fechaMax);

    Repuesto cargarRepuesto(Repuesto repuesto);

    Repuesto modificarRepuesto(Repuesto repuesto);

    void borrarRepuesto(Repuesto repuesto);

    @Deprecated
    List<Repuesto> buscarConFiltros(String inputParaBuscar, Integer opcionBusqueda,
                                    Boolean stockNormal, Boolean stockBajo, String nombreColumnaOrnenar,
                                    Integer tipoOrden);
}
