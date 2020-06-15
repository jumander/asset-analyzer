package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import trading.Trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/08/18.
 */
public class displayOrders extends Trader {

    List<AssetValue> bids = new ArrayList();
    List<AssetValue> asks = new ArrayList();
    public void init() {
        addAsset(new Asset("bid", "", "", bids));
        addAsset(new Asset("ask", "", "", asks));
    }

    @Override
    public void trade() {
        if(getNumAssets() > 0)
        {
            AssetValue value = getValue(0, 0);
            if(value.extra != null && value.extra.length > 2) {
                bids.add(new AssetValue(value.time, (float) value.extra[1]));
                asks.add(new AssetValue(value.time, (float) value.extra[2]));
            }
        }
    }
}
