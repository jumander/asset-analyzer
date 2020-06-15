package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import trading.Trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/08/17.
 */
public class BuySellStrategy extends Trader{

    boolean buy = false;
    private List<AssetValue> values = new ArrayList();

    public void init()
    {
        addAsset(new Asset("BuySellStrategy", "", "", values));
    }

    @Override
    public void trade() {

        if(buy)
        {
            float amount = (float)getBalance() * 0.9f;
            amount /= getNumAssets();
            for(int a = 0; a < getNumAssets(); a++)
            {
                buy(a, amount);
            }
        }
        else
        {
            for(int a = 0; a < getNumAssets(); a++)
            {
                sell(a, getHolding(a) * 0.9f);
            }
        }


        values.add(new AssetValue(getTime(), (float) getTotalValue()));
        buy = !buy;
    }
}
