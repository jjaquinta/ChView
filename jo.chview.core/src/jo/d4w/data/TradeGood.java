package jo.d4w.data;

import java.util.List;

import jo.util.beans.Bean;

public class TradeGood extends Bean
{
    private String  mName;
    private String  mDescription;
    private TradeGood mCategory;
    private List<TradeGood> mChildren;
    private Double  mAgricultural;
    private Double  mMaterial;
    private Double  mEnergy;
    private Integer mLotSize;
    private Double  mValueMod;
    
    public String getDescription()
    {
        return mDescription;
    }
    public void setDescription(String description)
    {
        mDescription = description;
    }
    public TradeGood getCategory()
    {
        return mCategory;
    }
    public void setCategory(TradeGood category)
    {
        mCategory = category;
    }
    public List<TradeGood> getChildren()
    {
        return mChildren;
    }
    public void setChildren(List<TradeGood> children)
    {
        mChildren = children;
    }
    public Double getAgricultural()
    {
        if ((mAgricultural == null) && (mCategory != null))
            return mCategory.getAgricultural();
        else
            return mAgricultural;
    }
    public void setAgricultural(Double agricultural)
    {
        mAgricultural = agricultural;
    }
    public Double getMaterial()
    {
        if ((mMaterial == null) && (mCategory != null))
            return mCategory.getMaterial();
        else
            return mMaterial;
    }
    public void setMaterial(Double mineral)
    {
        mMaterial = mineral;
    }
    public Double getEnergy()
    {
        if ((mEnergy == null) && (mCategory != null))
            return mCategory.getEnergy();
        else
            return mEnergy;
    }
    public void setEnergy(Double energy)
    {
        mEnergy = energy;
    }
    public Integer getLotSize()
    {
        if ((mLotSize == null) && (mCategory != null))
            return mCategory.getLotSize();
        else
            return mLotSize;
    }
    public void setLotSize(Integer lotSize)
    {
        mLotSize = lotSize;
    }
    public Double getValueMod()
    {
        if ((mValueMod == null) && (mCategory != null))
            return mCategory.getValueMod();
        else
            return mValueMod;
    }
    public void setValueMod(Double lotValue)
    {
        mValueMod = lotValue;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
}
