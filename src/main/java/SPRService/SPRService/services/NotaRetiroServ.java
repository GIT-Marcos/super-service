package SPRService.SPRService.services;

import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDate;

public interface NotaRetiroServ {

    ResultadoPaginado<NotaRetiro> buscarPaginado(LocalDate fechaMin, LocalDate fechaMax,
                                                 int pagina, int tamanioPagina);

    NotaRetiro guardarNota(NotaRetiro notaRetiro);

    void cancelarNota(NotaRetiro notaRetiro);

}
