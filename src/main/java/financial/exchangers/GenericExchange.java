package financial.exchangers;

import financial.Exchange;
import trading.Trader;

/**
 * Created by johannes on 17/06/17.
 */
public class GenericExchange extends Exchange {

    public void init(Trader t)
    {
        t.setExchangeTrigger(Trader.Trigger.NEVER);
    }

    @Override
    public void trade() {
        // do nothing
    }

    @Override
    public String toString() {
        return "Generic exchange simulator";
    }

    @Override
    public String getMarkets() {
        return "";
    }
}
