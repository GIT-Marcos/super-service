package SPRService.SPRService.services;

import SPRService.SPRService.entities.Stock;

import java.util.List;

public interface StockServ {

    Stock agregarExistente(Stock stock, Double cantidad);

    Stock quitarExistente(Stock stock, Double cantidad);
}
