package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDate;

public interface NotaRetiroDAO extends GenericDAO<NotaRetiro, Long>{

    ResultadoPaginado<NotaRetiro> buscarPaginado(LocalDate fechaMin, LocalDate fechaMax, int pagina, int tamanioPagina);
}
