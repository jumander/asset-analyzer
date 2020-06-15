package financial;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by johannes on 17/06/22.
 */
public class AssetTest {

    @Test
    public void shouldFindIndex() {
        List<AssetValue> values = new ArrayList<>();
        values.add(new AssetValue(new Time(2017, 1, 1), 200.0f));
        Asset A = new Asset("test", "", "", values);
        assertTrue(A.find(new Time(2016, 1, 1), Asset.choose.CLOSEST) == 0);
    }

    @Test
    public void shouldFindIndex2() {
        List<AssetValue> values = new ArrayList<>();
        values.add(new AssetValue(new Time(2017, 1, 1), 200.0f));
        Asset A = new Asset("test", "", "", values);
        assertTrue(A.find(new Time(2016, 1, 1), Asset.choose.AFTER) == 0);
    }

    @Test
    public void shouldFindIndex3() {
        List<AssetValue> values = new ArrayList<>();
        values.add(new AssetValue(new Time(2017, 1, 1), 200.0f));
        Asset A = new Asset("test", "", "", values);
        assertTrue(A.find(new Time(2018, 1, 1), Asset.choose.BEFORE) == 0);
    }

    @Test
    public void shouldNotFindIndexBefore() {
        List<AssetValue> values = new ArrayList<>();
        values.add(new AssetValue(new Time(2017, 1, 1), 200.0f));
        Asset A = new Asset("test", "", "", values);
        assertTrue(A.find(new Time(2016, 1, 1), Asset.choose.BEFORE) == -1);
    }

    @Test
    public void shouldNotFindIndexAfter() {
        List<AssetValue> values = new ArrayList<>();
        values.add(new AssetValue(new Time(2017, 1, 1), 200.0f));
        Asset A = new Asset("test", "", "", values);
        assertTrue(A.find(new Time(2018, 1, 1), Asset.choose.AFTER) == -1);
    }

}