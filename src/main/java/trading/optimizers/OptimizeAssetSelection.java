package trading.optimizers;

import GUI.components.Console;
import assetAnalyzer.AssetGraph;
import financial.Asset;
import financial.Exchange;
import financial.Time;
import trading.Optimizer;
import trading.Trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/06/30.
 */
public class OptimizeAssetSelection extends Optimizer {

    @Override
    public void run(Trader trader, List<Asset> assets, Exchange exchange, Time start, Time end, double startAmount, AssetGraph graph, String param) {


        trader.run(assets, exchange, start, end, startAmount, graph, param.split(" "));
        double maxValue = trader.getTotalValue();
        List<Asset> best = new ArrayList<>(assets);

        boolean done = false;

        while(!done) {
            done = true;
            for (Asset test : assets) {
                List<Asset> copy = new ArrayList(assets);
                copy.remove(test);
                Console.println("Number of assets: " + copy.size());
                trader.run(copy, exchange, start, end, startAmount, graph, param.split(" "));
                if (trader.getTotalValue() >= maxValue) {
                    maxValue = (float)trader.getTotalValue();
                    best = new ArrayList<>(copy);
                    assets = new ArrayList<>(copy);
                    done = false;
                    break;
                }
            }
        }


        Console.println("Optimizer done. Most Optimal asset selection: ");
        for(Asset a : best)
        {
            Console.println(a.getName());
        }
    }

}
