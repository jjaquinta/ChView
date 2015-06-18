package jo.d2k.data.logic;

import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.logic.schema.ISchemaComparator;

public class FilterLogic
{
    public static boolean isFiltered(StarBean star, StarFilter filter)
    {
        char spectra = star.getSpectra().charAt(0);
        if (filter.isSpectraO() && (spectra == 'O'))
            return true;
        if (filter.isSpectraB() && (spectra == 'B'))
            return true;
        if (filter.isSpectraA() && (spectra == 'A'))
            return true;
        if (filter.isSpectraF() && (spectra == 'F'))
            return true;
        if (filter.isSpectraG() && (spectra == 'G'))
            return true;
        if (filter.isSpectraK() && (spectra == 'K'))
            return true;
        if (filter.isSpectraM() && (spectra == 'M'))
            return true;
        if (filter.isSpectraL() && (spectra == 'L'))
            return true;
        if (filter.isSpectraT() && (spectra == 'T'))
            return true;
        if (filter.isSpectraY() && (spectra == 'Y'))
            return true;
        if (filter.getGenerated() != null)
            if (filter.getGenerated() == star.isGenerated())
                return true;
        for (String key : filter.getExtraFields().keySet())
        {
            String starVal = StarLogic.getMetadata(star).get(key);
            if (starVal != null)
            {
                String filterVal = filter.getExtraFields().get(key);
                StarSchemaBean schema = StarSchemaLogic.getSchema(key);
                if (schema.getType() == StarSchemaBean.TEXT)
                {
                    if (starVal.toLowerCase().indexOf(filterVal.toLowerCase()) >= 0)
                        return true;
                }
                else if (schema.getType() == StarSchemaBean.TAGS)
                {
                    starVal = " "+starVal.toLowerCase()+" ";
                    filterVal = " "+filterVal.toLowerCase()+" ";
                    if (starVal.indexOf(filterVal) >= 0)
                        return true;                    
                }
                else
                {
                    if (starVal.equals(filterVal))
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean isAnyFilter(StarFilter filter)
    {
        if (filter.isSpectraO())
            return true;
        if (filter.isSpectraB())
            return true;
        if (filter.isSpectraA())
            return true;
        if (filter.isSpectraF())
            return true;
        if (filter.isSpectraG())
            return true;
        if (filter.isSpectraK())
            return true;
        if (filter.isSpectraM())
            return true;
        if (filter.isSpectraL())
            return true;
        if (filter.isSpectraT())
            return true;
        if (filter.isSpectraY())
            return true;
        if (filter.getGenerated() != null)
            return true;
        if (filter.getExtraFields().size() > 0)
            return true;
        return false;
    }
    
    public static boolean isFiltered(ChViewContextBean context, StarBean star, FilterConditionBean cond)
    {
        StarColumn col = StarColumnLogic.getColumn(cond.getID());
        ISchemaComparator comp = col.getComparator();
        boolean match = comp.isMatch(context, star, cond);
        return match;
    }
    
    public static boolean isFiltered(ChViewContextBean context, StarBean star, List<FilterConditionBean> conds, boolean and)
    {
        if (and)
        {
            for (FilterConditionBean cond : conds)
                if (!isFiltered(context, star, cond))
                    return false;
            return true;
        }
        else
        {
            for (FilterConditionBean cond : conds)
                if (isFiltered(context, star, cond))
                    return true;
            return false;
        }
    }
}
