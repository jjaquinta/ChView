package jo.d4w.logic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.data.TradeGood;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;
import jo.util.utils.obj.StringUtils;
import jo.util.utils.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TradeGoodLogic
{
    private static TradeGood mTradeGoodTree = null;
    private static final List<TradeGood> mMajorCategories = new ArrayList<TradeGood>();
    private static final List<TradeGood> mMinorCategories = new ArrayList<TradeGood>();
    private static final List<TradeGood> mSubCategories = new ArrayList<TradeGood>();
    private static final List<TradeGood> mTradeGoods = new ArrayList<TradeGood>();
    private static final Map<Long, TradeGood> mTradeGoodIndex = new HashMap<Long, TradeGood>();
    
    private static void init()
    {
        if (mTradeGoodTree != null)
            return;
        mTradeGoodTree = new TradeGood();
        mTradeGoodTree.setAgricultural(.333);
        mTradeGoodTree.setMaterial(.333);
        mTradeGoodTree.setEnergy(.333);
        mTradeGoodTree.setLotSize(10); // default
        mTradeGoodTree.setValueMod(1.0); // default
        InputStream is = ResourceUtils.loadSystemResourceStream("tradeGoods.xml", PopulatedObjectBean.class);
        Document doc = XMLUtils.readStream(is);
        //is.close(); // XMLUtils closes the stream
        fromXML(mTradeGoodTree, doc.getFirstChild());
        indexTradeGoods(mTradeGoodTree, 0);
    }

    private static void indexTradeGoods(TradeGood parent, int depth)
    {
        if (depth == 1)
            mMajorCategories.add(parent);
        else if (depth == 1)
            mMinorCategories.add(parent);
        else if (depth == 2)
            mSubCategories.add(parent);
        else if (depth >= 3)
            if ((parent.getChildren() == null) || (parent.getChildren().size() == 0))
                mTradeGoods.add(parent);
        if (parent.getChildren() != null)
            for (TradeGood child : parent.getChildren())
                indexTradeGoods(child, depth + 1);
    }
    
    private static void fromXML(TradeGood parent, Node p)
    {
        for (Node n = p.getFirstChild(); n != null; n = n.getNextSibling())
        {
            if (n.getNodeName().startsWith("#"))
                continue;
            String code = XMLUtils.getAttribute(n, "code");
            String name = XMLUtils.getAttribute(n, "name");
            String desc = XMLUtils.getAttribute(n, "description");
            if (StringUtils.isTrivial(code) || StringUtils.isTrivial(desc))
                continue;
            String agricultural = XMLUtils.getAttribute(n, "agricultural");
            String material = XMLUtils.getAttribute(n, "material");
            String energy = XMLUtils.getAttribute(n, "energy");
            String lotSize = XMLUtils.getAttribute(n, "lotSize");
            String valueMod = XMLUtils.getAttribute(n, "valueMod");
            TradeGood child = new TradeGood();
            child.setOID(LongUtils.parseLong(code));
            if (!StringUtils.isTrivial(name))
                child.setName(name);
            else
                child.setName(desc);
            child.setDescription(desc);
            if (!StringUtils.isTrivial(agricultural))
                child.setAgricultural(DoubleUtils.parseDouble(agricultural));
            if (!StringUtils.isTrivial(material))
                child.setMaterial(DoubleUtils.parseDouble(material));
            if (!StringUtils.isTrivial(energy))
                child.setEnergy(DoubleUtils.parseDouble(energy));
            if (!StringUtils.isTrivial(lotSize))
                child.setLotSize(IntegerUtils.parseInt(lotSize));
            if (!StringUtils.isTrivial(valueMod))
                child.setValueMod(DoubleUtils.parseDouble(valueMod));
            if (parent.getChildren() == null)
                parent.setChildren(new ArrayList<TradeGood>());
            parent.getChildren().add(child);
            child.setCategory(parent);
            mTradeGoodIndex.put(child.getOID(), child);
            fromXML(child, n);
        }
    }
    
    public static TradeGood getTradeGood(long classification)
    {
        return mTradeGoodIndex.get(classification);
    }

    public static double hoursToProduce(TradeGood good, PopulatedObjectBean pop)
    {
        double hours = 0;
        hours += good.getAgricultural()*pop.getAgriculturalProductivity();
        hours += good.getMaterial()*pop.getMaterialProductivity();
        hours += good.getEnergy()*pop.getEnergyProductivity();
        hours *= good.getLotSize();
        return hours;
    }
    
    public static List<TradeGood> getWeightedGoods(PopulatedObjectBean pop, Map<TradeGood, Double> weights)
    {
        init();
        if (weights == null)
            weights = new HashMap<TradeGood, Double>();
        List<TradeGood> goods = new ArrayList<TradeGood>();
        assembleWeightedGoods(pop, mTradeGoodTree, weights, goods);
        Collections.sort(goods, new TradeGoodComparator(weights));
        return goods;
    }
    
    private static void assembleWeightedGoods(PopulatedObjectBean pop, TradeGood parent, Map<TradeGood, Double> weights, List<TradeGood> goods)
    {
        if ((parent.getChildren() == null) || (parent.getChildren().size() == 0))
        {
            goods.add(parent);
            weights.put(parent, hoursToProduce(parent, pop));
        }
        else
            for (TradeGood child : parent.getChildren())
                assembleWeightedGoods(pop, child, weights, goods);
    }
    
    public static void main(String[] argv)
    {
        init();
        DebugUtils.info("Major: "+mMajorCategories.size());
        DebugUtils.info("Minor: "+mMinorCategories.size());
        DebugUtils.info("Sub: "+mSubCategories.size());
        DebugUtils.info("TradeGoods: "+mTradeGoods.size());
        DebugUtils.info("All: "+mTradeGoodIndex.size());
    }
}

class TradeGoodComparator implements Comparator<TradeGood>
{
    private Map<TradeGood,Double> mWeights;
    
    public TradeGoodComparator(Map<TradeGood,Double> weights)
    {
        mWeights = weights;
    }

    @Override
    public int compare(TradeGood object1, TradeGood object2)
    {
        double w1 = mWeights.get(object1)/object1.getLotSize(); // compare apples to apples
        double w2 = mWeights.get(object2)/object2.getLotSize(); // by removing size difference
        return (int)Math.signum(w1 - w2);
    }   
}