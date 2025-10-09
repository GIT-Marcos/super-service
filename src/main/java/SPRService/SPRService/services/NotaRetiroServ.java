package SPRService.SPRService.services;

import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDate;
import java.util.List;

public interface NotaRetiroServ {

    @Deprecated
    List<NotaRetiro> verTodasPorFecha();

    ResultadoPaginado<NotaRetiro> buscarPaginado(LocalDate fechaMin, LocalDate fechaMax,
                                                 int pagina, int tamanioPagina);

    @Deprecated
    List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax);

    NotaRetiro guardarNota(NotaRetiro notaRetiro);

    void cancelarNota(NotaRetiro notaRetiro);

}
