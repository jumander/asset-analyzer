package financial;

/**
 * Created by johannes on 16/12/28.
 */
public abstract class AssetProvider {
    public abstract String getName();

    public abstract String[] getAssets();

    public abstract Asset getAsset(int index);

    public abstract String toString();
}
