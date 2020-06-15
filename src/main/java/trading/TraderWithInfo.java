package trading;

import financial.Asset;
import financial.AssetValue;
import graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/08/25.
 */
abstract public class TraderWithInfo extends Trader {

    private boolean first = true;
    private List<AssetValue> balance = new ArrayList();

    @Override
    public void beforeTrade()
    {
        if(first)
        {
            addAsset(new Asset("Balance", "", "", balance), new Color(255));


        }


        balance.add(new AssetValue(getTime(), (float)getTotalValue()));

        first = false;
    }


}
