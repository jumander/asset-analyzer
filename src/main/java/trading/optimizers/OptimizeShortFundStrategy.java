package trading.optimizers;

import trading.Optimizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/06/28.
 */
public class OptimizeShortFundStrategy extends Optimizer {
    @Override
    public List<String> generateParameters() {
        List<String> parameters = new ArrayList<>();

        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

        for(String day : days)
        {
            for(int numFunds = 4; numFunds <= 10; numFunds++) {
                for(int rolloverDays = 25; rolloverDays <= 40; rolloverDays += 1)
                parameters.add(day + " " + numFunds + " " + rolloverDays);
            }
        }

        return parameters;
    }
}
