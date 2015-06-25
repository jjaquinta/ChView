package jo.d4w.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import jo.d4w.data.PopulatedObjectBean;
import jo.util.utils.io.ResourceUtils;

public class NameLogic
{
    private static final long CITY_FILE_LENGTH = 10011 - 11;
    
    private static Map<Long, String> mCityNameCache = new HashMap<Long, String>();
    
    public static String getCityName(Random rnd)
    {
        long idx = rnd.nextLong();
        //System.out.println("idx="+idx);
        if (mCityNameCache.containsKey(idx))
            return mCityNameCache.get(idx);
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream("cities.txt", PopulatedObjectBean.class);
            // seek to random position in file
            long off = (Math.abs(idx) % CITY_FILE_LENGTH);
            //System.out.println("off="+idx);
            is.skip(off);
            // skip until newline
            for (;;)
            {
                int ch = is.read();
                if (ch < 0)
                {
                    is.close();
                    is = ResourceUtils.loadSystemResourceStream("cities.txt", PopulatedObjectBean.class);
                    break;
                }
                if (ch == '\n')
                    break;
            }
            // convert to reader and read name
            StringBuffer name = new StringBuffer();
            InputStreamReader rdr = new InputStreamReader(is, "utf-8");
            for (;;)
            {
                int ch = rdr.read();
                if ((ch == '\n') || (ch == '\r'))
                    break;
                name.append((char)ch);
            }
            is.close();
            String n = name.toString();
            n = deAccent(n);
            mCityNameCache.put(idx, n);
            return n;
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Error while reading city file", e);
        }
    }
    
    private static Pattern mDeAccentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    
    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        return mDeAccentPattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
    public static void main(String[] argv)
    {
        Random r = new Random(0);
        for (int i = 0; i < 16; i++)
            System.out.println(i+": "+getCityName(r));
        r = new Random(0);
        for (int i = 0; i < 16; i++)
            System.out.println(i+": "+getCityName(r));
    }
}
