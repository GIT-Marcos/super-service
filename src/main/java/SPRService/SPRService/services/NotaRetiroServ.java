package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.filtros.FiltroNotaRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;

import java.util.Optional;

public interface NotaRetiroServ {

    ResultadoPaginado<NotaRetiro> verTodas(int pagina, int tamanioPaginado);

    ResultadoPaginado<NotaRetiro> buscarPaginado(FiltroNotaRetiro filtros, int pagina, int tamanioPagina);

    Optional<NotaRetiro> verDetalle(Long id);

    NotaRetiro guardarNota(NotaRetiro notaRetiro);

    void cancelarNota(Long id);

}
