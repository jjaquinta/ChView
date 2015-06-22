package jo.d2k.stars.logic;

import java.util.List;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;

public class ExportLogic
{
    public static final void main(String[] argv)
    {
        try
        {
            //List<StarBean> stars = StarLogic.getByQuadrant("000");
            List<StarBean> stars = StarLogic.getAllWithin(0, 0, 0, 15);
            System.out.println(stars.size()+" stars");
            for (StarBean s : stars)
            {
                System.out.println(s.getName()+","+s.getQuadrant()+","+s.getX()+","+s.getY()+","+s.getZ()+","+s.getSpectra()+","+s.getAbsMag());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
