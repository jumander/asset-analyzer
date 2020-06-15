package trading.tools;

import financial.Asset;
import financial.AssetValue;
import financial.Time;
import trading.Trader;

import java.util.Calendar;

/**
 * Created by johannes on 17/07/04.
 */
public class Average
{
    private Trader trader;

    private int asset;
    private int seconds;

    private double totalSum = 0;
    private int totalWidth = 0;

    private Time startDate;
    private int startIndex;
    private Time endDate;
    private int endIndex;

    private Time lastRecalculation;

    public Average(Trader trader, int asset, int seconds)
    {
        this.trader = trader;
        this.asset = asset;
        this.seconds = seconds;
        startDate = null;
        endDate = null;
        totalSum = 0;
    }



    /*private void recalculate()
    {
        totalSum = 0;
        totalWidth = 0;
        endDate = trader.getTime();
        startDate = new Time(endDate.toInt()-seconds);

        startIndex = trader.findIndex(asset, startDate, Asset.choose.BEFORE_FIRST);

        endIndex = trader.findIndex(asset, endDate, Asset.choose.BEFORE);
        if(endIndex == -1)
            return;

        Time time = startDate;
        AssetValue value = trader.getValueByIndex(asset, startIndex);
        for(int i = startIndex; i < endIndex; i++)
        {
            AssetValue nextValue = trader.getValueByIndex(asset, i + 1);

            totalSum += value.value * (nextValue.time.toInt() - time.toInt());

            time = nextValue.time;
            value = nextValue;
        }

        totalSum += value.value * (endDate.toInt() - time.toInt());
        totalWidth = endDate.toInt() - startDate.toInt();

    }*/

    private void add(Time stopTime)
    {

        int stopIndex = trader.findIndex(asset, stopTime, Asset.choose.BEFORE);
        if(stopIndex == -1)
            return;

        Time time = endDate;
        AssetValue value = trader.getValueByIndex(asset, endIndex);
        for(int i = endIndex; i < stopIndex; i++)
        {
            AssetValue nextValue = trader.getValueByIndex(asset, i + 1);

            int width = nextValue.time.toInt() - time.toInt();
            totalSum += value.value * width;

            time = nextValue.time;
            value = nextValue;
        }

        totalSum += value.value * (stopTime.toInt() - time.toInt());
        totalWidth += stopTime.toInt() - endDate.toInt();

        endIndex = stopIndex;


    }

    private void subtract(Time stopTime)
    {

        int stopIndex = trader.findIndex(asset, stopTime, Asset.choose.BEFORE);
        if(stopIndex == -1)
            return;

        Time time = startDate;
        AssetValue value = trader.getValueByIndex(asset, startIndex);
        for(int i = startIndex; i < stopIndex; i++)
        {
            AssetValue nextValue = trader.getValueByIndex(asset, i + 1);

            int width = nextValue.time.toInt() - time.toInt();
            totalSum -= value.value * width;

            time = nextValue.time;
            value = nextValue;
        }

        totalSum -= value.value * (stopTime.toInt() - time.toInt());
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
            Time newStartDate = new Time(trader.getTime().toInt() - seconds);
            Time newEndDate = trader.getTime();

            // advance tail
            subtract(newStartDate);
            startDate = newStartDate;

            // advance head
            add(newEndDate);
            endDate = newEndDate;
        }

        if(totalWidth == 0)
            return 0;
        else
            return (float)(totalSum/totalWidth);
    }


    public boolean isStable()
    {
        return totalWidth == seconds;
    }


}
