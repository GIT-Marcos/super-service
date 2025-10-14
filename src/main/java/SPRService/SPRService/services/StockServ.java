package SPRService.SPRService.services;

import SPRService.SPRService.entities.Stock;

import java.util.List;

public interface StockServ {

    Stock agregarExistente(Stock stock, Double cantidad);

    Stock quitarExistente(Stock stock, Double cantidad);

    /**
     * Usado cuando se hace un ingreso en la cantidad de stock de un repuesto.
     */
    @Deprecated
    void modificarStock(Stock stockActualizado);

    @Deprecated
    void modificarStock(List<Stock> stockListActualizado);
}
