package financial.exchangers;

import GUI.components.Console;
import financial.Asset;
import financial.Exchange;
import trading.Trader;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static trading.Trader.Trigger.ON_DATE;

/**
 * Created by johannes on 17/06/17.
 */
public class HandelsbankenExchange extends Exchange {

    public void init(Trader t)
    {
        t.setExchangeTrigger(ON_DATE);
        t.setExchangeTriggerDate(Calendar.HOUR_OF_DAY, 18);
    }

    @Override
    public void trade() {
        int day = time.toCalendar().get(Calendar.DAY_OF_WEEK);
        if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
            return;


        for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext();) {
            Transaction t = iterator.next();
            int index = assets.get(t.asset).find(time, Asset.choose.BEFORE);

            if(index != -1) {

                balance -= t.amount;
                holdings[t.asset] += t.amount/assets.get(t.asset).getValues().get(index).value;
                iterator.remove();

                // To prevent crash
                if(balance > 100000000) {
                    balance = 100000;
                    transactions.clear();
                    for(int i = 0; i < holdings.length; i++)
                        holdings[i] = 0;
                    break;
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return "Handelsbanken simulator";
    }

    @Override
    public String getMarkets() {
        return "Handelsbanken";
    }


}
