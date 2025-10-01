package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.NotaRetiroDAO;
import SPRService.SPRService.DAOs.StockDAO;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.services.NotaRetiroServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class NotaRetiroServImpl implements NotaRetiroServ {

    private final NotaRetiroDAO daoNota;
    private final StockDAO daoStock;

    @Inject
    public NotaRetiroServImpl(NotaRetiroDAO daoNota, StockDAO daoStock) {
        this.daoNota = daoNota;
        this.daoStock = daoStock;
    }

    @Transactional
    @Override
    public List<NotaRetiro> verTodasPorFecha() {
        return daoNota.verTodasPorFecha();
    }

    @Transactional
    @Override
    public List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax) {
        return daoNota.buscarPorFecha(fechaMin, fechaMax);
    }

    @Transactional
    @Override
    public NotaRetiro guardarNota(NotaRetiro notaRetiro) {
        // la cantidad del stock se actualiza al crearse el objeto DetalleRetiro.
        List<Stock> stocksParaActualizar = new ArrayList<>();
        for (DetalleRetiro d : notaRetiro.getDetallesRetiroList()) {
            stocksParaActualizar.add(d.getRepuesto().getStock());
        }
        daoNota.save(notaRetiro);
        daoStock.update(stocksParaActualizar);
        return notaRetiro;
    }

    @Transactional
    @Override
    public void cancelarNota(NotaRetiro notaRetiro) {
        notaRetiro.cancelarNota();
        List<Stock> stockParaRestablecer = new ArrayList<>();
        for (DetalleRetiro d : notaRetiro.getDetallesRetiroList()) {
            stockParaRestablecer.add(d.getRepuesto().getStock());
        }
        daoStock.update(stockParaRestablecer);
        daoNota.update(notaRetiro);
    }
}
