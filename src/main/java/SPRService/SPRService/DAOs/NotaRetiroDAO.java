package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDate;
import java.util.List;

public interface NotaRetiroDAO extends GenericDAO<NotaRetiro, Long>{

    List<NotaRetiro> verTodasPorFecha();

    ResultadoPaginado<NotaRetiro> buscarPaginado(LocalDate fechaMin, LocalDate fechaMax, int pagina, int tamanioPagina);

    List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax);
}
