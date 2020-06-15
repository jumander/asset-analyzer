package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import trading.Trader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;

/**
 * Created by johannes on 17/07/01.
 */
public class GodsAlgorithm extends Trader {


    List<AssetValue> graphValues;
    public void init() {
        setTrigger(Trigger.ON_DATE);
        setTriggerDate(Calendar.HOUR_OF_DAY, 0);
        graphValues = new ArrayList();
        addAsset(new Asset("Gods algorithm", "", "", graphValues));
    }

    @Override
    public void trade() {
        int day = getTime().toCalendar().get(Calendar.DAY_OF_WEEK);
        if(day == SUNDAY || day == SATURDAY)
            return;

        List<Asset> assets = null;
        // Access the data (secret)
        try {
            Field field = Trader.class.getDeclaredField("assets");

            field.setAccessible(true);
            assets = (List<Asset>)field.get(this);
            field.setAccessible(false);
        } catch (Exception e) {
            System.err.println("Could not access private variable");
            return;
        }


        // sell all current
        for(int i = 0; i < getNumAssets(); i++)
            sell(i, getHolding(i));


        // find best asset
        float bestIncrease = 1;
        int bestAsset = 0;

        for(int i = 0; i < getNumAssets(); i++)
        {
            Asset asset = assets.get(i);
            int index = asset.find(getTime(), Asset.choose.BEFORE);
            List<AssetValue> values = asset.getValues();
            if(index != -1 && values.size() > index + 2)
            {
                float increase = values.get(index+2).value / values.get(index+1).value;
                if(increase > bestIncrease)
                {
                    bestIncrease = increase;
                    bestAsset = i;
                }
            }
        }

        // buy
        if(bestIncrease > 1)
            buy(bestAsset, getTotalValue());

        graphValues.add(new AssetValue(getTime(), (float) getTotalValue()));
    }
}
