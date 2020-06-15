package trading.algorithms;

import GUI.components.Console;
import financial.Asset;
import financial.AssetValue;
import trading.tools.Average;
import trading.Trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/06/14.
 */
public class MovingAverage extends Trader {

    private List<AssetValue> chart;
    private Average average = null;

    public void init(String[] param)
    {
        int movingAverage = 60;
        if(param.length > 0 && !param[0].equals(""))
        {
            movingAverage = Integer.parseInt(param[0]);
        }
        average = new Average(this, 0, movingAverage);

        chart = new ArrayList();
        addAsset(new Asset("Average " + movingAverage, "", "", chart));
    }

    public void trade() {
        if(getNumAssets() > 0)
        {
            float val = average.getValue();

            chart.add(new AssetValue(getTime(), val));
        }
    }
}
