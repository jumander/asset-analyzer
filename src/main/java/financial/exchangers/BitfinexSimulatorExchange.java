package financial.exchangers;

import financial.Asset;
import financial.AssetValue;
import financial.Exchange;
import trading.Trader;

import java.util.Iterator;

/**
 * Created by johannes on 17/08/16.
 */
public class BitfinexSimulatorExchange extends Exchange {

    public final float MAKER_FEE = 0.001f;
    public final float TAKER_FEE = 0.002f;
    public final int BID = 1;
    public final int ASK = 2;

    public void init(Trader t)
    {
        t.setExchangeTrigger(Trader.Trigger.ON_ORDER);
    }


    @Override
    public void trade() {

        for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext();) {
            Transaction t = iterator.next();
            boolean buy = t.amount >= 0 ? true : false;
            t.amount = Math.abs(t.amount);

            if(buy && t.amount > balance) {
                System.out.println("Not sufficient funds for transaction!");
                iterator.remove();
                continue;
            }

            int index = assets.get(t.asset).find(time, Asset.choose.BEFORE);

            if(index != -1) {
                AssetValue value = assets.get(t.asset).getValues().get(index);
                if(value.extra.length != 3)
                    System.out.println("extra does not have length of 3!");


                float bid = (float)value.extra[BID];
                float ask = (float)value.extra[ASK];

                if(bid < 0 || bid > 10000)
                    System.out.println("bid stragne " + bid);
                if(ask < 0 || ask > 10000)
                    System.out.println("ask stragne " + ask);

                if(!buy && t.amount > holdings[t.asset]*bid)
                {
                    System.out.println("Not sufficient assets for transaction!");
                    iterator.remove();
                    continue;
                }

                if(buy)
                {
                    balance -= t.amount;
                    holdings[t.asset] += (t.amount*(1-TAKER_FEE))/ask;
                }
                else
                {
                    balance += t.amount*(1-TAKER_FEE);
                    holdings[t.asset] -= t.amount/bid;
                }

                iterator.remove();

            }
        }
    }

    @Override
    public String toString() {
        return "Bitfinex simulator";
    }

    @Override
    public String getMarkets() {
        return "Bitfinex";
    }
}
