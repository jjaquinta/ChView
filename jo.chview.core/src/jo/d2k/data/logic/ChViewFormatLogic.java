package jo.d2k.data.logic;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarColumn;
import jo.util.utils.obj.StringUtils;

public class ChViewFormatLogic
{
    
    public static String getStarName(ChViewContextBean context, StarBean star)
    {
        String name = null;
        String col = context.getStarNameColumn();
        if (col.equals( ChViewContextBean.NAME))
            name = star.getName();
        else if (col.equals( ChViewContextBean.COMMON_NAME))
            name = star.getCommonName();
        else if (col.equals( ChViewContextBean.HIP_NAME))
            name = star.getHIPName();
        else if (col.equals( ChViewContextBean.GJ_NAME))
            name = star.getGJName();
        else if (col.equals( ChViewContextBean.HD_NAME))
            name = star.getHDName();
        else if (col.equals( ChViewContextBean.HR_NAME))
            name = star.getHRName();
        else if (col.equals( ChViewContextBean.SAO_NAME))
            name = star.getSAOName();
        else if (col.equals( ChViewContextBean.TWOMASS_NAME))
            name = star.getTwoMassName();
        else 
        {
            StarColumn column = StarColumnLogic.getColumn(context.getStarNameColumn());
            if (column != null)
                name = StarColumnLogic.getText(context, star, column);
        }
        if (StringUtils.isTrivial(name))
            name = star.getName();
        return name;
    }

    public static int getStarRadius(ChViewContextBean context, StarBean star)
    {
        int s = StarExtraLogic.getClassOff(star.getSpectra());
        switch (s)
        {
            case 0:
                return context.getStar0Radius();
            case 1:
                return context.getStar1Radius();
            case 2:
                return context.getStar2Radius();
            case 3:
                return context.getStar3Radius();
            case 4:
                return context.getStar4Radius();
            case 5:
                return context.getStar5Radius();
        }
        throw new IllegalArgumentException("Unexpected spectra: "+star.getSpectra());
    }

    public static String getRouteName(ChViewContextBean context, int idx)
    {
        switch (idx)
        {
            case 0:
                return context.getRoute1Name();
            case 1:
                return context.getRoute2Name();
            case 2:
                return context.getRoute3Name();
            case 3:
                return context.getRoute4Name();
            case 4:
                return context.getRoute5Name();
            case 5:
                return context.getRoute6Name();
            case 6:
                return context.getRoute7Name();
            case 7:
                return context.getRoute8Name();
        }
        throw new IllegalStateException(); // shouldn't happen
    }

}
