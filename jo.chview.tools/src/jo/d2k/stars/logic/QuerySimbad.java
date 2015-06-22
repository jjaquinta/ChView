package jo.d2k.stars.logic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import jo.util.html.HTTPThread;

public class QuerySimbad
{
    static final long TIMEOUT = 300*1000; // 5 minutes
    
    public static void main(String[] argv)
    {
        try
        {
            String criteria = "plx > 12";
            getCatalog(criteria, "simbad", null);
            getCatalog(criteria, "simbad", "2MASS");
            getCatalog(criteria, "simbad", "GJ");
            getCatalog(criteria, "simbad", "HD");
            getCatalog(criteria, "simbad", "HIP");
            getCatalog(criteria, "simbad", "HR");
            getCatalog(criteria, "simbad", "NAME");
            getCatalog(criteria, "simbad", "SAO");
            criteria = "plx <= 12 & Vmag <= 6";
            getCatalog(criteria, "bright", null);
            getCatalog(criteria, "bright", "2MASS");
            getCatalog(criteria, "bright", "GJ");
            getCatalog(criteria, "bright", "HD");
            getCatalog(criteria, "bright", "HIP");
            getCatalog(criteria, "bright", "HR");
            getCatalog(criteria, "bright", "NAME");
            getCatalog(criteria, "bright", "SAO");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static void getCatalog(String criteria, String base, String cat) throws UnsupportedEncodingException
    {
        File file;
        if (cat == null)
            file = new File("D:\\temp\\data\\chview2\\simbad\\simbad_data2\\"+base+".txt");
        else
            file = new File("D:\\temp\\data\\chview2\\simbad\\simbad_data2\\"+base+"-"+cat+".txt");
        getSimbadData(criteria, file, cat);
    }
    
    private static void getSimbadData(String criteria, File file, String catalog) throws UnsupportedEncodingException
    {
        if (file.exists() && (file.length() > 1024))
            return;
        String url="http://simbad.u-strasbg.fr/simbad/sim-sam?Criteria="+URLEncoder.encode(criteria, "utf-8")
                +"&OutputMode=LIST"
                +"&output.format=ASCII"
                +"&otypedisp=V"
                +"&list.plxsel=on"
                +"&list.spsel=on"
                +"&list.fluxsel=on&V=on"
                +"&list.bibsel=off"
                +"&list.notesel=off"
                + "&maxObject=20000";
        if (catalog != null)
        {
            url += "&list.idopt=CATLIST&list.idcat="+catalog;
        }
        System.out.println(url);
        downloadFile(url, null, file, null);
        if (file.exists())
            System.out.println("size="+file.length());
    }
    
    private static String downloadFile(String path, String referer, File file, Collection<String> dupLengths)
    {
        File parent = file.getParentFile();
        parent.mkdirs();
        OutputStream os;
        try
        {
            os = new BufferedOutputStream(new FileOutputStream(file));
        }
        catch (FileNotFoundException e)
        {
            return "no-file";
        }
        HTTPThread getThread = new HTTPThread(os, path, referer, dupLengths);
        getThread.start();
        while (!getThread.isDone())
        {
            if (System.currentTimeMillis() - getThread.getLastActive() > TIMEOUT)
            {
                getThread.setDone(true);
                getThread.setError("timeout");
                try { os.close(); } catch (IOException ee) { }
                file.delete();
                break;
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
        }
        try { os.close(); } catch (IOException ee) { }
        if (getThread.isDone() && (getThread.getError() == null))
        {
            return null;
        }
        file.delete();
        return getThread.getError();
    }

}
