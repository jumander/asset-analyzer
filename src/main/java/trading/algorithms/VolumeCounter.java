package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import trading.Trader;
import trading.tools.Average;
import trading.tools.Sum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/08/25.
 */
public class VolumeCounter extends Trader {
    private List<AssetValue> chart;
    private Sum sum = null;

    public void init(String[] param)
    {
        int sumPeriod = 10;
        if(param.length > 0 && !param[0].equals(""))
        {
            sumPeriod = Integer.parseInt(param[0]);
        }
        sum = new Sum(this, 0, sumPeriod, 0);

        chart = new ArrayList();
        addAsset(new Asset("Volume " + sumPeriod, "", "", chart));
    }

    public void trade() {
        if(getNumAssets() > 0)
        {
            float val = sum.getValue();

            if(sum.isStable())
                chart.add(new AssetValue(getTime(), val));
        }
    }
}
