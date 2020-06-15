package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import financial.Time;
import trading.Trader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by johannes on 17/06/16.
 */
public class ShortFundStrategy extends Trader {

    private int NUM_ASSETS;

    private List<AssetValue> values;

    public void init(String[] param)
    {
        NUM_ASSETS = 8;
        values = new ArrayList<>();

        setTrigger(Trigger.ON_DATE);
        setTriggerDate(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        addAsset(new Asset("ShortFundStrategy", "", "", values));
    }


    public void trade()
    {
        // Sell current assets
        for(int i = 0; i < getNumAssets(); i++) {
            sell(i, getHolding(i));
        }


        Calendar c = getTime().toCalendar();
        //System.out.println("-");
        //System.out.println(new Time(c));
        //c.add(Calendar.DAY_OF_WEEK, c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 0 : 8-c.get(Calendar.DAY_OF_WEEK));
        c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);//c.set(Calendar.MONTH, c.get(Calendar.SATURDAY)-1); // OP STRAT
        //System.out.println(new Time(c));

        // Find best assets
        List<Increase> increases = new ArrayList<>();
        for(int i = 0; i < getNumAssets(); i++)
        {
            AssetValue now = getValue(i, 0);
            if(now != null && (now.time.toCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.time.toCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))
                System.out.println(now.time + getName(i));

            AssetValue before = getAssetValue(i, new Time(c), Asset.choose.BEFORE);
            if(now != null && before != null)
            {
                float percent = now.value/before.value;
                increases.add(new Increase(i, percent));
            }
        }
        Collections.sort(increases);

        // Buy the assets
        int num = Math.min(increases.size(), NUM_ASSETS);
        for(int i = 0; i < num; i++)
        {
            buy(increases.get(i).index, getTotalValue()/(float)num);
        }

        values.add(new AssetValue(getTime(), (float) getTotalValue()));
    }

    public void done() { }





    private class Increase implements Comparable<Increase>
    {
        int index;
        float increase;

        public Increase(int index, float increase)
        {
            this.index = index;
            this.increase = increase;
        }

        @Override
        public int compareTo(Increase o) {
            return (int)Math.signum(o.increase-increase);
        }
    }
}
