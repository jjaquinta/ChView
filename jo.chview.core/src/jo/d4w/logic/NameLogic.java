package jo.d4w.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jo.d4w.data.PopulatedObjectBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;

public class NameLogic
{
    private static final long CITY_FILE_LENGTH = 132454 - 11;
    
    private static Map<Long, String> mCityNameCache = new HashMap<Long, String>();
    
    public static String getCityName(Random rnd)
    {
        long idx = rnd.nextLong();
        //DebugUtils.trace("idx="+idx);
        if (mCityNameCache.containsKey(idx))
            return mCityNameCache.get(idx);
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream("cities.txt", PopulatedObjectBean.class);
            // seek to random position in file
            long off = (Math.abs(idx) % CITY_FILE_LENGTH);
            //DebugUtils.trace("off="+idx);
            is.skip(off);
            // skip until newline
            for (;;)
            {
                int ch = is.read();
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
            mCityNameCache.put(idx, name.toString());
            return name.toString();
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Error while reading city file", e);
        }
    }
    
    public static void main(String[] argv)
    {
        Random r = new Random(0);
        for (int i = 0; i < 16; i++)
            DebugUtils.info(i+": "+getCityName(r));
        r = new Random(0);
        for (int i = 0; i < 16; i++)
            DebugUtils.info(i+": "+getCityName(r));
    }
}
