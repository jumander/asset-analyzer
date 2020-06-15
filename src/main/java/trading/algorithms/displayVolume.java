package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import trading.Trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/08/18.
 */
public class displayVolume extends Trader {

    List<AssetValue> volumes = new ArrayList();
    public void init() {
        addAsset(new Asset("Volume", "", "", volumes));
    }

    @Override
    public void trade() {
        if(getNumAssets() > 0)
        {
            AssetValue value = getValue(0, 0);
            if(value.extra != null && value.extra.length > 0)
                volumes.add(new AssetValue(value.time, (float)((int)value.extra[0])));
        }
    }
}
