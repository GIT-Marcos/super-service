package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.NotaRetiroDAO;
import SPRService.SPRService.DTOs.filtros.FiltroNotaRetiro;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.services.StockServ;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.Optional;

@Singleton
public class NotaRetiroServImpl implements NotaRetiroServ {

    private final NotaRetiroDAO daoNota;
    private final StockServ daoStock;

    @Inject
    public NotaRetiroServImpl(NotaRetiroDAO daoNota, StockServ daoStock) {
        this.daoNota = daoNota;
        this.daoStock = daoStock;
    }

    @Transactional
    @Override
    public ResultadoPaginado<NotaRetiro> verTodas(int pagina, int tamanioPaginado) {
        return daoNota.verTodas(pagina, tamanioPaginado);
    }

    @Transactional
    @Override
    public ResultadoPaginado<NotaRetiro> buscarPaginado(FiltroNotaRetiro filtros, int pagina, int tamanioPagina) {
        return daoNota.buscarPaginado(filtros, pagina, tamanioPagina);
    }

    @Transactional
    @Override
    public Optional<NotaRetiro> verDetalle(Long id) {
        return daoNota.verDetalles(id);
    }

    @Transactional
    @Override
    public NotaRetiro guardarNota(NotaRetiro notaRetiro) {
        notaRetiro.getDetallesRetiro().forEach(detalleRetiro -> {
            daoStock.quitarExistente(detalleRetiro.getRepuesto().getStock(), detalleRetiro.getCantidadRetirada());
        });
        daoNota.save(notaRetiro);
        return notaRetiro;
    }

    @Transactional
    @Override
    public void cancelarNota(Long id) {
        Optional<NotaRetiro> result = verDetalle(id);
        result.ifPresent(managedNota -> {
            managedNota.cancelarNota();
            for (DetalleRetiro d : managedNota.getDetallesRetiro()) {
                d.getRepuesto().getStock().entradaStock(d.getCantidadRetirada());
            }
        });
    }
}
