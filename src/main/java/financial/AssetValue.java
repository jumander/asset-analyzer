package financial;

import java.io.*;
/**
 * Created by johannes on 16/12/28.
 */
public class AssetValue implements Serializable {

    public Time time;
    public float value;
    public Object[] extra = null;
    public String info = null;

    public AssetValue(Time time, float value)
    {
        this.time = time;
        this.value = value;
    }

    public AssetValue(Time time, float value, Object[] extra)
    {
        this.time = time;
        this.value = value;
        this.extra = extra;
    }


    public AssetValue(){}

    public AssetValue(AssetValue v) {
        this.time = v.time;
        this.value = v.value;
        this.extra = v.extra;
        this.info = v.info;
    }

    public String toString()
    {
        return time + " " + value;
    }

}
