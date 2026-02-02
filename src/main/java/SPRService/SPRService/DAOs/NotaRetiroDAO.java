package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.filtros.FiltroNotaRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;

import java.util.Optional;

public interface NotaRetiroDAO extends GenericDAO<NotaRetiro, Long> {

    ResultadoPaginado<NotaRetiro> verTodas(int pagina, int tamanioPagina);

    ResultadoPaginado<NotaRetiro> buscarPaginado(FiltroNotaRetiro filtros, int pagina, int tamanioPagina);

    /**
     * Trae información sobre los detalles con su repuesto, stock, ubicación y marca.
     * Usado para la consulta de detalles de una nota particular, para la generación de tickets y para la eliminación.
     */
    Optional<NotaRetiro> verDetalles(Long id);
}
