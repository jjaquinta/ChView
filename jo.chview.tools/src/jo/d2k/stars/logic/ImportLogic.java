package jo.d2k.stars.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.DataLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.logic.CSVLogic;

public class ImportLogic
{
    private static final int CHUNK = 100;
    
    public static final void main(String[] argv)
    {
        DataLogic.addDataSource("D2K", "MYSQL", "jdbc:mysql://www.ocean-of-storms.com/ocean50_d2k?user=ocean50_d2kadmin&password=d2kadmin", "false", "true");
        Random rnd = new Random(0);
        try
        {
            StarLogic.deleteAll();
            //File d2kInput = new File("C:\\temp\\data\\chview2\\HYG-Database-master\\d2k.csv");
            File d2kInput = new File("D:\\temp\\data\\chview2\\simbad\\d2k.csv");
            Collection<?> stars = CSVLogic.fromCSV(d2kInput, StarBean.class);
            while (stars.size() > 0)
            {
                List<StarBean> chunk = new ArrayList<StarBean>();
                for (Iterator<?> i = stars.iterator(); i.hasNext(); )
                {
                    StarBean star = (StarBean)i.next();
                    adjustSpectra(star, rnd);
                    chunk.add(star);
                    i.remove();
                    if (chunk.size() == CHUNK)
                        break;
                }
                StarLogic.update(chunk);
                System.out.println(stars.size());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void adjustSpectra(StarBean star, Random rnd)
    {
        String spectra = star.getSpectra();
        if ((spectra.length() == 1) || !Character.isDigit(spectra.charAt(1)))
        {
            int roll = rnd.nextInt(10);
            spectra = spectra.substring(0, 1) + String.valueOf(roll) + spectra.substring(1);
        }
        if (!spectra.endsWith("I") && !spectra.endsWith("V"))
        {
            int s = StarGenLogic.getSpectrum(spectra);
            spectra += StarGenLogic.makeClass(s, rnd);
        }
        star.setSpectra(spectra);
    }
}
