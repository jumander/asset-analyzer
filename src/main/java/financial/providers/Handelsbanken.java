package financial.providers;

import financial.*;
import financial.exchangers.HandelsbankenExchange;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Created by johannes on 16/12/28.
 */
public class Handelsbanken extends AssetProvider {

    private String selectedCompany = "Handelsbanken";
    private int selectedCompId;
    private String fundSite = "http://web.msse.se/shb/sv.se/history/";
    private String startDate = "1970-01-01";
    HashMap<String, String> funds = null; // name | id

    @Override
    public String getName() {
        return "Handelsbanken";
    }

    @Override
    public String[] getAssets() {

        HashMap<String, Integer> companies;
        try {
            companies = getCompanies(fundSite);
        } catch (IOException e) {
            System.err.println("Unable to download companies data");
            return null;
        }

        selectedCompId = companies.get(selectedCompany);


        try {
            funds = getFunds(fundSite, selectedCompId);
        } catch (IOException e) {
            System.err.println("Unable to download funds data");
            return null;
        }

        String[] names = new String[funds.size()];

        int i = 0;
        Iterator it = funds.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            names[i] = ((String)pair.getKey()).replace(selectedCompany + " ", "");
            i++;
        }

        return names;
    }

    /** gives the given asset
     *
     * @param index
     * @return
     */
    @Override
    public Asset getAsset(int index) {

        String assetName = "";
        String assetMarket = "Handelsbanken";
        String assetCurrency = "SEK";
        ArrayList<AssetValue> values = null;
        Exchange exchange = new HandelsbankenExchange();


        int i = 0;
        Iterator it = funds.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(i == index)
            {
                try {
                    assetName = (String)pair.getKey();
                    values = getValues(fundSite, selectedCompId, (String)pair.getValue(), startDate);
                } catch (IOException e) {
                    System.err.println("Could not download fund information!");
                    return null;
                }
            }



            i++;
        }

        Asset asset = new Asset(assetName, assetMarket, assetCurrency, values);
        return asset;
    }

    @Override
    public String toString() {
        return "Handelsbanken";
    }



    String getLatestDate(Document doc)
    {
        return doc.getElementById("temp_EndDate").attr("value");
    }

    ArrayList<AssetValue> getValues(String url, int companyID, String fundID, String startDate) throws IOException {
        // Download values

        /*System.out.println(
                "company:" + companyID
                        + "\nFundId:" + fundID
                        + "\nStartDate:" + startDate);*/

        url += "onefund.xls?"
                + "company=" + companyID
                + "&fundid=" + fundID
                + "&startdate=" + startDate;

        Document doc = Jsoup.connect(url).maxBodySize(0).timeout(10000).get();

        //System.out.println("Successfully downloaded values from " + url);

        Elements values = doc.getElementsByTag("tr");
        values.remove(0);
        ArrayList<AssetValue> fundData = new ArrayList<>(values.size());

        for(int i = values.size()-1; i >= 0; i--)
        {
            Element value = values.get(i);

            AssetValue data = new AssetValue();
            data.value = Float.parseFloat(value.child(1).text().replace(',', '.').replaceAll("[^\\d.]", ""));
            data.time = new Time(value.child(3).text());
            Calendar c = data.time.toCalendar();
            c.set(Calendar.HOUR_OF_DAY, 18);
            data.time = new Time(c);

            int day = c.get(Calendar.DAY_OF_WEEK);
            if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
                continue;

            fundData.add(data);
        }

        return fundData;
    }

    /** Returns a list of available funds and their id:s
     *
     * @param url
     * @param companyID
     * @return
     * @throws IOException
     */
    HashMap<String, String> getFunds(String url, int companyID) throws IOException {
        // Download funds
        Document doc = Jsoup.connect(url).data("company", Integer.toString(companyID)).post();
        //System.out.println("Successfully downloaded funds from " + fundSite +
        //        " company:" + companyID);

        // Get td containing funds in the form
        // <option value="fundID">fundName</option>
        Elements funds = doc.getElementById("FundId").children();

        // Initialize hashmap
        HashMap<String, String> hashMap = new HashMap<>(funds.size());

        // Store each fund
        for(Element fund : funds)
        {
            if(fund.hasAttr("value") && fund.attr("value").length() > 0)
            {
                String fundID = fund.attr("value");
                String fundName = fund.text();
                hashMap.put(fundName, fundID);
            }
        }

        return hashMap;
    }

    /** Returns a list of fund providers and the id of the provider
     *
     * @param url
     * @return
     * @throws IOException
     */
    HashMap<String, Integer> getCompanies(String url) throws IOException {

        // Download company data
        Document doc = Jsoup.connect(url).get();
        //System.out.println("Successfully downloaded companies from " + fundSite);

        // Get td containing companies in the form
        // <option value="companyID">companyName</option>
        Elements companies = doc.getElementById("company").children();

        // Initialize hashmap
        HashMap<String, Integer> hashMap = new HashMap<>(companies.size());

        // Store each company
        for(Element company : companies)
        {
            if(company.hasAttr("value") && company.attr("value").length() > 0)
            {
                int companyID = Integer.parseInt(company.attr("value"));
                String companyName = company.text();
                hashMap.put(companyName, companyID);
            }
        }

        return hashMap;
    }
}
