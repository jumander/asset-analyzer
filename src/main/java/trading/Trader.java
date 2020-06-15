package trading;

import assetAnalyzer.AssetGraph;
import financial.Asset;
import financial.AssetValue;
import financial.Exchange;
import financial.Time;
import financial.exchangers.GenericExchange;
import graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.SECOND;

/**
 * Created by johannes on 16/12/17.
 */
public abstract class Trader {

    public enum Trigger {ON_CHANGE, ON_DATE, ON_ORDER, NEVER}

    private AssetGraph graph;
    private List<Asset> assets;
    private List<String> assetNames;
    private int[] indices;
    private Time startDate;
    private Time endDate;

    private Time time;
    private Trigger trigger;
    private Trigger exchangeTrigger;
    private int triggerPeriod;
    private int triggerDate;
    private int exchangeTriggerPeriod;
    private int exchangeTriggerDate;
    private Exchange exchange;

    public void run(List<Asset> assets, Exchange exchange, Time start, Time end, double startAmount, AssetGraph graph, String[] param) {
        this.assets = assets;
        this.startDate = start;
        this.endDate = end;
        this.graph = graph;
        assetNames = new ArrayList<>();
        for(Asset a : assets)
            assetNames.add(a.getName());
        time = new Time(start.toInt());
        indices = new int[assets.size()];
        for(int i = 0; i < indices.length; i++)
            indices[i] = assets.get(i).find(start, Asset.choose.BEFORE);

        // set graph
        //if(graph != null)
        //    graph.setWindow(start, end);

        trigger = Trigger.ON_CHANGE;
        exchangeTrigger = Trigger.ON_CHANGE;

        if(exchange != null)
            this.exchange = exchange;
         else
            this.exchange = new GenericExchange();

        this.exchange.init(this);
        this.exchange.setAssets(assets);
        this.exchange.setTime(time);
        this.exchange.deposit(startAmount);

        incrementIndices();

        init(param);
        init();

        loop();

        done();
        this.exchange.done();
    }

    public void init(){}
    public void init(String[] param){}
    public void beforeTrade(){}
    public abstract void trade();
    public void afterTrade(){}
    public void done(){}

    private final void loop()
    {
        while(true)
        {

            Time traderDate = trigger(trigger, triggerPeriod, triggerDate);
            Time exchangeDate = trigger(exchangeTrigger, exchangeTriggerPeriod, exchangeTriggerDate);
            if(traderDate == null || exchangeDate == null)
                break;

            int s = traderDate.compareTo(exchangeDate);
            if(s < 0)
                time = traderDate;
            else
                time = exchangeDate;

            if(time.isGreaterThan(endDate)) {
                time = endDate;
                incrementIndices();
                break;
            }

            incrementIndices();

            exchange.setTime(time);
            if(s > 0 || s == 0)
                exchange.trade();
            if(s < 0 || s == 0) {
                beforeTrade();
                trade();
                afterTrade();
            }

            // Increment time
            Calendar c = time.toCalendar();
            c.add(SECOND, 1);
            time = new Time(c);
        }
    }

    private final Time trigger(Trigger trigger, int triggerPeriod, int triggerDate)
    {
        Time newTime = time;
        if(trigger == Trigger.ON_CHANGE) {
            Time minDate = new Time(2040, 1, 1);

            // find next earliest date
            for (int a = 0; a < assets.size(); a++) {
                List<AssetValue> values = assets.get(a).getValues();
                if (indices[a] + 1 != values.size() && values.get(indices[a] + 1).time.isLessThan(minDate))
                    minDate = values.get(indices[a] + 1).time;
            }
            if (minDate.compareTo(new Time(2040, 1, 1)) == 0)
                return null;

            newTime = minDate;

        }
        else if(trigger == Trigger.ON_DATE)
        {
            newTime = time.advance(triggerPeriod, triggerDate);
            if(newTime == null)
                return null;
        }
        else if(trigger == Trigger.NEVER || trigger == Trigger.ON_ORDER)
            return new Time(2040, 1, 1);

        return newTime;
    }

    private final void incrementIndices()
    {
        // increment indices
        for (int a = 0; a < assets.size(); a++) {
            List<AssetValue> values = assets.get(a).getValues();
            while (indices[a] + 1 != values.size() && !values.get(indices[a] + 1).time.isGreaterThan(time))
                indices[a]++;
        }
    }

    public final void buy(int asset, double amount)
    {
        exchange.newTransaction(asset, amount);
        if(exchangeTrigger == Trigger.ON_ORDER)
            exchange.setTime(time);
            exchange.trade();
    }

    public final void sell(int asset, double amount)
    {
        buy(asset, -amount);
    }

    public final double getTotalValue()
    {
        return exchange.getValue();
    }

    public final double getBalance() { return exchange.getBalance(); }

    public final double getHolding(int asset)
    {
        return exchange.getHolding(asset);
    }

    public final void setTrigger(Trigger trigger)
    {
        this.trigger = trigger;
    }

    public final void setTriggerDate(int period, int date)
    {
        triggerPeriod = period;
        triggerDate = date;
    }

    public final void setExchangeTrigger(Trigger trigger)
    {
        this.exchangeTrigger = trigger;
    }

    public final void setExchangeTriggerDate(int period, int date)
    {
        exchangeTriggerPeriod = period;
        exchangeTriggerDate = date;
    }

    public final Time getTime() {
        return new Time(time.toInt());
    }

    public final int getNumAssets() {
        return assets.size();
    }

    public final String getName(int asset) {

        return assetNames.get(asset);
    }

    public final AssetValue getValue(int asset, int t) {
        if(indices[asset] != -1 && indices[asset]-t >= 0)
            return assets.get(asset).getValues().get(indices[asset]-t);

        return null;
    }

    public final AssetValue getValueByIndex(int asset, int index)
    {
        if(indices[asset] != -1 && indices[asset] >= index)
            return assets.get(asset).getValues().get(index);

        return null;
    }


    public final AssetValue getAssetValue(int asset, Time time, Asset.choose rel) {
        int index = assets.get(asset).find(time, rel);
        if(index != -1 && index <= indices[asset])
            return assets.get(asset).getValues().get(index);
        else
            return null;
    }

    public final int findIndex(int asset, Time time, Asset.choose rel)
    {
        return assets.get(asset).find(time, rel);
    }

    /*public final AssetGraph getAssetGraph()
    {
        return graph;
    }*/

    public final void addAsset(Asset asset)
    {
        if(graph != null)
        {
            graph.addAsset(asset);
        }
    }

    public final void addAsset(Asset asset, Color color)
    {
        if(graph != null)
        {
            graph.addAsset(asset, color);
        }
    }

    public String toString() {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass.getSimpleName();
        } else {
            return getClass().getSimpleName();
        }
    }

}
