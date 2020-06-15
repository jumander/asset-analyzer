package financial;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by johannes on 17/08/30.
 */
public class AssetFileHandler {

    public boolean store(List<Asset> assets, String filename)
    {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), "utf-8"))) {
            // start file
            writer.write(String.format("{'nr_assets':%d, %n'assets':[%n", assets.size()).replaceAll("'", "\""));

            for(int i = 0; i < assets.size(); i++)
            {
                if(i != 0) // print comma between assets
                    writer.write(',' + System.lineSeparator());
                Asset asset = assets.get(i);

                writer.write(String.format("{'name':'%s', 'market':'%s', 'currency':'%s', 'values':'",
                        asset.getName(), asset.getMarket(), asset.getCurrency()).replaceAll("'", "\""));

                // Protect against concurrent modification
                int valuesLength = asset.getValues().size();

                for(int v = 0; v < valuesLength; v++)
                {
                    AssetValue value = asset.getValues().get(v);

                    writer.write(System.lineSeparator());

                    writer.write("" + value.time.toInt());
                    writer.write(',');
                    writer.write("" + value.value);

                    if(value.extra != null)
                    {
                        for(Object o : value.extra)
                        {
                            writer.write("," + o.toString());
                        }

                    }

                }

                writer.write(System.lineSeparator() + "\"}" + System.lineSeparator());
            }
            // name market currency

            // endfile
            writer.write(System.lineSeparator() + "]}");
        } catch( IOException e)
        {
            return false;
        }

        return true;
    }

    public List<Asset> load(String filename)
    {
        List<Asset> assets = new ArrayList();

        StringBuilder JSONstring = new StringBuilder();

        boolean isJSON = true;
        int assetIndex = 0;

        List<List<AssetValue>> listOfValues = new ArrayList();
        List<AssetValue> tempValueList = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), "utf-8"))) {

            while(reader.ready()) {

                String line = reader.readLine();

                if (isJSON) {
                    JSONstring.append(line);

                    if (line.endsWith("\"values\":\"")) {
                        isJSON = false;
                        tempValueList = new ArrayList();
                    }
                } else if (line.endsWith("\"}")) {
                    listOfValues.add(tempValueList);

                    JSONstring.append(line);
                    isJSON = true;
                    assetIndex++;
                }
                else
                {
                    tempValueList.add(stringToAssetValue(line));
                }
            }

            JSONParser parser = new JSONParser();
            try {
                JSONObject result = (JSONObject)parser.parse(JSONstring.toString());

                JSONArray JSONassets = (JSONArray)result.get("assets");

                for(int a = 0; a < JSONassets.size(); a++)
                {
                    JSONObject JSONAsset = (JSONObject)JSONassets.get(a);
                    String name = (String)JSONAsset.get("name");
                    String market = (String)JSONAsset.get("market");
                    String currency = (String)JSONAsset.get("currency");


                    assets.add(new Asset(name, market, currency, listOfValues.get(a)));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        catch (FileNotFoundException e)
        {
            return null;
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }

        return assets;
    }

    public AssetValue stringToAssetValue(String line)
    {
        Object[] extra = null;
        String[] numbers = line.split(",");
        if(numbers.length < 2)
            return null;

        Time time = new Time(Integer.parseInt(numbers[0]));
        float value = Float.parseFloat(numbers[1]);

        if(numbers.length > 2) {
            extra = new Object[numbers.length - 2];
            for(int i = 2; i < numbers.length; i++)
                extra[i-2] = stringToObject(numbers[i]);
        }


        return new AssetValue(time, value, extra);
    }





    public Object stringToObject(String str)
    {
        final int INTEGER = 1;
        final int FLOAT = 2;
        switch(isNumber(str))
        {
            case INTEGER:
                return Integer.parseInt(str);
            case FLOAT:
                return Float.parseFloat(str);
            default:
                return null;
        }
    }

    /**
     * Determines weather the string is a float, integer or neither
     * @param s the string
     * @return 0 for non-number, 1 for integer, 2 for float
     */
    public int isNumber(String s)
    {
        boolean dot = false;
        if(s.isEmpty()) return 0;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return 0;
                else continue;
            }
            if(!Character.isDigit(s.charAt(i)))
            {
                if(dot || s.charAt(i) != '.')
                    return 0;
                else
                    dot = true;
            }
        }
        return dot ? 2 : 1;
    }


    /*
    public static void main(String[] args)
    {
        storeMain();
        loadMain();

    }

    public static void printAsset(Asset asset)
    {
        System.out.println(asset.getName() + " " + asset.getMarket() + " " + asset.getCurrency() + ":");

        for(AssetValue av : asset.getValues()) {
            System.out.print("time: " + av.time + " value: " + av.value);

            if (av.extra != null)
            {
                for(Object o : av.extra)
                    System.out.print(" " + o.toString());
            }
            System.out.println();
        }

    }


    public static void storeMain()
    {
        Object[] extra = new Object[3];
        extra[0] = 152153;
        extra[1] = 100.0f;
        extra[2] = 200.0f;
        AssetValue val = new AssetValue(new Time(2017, 7, 12, 1, 2, 3), 123.456f, extra);
        AssetValue val2 = new AssetValue(new Time(2017, 7, 12, 2, 2, 3), 133.456f);
        AssetValue val3 = new AssetValue(new Time(2017, 7, 12, 3, 2, 3), 143.456f);
        AssetValue val4 = new AssetValue(new Time(2017, 7, 12, 4, 2, 3), 153.456f, extra);
        List<AssetValue> values = new ArrayList<>();
        values.add(val);
        values.add(val2);
        values.add(val3);
        values.add(val4);

        Asset A = new Asset("TSLA", "NASDAQ", "USD", values);
        Asset B = new Asset("BTCUSD", "Bitfinex", "USD", values);
        List<Asset> assets = new ArrayList<>();
        assets.add(A);
        assets.add(B);

        new AssetFileHandler().store(assets, "myFile.txt");
    }

    public static void loadMain()
    {
        List<Asset> assets = new AssetFileHandler().load("myFile.txt");

        for(Asset a : assets)
        {
            printAsset(a);
            System.out.println();
        }
    }*/

}
