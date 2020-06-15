package financial.providers;

import financial.Asset;
import financial.AssetProvider;
import financial.AssetValue;
import financial.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/06/21.
 */
public class TestProvider extends AssetProvider {
    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public String[] getAssets() {
        String[] assets = new String[1];
        assets[0] = "test";
        return assets;
    }

    @Override
    public Asset getAsset(int index) {
        List<AssetValue> values = new ArrayList<>();
        values.add(new AssetValue(new Time(2017, 6, 19, 18, 0, 0), 200));
        values.add(new AssetValue(new Time(2017, 6, 20, 18, 0, 0), 50));
        values.add(new AssetValue(new Time(2017, 6, 23, 18, 0, 0), 100));
        return new Asset("test", "", "", values);
    }

    @Override
    public String toString() {
        return getName();
    }
}
