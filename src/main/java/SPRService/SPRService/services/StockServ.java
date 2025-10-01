package SPRService.SPRService.services;

import SPRService.SPRService.entities.Stock;

import java.util.List;

public interface StockServ {

    /**
     * Usado cuando se hace un ingreso en la cantidad de stock de un repuesto.
     */
    void modificarStock(Stock stockActualizado);

    void modificarStock(List<Stock> stockListActualizado);
}
