package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.StockDAO;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.services.StockServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.List;

@Singleton
public class StockServImpl implements StockServ {

    private final StockDAO daoStock;

    @Inject
    public StockServImpl(StockDAO daoStock) {
        this.daoStock = daoStock;
    }

    @Override
    public Stock agregarExistente(Stock stock, Double cantidad) {
        if (cantidad.isInfinite() || cantidad.isNaN() || cantidad < 0)
            throw new IllegalArgumentException("Cantidad de stock a agregar en mal formato");

        stock.entradaStock(cantidad);
        return daoStock.update(stock);
    }

    @Override
    public Stock quitarExistente(Stock stock, Double cantidad) {
        if (cantidad.isInfinite() || cantidad.isNaN() || cantidad < 0)
            throw new IllegalArgumentException("Cantidad de stock a agregar en mal formato");
        if (cantidad > stock.getCantidadExistente())
            throw new IllegalArgumentException("La cantidad de salida de stock es mayor al existente.");

        stock.salidaDeStock(cantidad);
        return daoStock.update(stock);
    }

    @Transactional
    @Override
    public void modificarStock(Stock stockActualizado) {
        if (stockActualizado == null) {
            throw new NullPointerException("El stock que se quiere actualizar es nulo.");
        }
        modificarStock(List.of(stockActualizado));
    }

    @Transactional
    @Override
    public void modificarStock(List<Stock> stockListActualizado) {
        if (stockListActualizado == null) {
            throw new NullPointerException("El stock que se quiere actualizar es nulo.");
        }
        daoStock.update(stockListActualizado);
    }

}
