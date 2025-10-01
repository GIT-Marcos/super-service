package SPRService.SPRService.services;

import SPRService.SPRService.entities.NotaRetiro;

import java.time.LocalDate;
import java.util.List;

public interface NotaRetiroServ {

    List<NotaRetiro> verTodasPorFecha();

    List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax);

    NotaRetiro guardarNota(NotaRetiro notaRetiro);

    void cancelarNota(NotaRetiro notaRetiro);

}
