package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.StockDAO;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.services.StockServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class StockServImpl implements StockServ {

    private final StockDAO daoStock;

    @Inject
    public StockServImpl(StockDAO daoStock) {
        this.daoStock = daoStock;
    }

    @Transactional
    @Override
    public Stock agregarExistente(Stock stock, Double cantidad) {
        stock.entradaStock(cantidad);
        return daoStock.update(stock);
    }

    @Transactional
    @Override
    public Stock quitarExistente(Stock stock, Double cantidad) {
        stock.salidaDeStock(cantidad);
        return daoStock.update(stock);
    }
}
