package trading;

import GUI.components.Console;
import assetAnalyzer.AssetGraph;
import financial.Asset;
import financial.Exchange;
import financial.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by johannes on 17/06/26.
 */
public abstract class Optimizer {

    public class Result implements Comparable<Result> {
        int index;
        double increase;

        public Result(int index, double increase) {
            this.index = index;
            this.increase = increase;
        }

        @Override
        public int compareTo(Result o) {
            return (int) Math.signum(o.increase - increase);
        }
    };

    public void run(Trader trader, List<Asset> assets, Exchange exchange, Time start, Time end, double startAmount, AssetGraph graph, String param)
    {
        List<Result> results = new ArrayList();
        List<String> parameters = generateParameters();
        List<List<Asset>> assetSelection = generateAssets(assets);


        if(parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                //Console.println("running " + trader.toString() + " " + parameters.get(i));
                trader.run(assets, exchange, start, end, startAmount, graph, parameters.get(i).split(" "));
                results.add(new Result(i, trader.getTotalValue()));
                Console.println(String.format("%.2f%%", (100 * (i + 1)) / (float) parameters.size()));
            }

        } else if(assetSelection != null)
        {   // Does not work
            /*for (int i = 0; i < assetSelection.size(); i++) {
                //Console.println("running " + trader.toString() + " " + parameters.get(i));
                trader.run(assetSelection.get(i), start, end, startAmount, graph, param.split(" "));
                if (trader.getTotalValue() > maxValue) {
                    maxValue = trader.getTotalValue();
                    index = i;
                }
                Console.println(String.format("%.2f%%", (100 * (i + 1)) / (float) assetSelection.size()));
            }
            Console.println("Optimizer done. Most Optimal asset selection: ");
            for(Asset a : assetSelection.get(index))
            {
                Console.println(a.getName());
            }*/
        }
        Collections.sort(results);

        for(Result r : results)
        {
            System.out.println(parameters.get(r.index) + " " + r.increase);
        }

        Console.println("Optimizer done. Most Optimal parameter: " + parameters.get(results.get(0).index));
    }

    public List<String> generateParameters() {
        return null;
    }

    public List<List<Asset>> generateAssets(List<Asset> assets) {
        return null;
    }


    public String toString() {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass.getSimpleName();
        } else {
            return getClass().getSimpleName();
        }
    }
}
