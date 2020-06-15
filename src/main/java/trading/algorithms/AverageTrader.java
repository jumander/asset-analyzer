package trading.algorithms;


import financial.Asset;
import financial.AssetValue;
import trading.TraderWithInfo;
import trading.tools.Average;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/08/25.
 */
public class AverageTrader extends TraderWithInfo {

    private Average[] longAverage;
    private Average[] shortAverage;

    private List<AssetValue> longChart = new ArrayList();
    private List<AssetValue> shortChart = new ArrayList();


    @Override
    public void init()
    {
        addAsset(new Asset("200 min avg", "", "", longChart));
        addAsset(new Asset("50 min avg", "", "", shortChart));

        longAverage = new Average[getNumAssets()];
        shortAverage = new Average[getNumAssets()];

        for(int a = 0; a < getNumAssets(); a++)
        {
            longAverage[a] = new Average(this, a, 2000 * 60);
            shortAverage[a] = new Average(this, a, 500 * 60);
        }
    }

    @Override
    public void trade() {

        double partition = getTotalValue()/getNumAssets();

        for(int a = 0; a < getNumAssets(); a++)
        {
            float shortAvg = shortAverage[a].getValue();
            float longAvg = longAverage[a].getValue();

            if(!shortAverage[a].isStable())
                continue;
            shortChart.add(new AssetValue(getTime(), shortAvg));

            if(!longAverage[a].isStable())
                continue;
            longChart.add(new AssetValue(getTime(), longAvg));

            if(shortAvg > longAvg)
            {
                // buy
                if(getHolding(a) < partition/2.0)
                {
                    buy(a, partition - getHolding(a));
                }
            }
            else
            {
                // sell
                if(getHolding(a) > partition/2.0)
                {
                    sell(a, getHolding(a));
                }
            }
        }

    }

}
