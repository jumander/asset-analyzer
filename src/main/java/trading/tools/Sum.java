package trading.tools;

import financial.Asset;
import financial.AssetValue;
import financial.Time;
import trading.Trader;

/**
 * Created by johannes on 17/08/25.
 */
public class Sum {

    private Trader trader;

    private int asset;
    private int eIndex;
    private int seconds;

    private double totalSum = 0;
    private int totalWidth = 0;

    private Time startDate;
    private int startIndex;
    private Time endDate;
    private int endIndex;

    private Time lastRecalculation;



    public Sum(Trader trader, int asset, int seconds, int eIndex)
    {
        this.trader = trader;
        this.asset = asset;
        this.seconds = seconds;
        this.eIndex = eIndex;
        startDate = null;
        endDate = null;
        totalSum = 0;
    }

    public Sum(Trader trader, int asset, int seconds)
    {
        this(trader, asset, seconds, -1);
    }

    private void add(Time stopTime)
    {

        int stopIndex = trader.findIndex(asset, stopTime, Asset.choose.BEFORE);
        if(stopIndex == -1)
            return;

        AssetValue value = trader.getValueByIndex(asset, endIndex);
        float v;
        for(int i = endIndex; i < stopIndex; i++)
        {
            AssetValue nextValue = trader.getValueByIndex(asset, i + 1);

            v = eIndex != -1 ? (int)value.extra[eIndex] : value.value;

            totalSum += v;

            value = nextValue;
        }

        v = eIndex != -1 ? (int)value.extra[eIndex] : value.value;

        totalSum += v;
        totalWidth += stopTime.toInt() - endDate.toInt();

        endIndex = stopIndex;


    }

    private void subtract(Time stopTime)
    {

        int stopIndex = trader.findIndex(asset, stopTime, Asset.choose.BEFORE);
        if(stopIndex == -1)
            return;

        AssetValue value = trader.getValueByIndex(asset, startIndex);
        float v;
        for(int i = startIndex; i < stopIndex; i++)
        {
            AssetValue nextValue = trader.getValueByIndex(asset, i + 1);

            v = eIndex != -1 ? (int)value.extra[eIndex] : value.value;

            totalSum -= v;

            value = nextValue;
        }
        v = eIndex != -1 ? (int)value.extra[eIndex] : value.value;

        totalSum -= v;
        totalWidth -= stopTime.toInt() - startDate.toInt();

        startIndex = stopIndex;
    }

    private void recalculate() {

        totalSum = 0;
        totalWidth = 0;
        endDate = new Time(trader.getTime().toInt()-seconds);
        endIndex = trader.findIndex(asset, endDate, Asset.choose.BEFORE_FIRST);

        add(trader.getTime());

        startDate = endDate;
        startIndex = endIndex;
        endDate = trader.getTime();
        endIndex = trader.findIndex(asset, endDate, Asset.choose.BEFORE);
    }

    public float getValue() {
        if (totalWidth == 0 || startDate == null || endDate == null || trader.getTime().toInt() - seconds > (startDate.toInt() + seconds / 2))
        {
            recalculate();
        }
        else
        {
            recalculate();
        /*    Time newStartDate = new Time(trader.getTime().toInt() - seconds);
            Time newEndDate = trader.getTime();

            // advance tail
            subtract(newStartDate);
            startDate = newStartDate;

            // advance head
            add(newEndDate);
            endDate = newEndDate;*/
        }

        if(totalWidth == 0)
            return 0;
        else
            return (float)totalSum;
    }


    public boolean isStable()
    {
        return totalWidth == seconds;
    }


}
