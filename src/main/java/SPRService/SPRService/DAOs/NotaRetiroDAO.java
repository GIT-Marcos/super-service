package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.NotaRetiro;

import java.time.LocalDate;
import java.util.List;

public interface NotaRetiroDAO extends GenericDAO<NotaRetiro, Long>{

    List<NotaRetiro> verTodasPorFecha();

    List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax);
}
