package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import trading.Trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/06/17.
 */
public class LongFundStrategy extends Trader {

    private List<AssetValue> values;
    private boolean firstIteration;

    public void init(String[] param) {
        values = new ArrayList<>();
        firstIteration = true;
        addAsset(new Asset("LongFundStrategy", "", "", values));
    }

    @Override
    public void trade() {
        if(firstIteration)
        {
            for(int i = 0; i < getNumAssets(); i++)
            {
                buy(i, getTotalValue()/(float)getNumAssets());
            }
            firstIteration = false;
        }

        values.add(new AssetValue(getTime(), (float) getTotalValue()));
    }

    public void done()
    {
    }

}
