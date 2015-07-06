package jo.d4w.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jo.d4w.data.CargoLot;
import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.data.TradeGood;
import jo.util.html.URIBuilder;
import jo.util.utils.obj.LongUtils;


public class CargoLogic
{   
    public static final int MIN_CARGO_AVAILABLE = 2;
    public static final int MAX_CARGO_AVAILABLE = 4;
    
    public static List<CargoLot> getLotsByURI(String uri)
    {
        String popURI = "pop://"+uri.substring(12);
        PopulatedObjectBean pop = D4WPopulationLogic.getByURI(popURI);
        if (pop == null)
            return null;
        URIBuilder u = new URIBuilder(uri);
        long date = LongUtils.parseLong(u.getQuery("date"));
        return getCargoLots(pop, date);
    }

    public static List<CargoLot> getCargoLots(PopulatedObjectBean port, long date)
    {
        long seed = makeSeed(port, date);
        Random rnd = new Random(seed);
        Map<TradeGood, Double> weights = new HashMap<TradeGood, Double>();
        TradeGoodLogic.getWeightedGoods(port, weights);
        double totalWeight = 0;
        for (Double weight : weights.values())
            totalWeight += weight;
        
        int q = (int)Math.ceil(Math.log10(port.getPopulation())*(rnd.nextGaussian()/2 + 1));
        List<CargoLot> lots = new ArrayList<CargoLot>();
        TradeGood[] goods = getSortedGoods(weights);
        while (q-- > 0)
        {
            CargoLot lot = makeLot(port, date, rnd.nextLong(), goods, weights, totalWeight);
            lots.add(lot);
        }
        return lots;
    }
    
    public static CargoLot getByURI(String uri)
    {
        String popURI = "pop://"+uri.substring(8);
        PopulatedObjectBean pop = D4WPopulationLogic.getByURI(popURI);
        if (pop == null)
            return null;
        URIBuilder u = new URIBuilder(uri);
        long date = LongUtils.parseLong(u.getQuery("date"));
        long seed = LongUtils.parseLong(u.getQuery("seed"));
        Map<TradeGood, Double> weights = new HashMap<TradeGood, Double>();
        TradeGoodLogic.getWeightedGoods(pop, weights);
        double totalWeight = 0;
        for (Double weight : weights.values())
            totalWeight += weight;
        TradeGood[] goods = getSortedGoods(weights);
        return makeLot(pop, date, seed, goods, weights, totalWeight);
    }

    public static TradeGood[] getSortedGoods(Map<TradeGood, Double> weights)
    {
        TradeGood[] goods = weights.keySet().toArray(new TradeGood[0]);
        Arrays.sort(goods, new Comparator<TradeGood>() {
            @Override
            public int compare(TradeGood o1, TradeGood o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return goods;
    }
    
    private static CargoLot makeLot(PopulatedObjectBean port, long date, long seed, TradeGood[] goods, Map<TradeGood, Double> weights, double totalWeight)
    {
        Random rnd = new Random(seed);
        TradeGood classification = findClassification(goods, weights, totalWeight, rnd);
        CargoLot lot = new CargoLot();
        lot.setOID(seed);
        lot.setDateAvailable(date);
        lot.setDateUnAvailable(date + MIN_CARGO_AVAILABLE + (int)((MAX_CARGO_AVAILABLE - MIN_CARGO_AVAILABLE)*rnd.nextGaussian()));
        lot.setValueMod(1.0 + rnd.nextGaussian()/4);
        lot.setClassification(classification.getOID());
        lot.setSize((int)(classification.getLotSize()*(1.0 + rnd.nextGaussian()/5)));
        String uri = "cargo://"+ port.getURI().substring(6) + "?date="+date+"&seed="+seed;
        lot.setURI(uri);
        return lot;
    }
    
    private static TradeGood findClassification(TradeGood[] goods, Map<TradeGood, Double> weights,
            double totalWeight, Random rnd)
    {
        double weight = totalWeight*rnd.nextDouble();
        for (TradeGood good : goods)
        {
            weight -= weights.get(good);
            if (weight <= 0)
                return good;
        }
        throw new IllegalStateException("Fell off end of table!");
    }

    private static long makeSeed(PopulatedObjectBean port, long date)
    {
        return port.getOID()^date;
    }
}