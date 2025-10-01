package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.StockDAO;
import SPRService.SPRService.entities.Stock;
import com.google.inject.Singleton;

@Singleton
public class StockDAOImpl extends GenericDAOImpl<Stock, Long> implements StockDAO {

    public StockDAOImpl() {
        super(Stock.class);
    }
}
