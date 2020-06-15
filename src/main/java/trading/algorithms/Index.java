package trading.algorithms;

import financial.Asset;
import financial.AssetValue;
import trading.Trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/06/17.
 */
public class Index extends Trader {

    private List<AssetValue> values;
    private double index;

    public void init(String[] param)
    {
        values = new ArrayList<>();
        index = 100;
        addAsset(new Asset("Index", "Handelsbanken", "SEK", values));
    }

    @Override
    public void trade() {

        double indexSum = 0;
        int count = 0;

        for(int i = 0; i < getNumAssets(); i++)
        {
            AssetValue now = getValue(i, 0);
            AssetValue before = getValue(i, 1);
            if(now != null && before != null && now.value != 0 && before.value != 0)
            {
                indexSum += now.value / before.value;
                count++;
            }
        }

        if(count != 0)
        {
            index = index * (indexSum/(double)count);

            AssetValue v = new AssetValue();
            v.time = getTime();
            v.value = (float)index;
            values.add(v);
        }
        //System.out.println(getTime() + " " + indexSum + " " + count);
    }

    public void done()
    {

    }
}
