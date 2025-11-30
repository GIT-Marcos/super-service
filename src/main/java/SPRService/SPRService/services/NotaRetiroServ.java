package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.filtros.FiltroNotaRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;

public interface NotaRetiroServ {

    ResultadoPaginado<NotaRetiro> buscarPaginado(FiltroNotaRetiro filtros, int pagina, int tamanioPagina);

    NotaRetiro guardarNota(NotaRetiro notaRetiro);

    void cancelarNota(NotaRetiro notaRetiro);

}
