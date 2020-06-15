package financial;

import trading.Trader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by johannes on 17/06/17.
 */
public abstract class Exchange implements Serializable{

    protected double balance;
    protected double[] holdings;
    protected List<Asset> assets;
    protected Queue<Transaction> transactions;
    protected Time time;

    public class Transaction {
        public int asset;
        public double amount; // positive for buy, negative for sell
    }

    public Exchange() {
        balance = 0;
        transactions = new LinkedList<>();
    }

    public void setAssets(List<Asset> assets)
    {
        this.assets = assets;
        this.holdings = new double[assets.size()];
        for(int i = 0; i < assets.size(); i++)
            holdings[i] = 0f;
        balance = 0;
    }

    public void open(){}
    public void close(){}

    public void init(Trader t){}
    public void setTime(Time time) {
        this.time = time;
    }
    public abstract void trade();
    public void done(){}

    public void deposit(double amount){
        balance += amount;
    }
    public void withdraw(double amount){
        balance -= amount;
    }

    public double getBalance() {return balance;}

    public double getHolding(int asset) {
        int index = assets.get(asset).find(time, Asset.choose.BEFORE);
        if(index != -1)
            return holdings[asset] * assets.get(asset).getValues().get(index).value;
        else
            return 0;
    }

    public double getHoldings() {
        float sum = 0;
        for(int i = 0; i < holdings.length; i++)
            sum += getHolding(i);

        return sum;
    }
    public double getValue()
    {
        return getBalance() + getHoldings();
    }

    /**
     * Creates a new transaction.
     * @param asset the index to tha asset
     * @param amount the amount(money), positive for buy, negative for sell
     */
    public void newTransaction(int asset, double amount)
    {

        Transaction t = new Transaction();
        t.asset = asset;
        t.amount = amount;
        transactions.add(t);
    }

    abstract public String toString();

    abstract public String getMarkets();
}
