package trading.algorithms;

import GUI.components.Console;
import financial.Asset;
import financial.AssetValue;
import financial.Time;
import trading.Trader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by johannes on 17/06/20.
 */
public class OptimizedShortFundStrategy extends Trader {


    private int DAY;
    private int NUM_ASSETS;
    private int ROLLOVER_DAYS;

    private List<AssetValue> values;

    public void init(String[] param)
    {
        DAY = 1;
        NUM_ASSETS = 8;
        ROLLOVER_DAYS = 30;
        values = new ArrayList<>();

        if(param.length > 0) {
            switch (param[0]) {
                case "SUNDAY":      DAY = 1; break;
                case "MONDAY":      DAY = 2; break;
                case "TUESDAY":     DAY = 3; break;
                case "WEDNESDAY":   DAY = 4; break;
                case "THURSDAY":    DAY = 5; break;
                case "FRIDAY":      DAY = 6; break;
                case "SATURDAY":    DAY = 7; break;
                default: Console.println("Invalid parameter" + param[0]);
            }
        }

        if(param.length > 1)
            NUM_ASSETS = Integer.parseInt(param[1]);

        if(param.length > 2)
            ROLLOVER_DAYS = Integer.parseInt(param[2]);

        setTrigger(Trader.Trigger.ON_DATE);
        setTriggerDate(Calendar.DAY_OF_WEEK, DAY);

        String input = String.join(", ", param);
        addAsset(new Asset("OptimizedShortFundStrategy{" + input + "}", "", "", values));
    }


    public void trade()
    {
        // Sell current assets
        for(int i = 0; i < getNumAssets(); i++) {
            sell(i, getHolding(i));
        }

        AssetValue newValue = new AssetValue();

        Calendar c = getTime().toCalendar();
        //c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        //c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-ROLLOVER_DAYS);//c.set(Calendar.MONTH, c.get(Calendar.SATURDAY)-1); // OP STRAT

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

        newValue.info = "";

        // Buy the assets
        int num = Math.min(increases.size(), NUM_ASSETS);
        for(int i = 0; i < num; i++)
        {
            buy(increases.get(i).index, getTotalValue()/(float)num);
            newValue.info += getName(increases.get(i).index);
        }

        newValue.time = getTime();
        newValue.value = (float) getTotalValue();
        values.add(newValue);
    }







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

