package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.NotaRetiroDAO;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.time.LocalDate;
import java.util.List;

@Singleton
public class NotaRetiroServImpl implements NotaRetiroServ {

    private final NotaRetiroDAO daoNota;

    @Inject
    public NotaRetiroServImpl(NotaRetiroDAO daoNota) {
        this.daoNota = daoNota;
    }

    @Transactional
    @Override
    public List<NotaRetiro> verTodasPorFecha() {
        return daoNota.verTodasPorFecha();
    }

    @Transactional
    @Override
    public ResultadoPaginado<NotaRetiro> buscarPaginado(LocalDate fechaMin, LocalDate fechaMax,
                                                        int pagina, int tamanioPagina) {
        return daoNota.buscarPaginado(fechaMin, fechaMax, pagina, tamanioPagina);
    }

    @Transactional
    @Override
    public List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax) {
        return daoNota.buscarPorFecha(fechaMin, fechaMax);
    }

    @Transactional
    @Override
    public NotaRetiro guardarNota(NotaRetiro notaRetiro) {
        for (DetalleRetiro d : notaRetiro.getDetallesRetiroList()) {
            d.getRepuesto().getStock().salidaDeStock(d.getCantidadRetirada());
        }
        daoNota.save(notaRetiro);
        return notaRetiro;
    }

    @Transactional
    @Override
    public void cancelarNota(NotaRetiro notaRetiro) {
        notaRetiro.cancelarNota();
        for (DetalleRetiro d : notaRetiro.getDetallesRetiroList()) {
            d.getRepuesto().getStock().entradaStock(d.getCantidadRetirada());
        }

        daoNota.update(notaRetiro);
    }
}
