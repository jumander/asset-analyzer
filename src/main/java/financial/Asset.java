package financial;

import java.io.Serializable;
import java.util.List;

/**
 * Created by johannes on 16/12/28.
 */
public class Asset implements Serializable {


    public enum choose{BEFORE, AFTER, CLOSEST, BEFORE_FIRST, EXACT}

    protected String name;

    protected String currency;
    protected String market;

    protected List<AssetValue> values;

    public Asset(String name, String market, String currency, List<AssetValue> values)
    {
        this.name = name;
        this.market = market;
        this.currency = currency;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMarket() {
        return market;
    }

    public List<AssetValue> getValues() {
        return values;
    }

    public int getEndDate()
    {
        return values.size();
    }

    public AssetValue getByDate(int index)
    {
        return values.get(index);
    }

    public int find(Time time, choose rel) {

        if(values.size() == 0)
            return -1;

        int left = 0;
        int right = values.size()-1;
        int middle = 0;

        while(right >= left) {
            middle = (right + left)/2;
            AssetValue stock = values.get(middle);
            if (time.isGreaterThan(stock.time))
                left = middle + 1;
            else if (time.isLessThan(stock.time))
                right = middle - 1;
            else
                return middle;


            //System.out.println(get(middle));
        }

        if(values.get(middle).time.isGreaterThan(time)) {
            if (rel == choose.AFTER)
                return middle;
            if (rel == choose.BEFORE)
                return middle - 1;
            if (rel == choose.BEFORE_FIRST)
            {
                if(middle == 0)
                    return middle;
                else
                    return middle - 1;
            }

        } else {
            if (rel == choose.BEFORE || rel == choose.BEFORE_FIRST)
                return middle;
            if (rel == choose.AFTER)
                return (middle + 1) < values.size() ? middle + 1 : -1;
        }

        if(rel == choose.CLOSEST) {
            int l,r;
            if(values.get(middle).time.isGreaterThan(time) && middle != 0) {
                r = middle;
                l = middle - 1;
            } else if (middle + 1 != values.size()) {
                r = middle + 1;
                l = middle;
            } else {
                return middle;
            }

            //System.out.println("r:" + (stocks.get(r).time - time));
            //System.out.println("l:" + (stocks.get(r).time - time));

            if(values.get(r).time.toInt() - time.toInt() > time.toInt() - values.get(l).time.toInt())
                return l;
            else
                return r;

        }

        if(rel == choose.EXACT && !values.get(middle).time.equals(time))
        {
            return -1;
        }

        return middle;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Asset asset = (Asset) o;
        if (name != null ? !name.equals(asset.name) : asset.name != null) return false;
        if (currency != null ? !currency.equals(asset.currency) : asset.currency != null) return false;
        return market != null ? market.equals(asset.market) : asset.market == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (market != null ? market.hashCode() : 0);
        return result;
    }
}
