/*
 * Created on Oct 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jo.util.utils.ExtensionPointUtils;
import jo.util.utils.obj.IntegerUtils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorUtils
{
    private static Map<Object,Object>  mRawColorMap;
    private static Map<String,Object> COLOR_MAP;
    private static Map<String,String> mColorNameMap;
    private static final String[] COLORS_TEXT = { "name", "rgb" };
    private static final String[] COLORMAP_TEXT = { "name", "color" };
    
    public static synchronized void init()
    {
        if (COLOR_MAP != null)
            return;
        mRawColorMap = new HashMap<Object, Object>();
        List<Map<String,Object>> map = ExtensionPointUtils.getExecutableExtensionInfo("jo.chview.util.colors", COLORS_TEXT, null);
        COLOR_MAP = new HashMap<String, Object>(map.size());
        for (int i = 0; i < map.size(); i++)
        {
            String name = (String)map.get(i).get(COLORS_TEXT[0]);
            String rgb = (String)map.get(i).get(COLORS_TEXT[1]);
            //DebugUtils.trace(name+"->"+rgb);
            COLOR_MAP.put(name, rgb);
        }
        mColorNameMap = new HashMap<String, String>();
        List<Map<String,Object>> imagemap = ExtensionPointUtils.getExecutableExtensionInfo("jo.chview.util.colormap", COLORMAP_TEXT, null);
        for (int i = 0; i < imagemap.size(); i++)
        {
            String name = (String)imagemap.get(i).get(COLORMAP_TEXT[0]);
            String color = (String)imagemap.get(i).get(COLORMAP_TEXT[1]);
            mColorNameMap.put(name, color);
        }
    }
    
    public static void addColor(String name, String rgb)
    {
        synchronized (COLOR_MAP)
        {
            COLOR_MAP.put(name, rgb);
        }
    }
    
    public static void addColor(String name, Color c)
    {
        synchronized (COLOR_MAP)
        {
            COLOR_MAP.put(name, c);
        }
    }
    
    public static Color getColor(int r, int g, int b)
    {
        Long key = new Long((r<<16)|(g<<8)|(b<<0));
        Color ret = (Color)mRawColorMap.get(key);
        if (ret == null)
        {
            ret = new Color(Display.getDefault(), r, g, b);
            mRawColorMap.put(key, ret);
        }
        return ret;
    }
    
    public static Color getColor(Object color)
    {
        if (color == null)
            return getColor(0L);
        if (color instanceof Color)
            return (Color)color;
        if (color instanceof RGB)
            return getColor(((RGB)color).red, ((RGB)color).green, ((RGB)color).blue);
        return getColor(color.toString());
    }
    
    public static Color getColor(long rgb)
    {
        Long key = new Long(rgb);
        Color ret = (Color)mRawColorMap.get(key);
        if (ret == null)
        {
            int r = (int)((rgb&0xff0000)>>16);
            int g = (int)((rgb&0xff00)>>8);
            int b = (int)((rgb&0xff)>>0);
            ret = new Color(Display.getDefault(), r, g, b);
            mRawColorMap.put(key, ret);
        }
        return ret;
    }
    
    public static double[] RGBtoHSL(Color c)
    {
        double var_R = (c.getRed() / 255.0);                     //Where RGB values = 0 ÷ 255
        double var_G = (c.getGreen() / 255.0);
        double var_B = (c.getBlue() / 255.0);

        double var_Min = Math.min(var_R, Math.min(var_G, var_B));    //Min. value of RGB
        double var_Max = Math.max(var_R, Math.max(var_G, var_B));    //Max. value of RGB
        double del_Max = var_Max - var_Min;             //Delta RGB value

        double H = 0;
        double S = 0;
        double L = ( var_Max + var_Min ) / 2;

        if (del_Max == 0)                     //This is a gray, no chroma...
        {
           H = 0;                                //HSL results = 0 ÷ 1
           S = 0;
        }
        else                                    //Chromatic data...
        {
           if (L < 0.5)
               S = del_Max/(var_Max + var_Min);
           else
               S = del_Max/(2 - var_Max - var_Min);
           
           double del_R = (((var_Max - var_R)/6) + (del_Max/2))/del_Max;
           double del_G = (((var_Max - var_G)/6) + (del_Max/2))/del_Max;
           double del_B = (((var_Max - var_B)/6) + (del_Max/2))/del_Max;

           if  (var_R == var_Max) 
               H = del_B - del_G;
           else if (var_G == var_Max) 
               H = (1.0/3.0) + del_R - del_B;
           else if (var_B == var_Max) 
               H = (2.0/3.0) + del_G - del_R;

           if (H < 0)
               H += 1.0;
           if (H > 1)
               H -= 1.0;
        }        
        double[] ret = new double[3];
        ret[0] = H;
        ret[1] = S;
        ret[2] = L;
        return ret;
    }
    
    public static int[] HSLtoRGB(double[] hsl)
    {
        double H = hsl[0];
        double S = hsl[1];
        double L = hsl[2];
        double R = 0;
        double G = 0;
        double B = 0;
        
        if (S == 0)                       //HSL values = 0 ÷ 1
        {
           R = L*255;                      //RGB results = 0 ÷ 255
           G = L*255;
           B = L*255;
        }
        else
        {
            double var_2;
           if (L < 0.5) 
               var_2 = L*(1 + S);
           else           
               var_2 = (L + S) - (S*L);

           double var_1 = 2*L - var_2;

           R = 255*Hue_2_RGB(var_1, var_2, H + (1.0/3.0));
           G = 255*Hue_2_RGB(var_1, var_2, H);
           B = 255*Hue_2_RGB(var_1, var_2, H - (1.0/3.0));
        }
        int ret[] = new int[3];
        ret[0] = (int)R;
        ret[1] = (int)G;
        ret[2] = (int)B;
        return ret;
    }
    
    private static double Hue_2_RGB(double v1, double v2, double vH)             //Function Hue_2_RGB
    {
       if (vH < 0) 
           vH += 1;
       if (vH > 1) 
           vH -= 1;
       if ((6*vH) < 1) 
           return (v1 + (v2 - v1)*6*vH);
       if ((2*vH) < 1) 
           return (v2);
       if ((3*vH) < 2) 
           return (v1 + (v2 - v1)*((2.0/3.0) - vH)*6);
       return v1;
    }
    
    public static String toString(int[] rgb)
    {
        return "["+rgb[0]+","+rgb[1]+","+rgb[2]+"]";
    }
    
    public static String toString(double[] hsl)
    {
        return "<"+hsl[0]+","+hsl[1]+","+hsl[2]+">";
    }
    
    public static String toString(Color c)
    {
        return "["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]";
    }

    public static Color brighter(String name) 
    {
        Color ret = null;
        if (COLOR_MAP.containsKey(name+"!brighter"))
            ret = getColor(name+"!brighter");
        else
        {
            Color c = getColor(name);
            ret = brighter(c);
            addColor(name+"!brighter", ret);
        }
        return ret;
    }

    public static Color brighter(Color c) 
    {
        return brighter(c, .5);
    }

    public static Color brighter(Color c, double FACTOR) 
    {
        if ((c.getRed() == 0) && (c.getGreen() == 0) && (c.getBlue() == 0))
            return getColor((int)(255*FACTOR), (int)(255*FACTOR), (int)(255*FACTOR));
        //DebugUtils.trace(toString(c));
        double[] hsl = RGBtoHSL(c);
        //DebugUtils.trace(toString(hsl));
        hsl[2] = Math.min(1.0, hsl[2]*(1.0 +FACTOR));
        //DebugUtils.trace(toString(hsl));
        int[] rgb = HSLtoRGB(hsl);
        //DebugUtils.trace(toString(rgb));
        return getColor(rgb[0], rgb[1], rgb[2]);
    }

    public static Color darker(Color c) 
    {
        return darker(c, .7);
    }

    public static Color darker(Color c, double FACTOR) 
    {
        return getColor(Math.max((int)(c.getRed()  *FACTOR), 0), 
             Math.max((int)(c.getGreen()*FACTOR), 0),
             Math.max((int)(c.getBlue() *FACTOR), 0));
    }

    public static Color darker(String name) 
    {
        Color ret = null;
        if (COLOR_MAP.containsKey(name+"!darker"))
            ret = getColor(name+"!darker");
        else
        {
            Color c = getColor(name);
            ret = darker(c);
            addColor(name+"!darker", ret);
        }
        return ret;
    }
    
    public static Color getColor(String name)
    {
        init();
        if (name.startsWith("#") && (name.length() == 7))
        {
            int r = Integer.parseInt(name.substring(1, 3), 16);
            int g = Integer.parseInt(name.substring(3, 5), 16);
            int b = Integer.parseInt(name.substring(5, 7), 16);
            return getColor(r, g, b);
        }
        if (name.startsWith("rgb(") && name.endsWith(")"))
        {
            StringTokenizer st = new StringTokenizer(name.substring(4, name.length() - 1), ", ");
            int r = IntegerUtils.parseInt(st.nextToken());
            int g = IntegerUtils.parseInt(st.nextToken());
            int b = IntegerUtils.parseInt(st.nextToken());
            return getColor(r, g, b);
        }
        if (name.indexOf(',') > 0)
        {
            StringTokenizer st = new StringTokenizer(name, ", ");
            if (st.countTokens() == 3)
            {
                int r = IntegerUtils.parseInt(st.nextToken());
                int g = IntegerUtils.parseInt(st.nextToken());
                int b = IntegerUtils.parseInt(st.nextToken());
                return getColor(r, g, b);
            }
        }
        synchronized (COLOR_MAP)
        {
            Object val = COLOR_MAP.get(name);
            if (val == null)
                val = COLOR_MAP.values().iterator().next();
            if (val instanceof Color)
                return (Color)val;
            String rgb = val.toString();
            if (rgb.startsWith("*"))
                return getColor(rgb.substring(1));
            int r = Integer.parseInt(rgb.substring(0, 2), 16);
            int g = Integer.parseInt(rgb.substring(2, 4), 16);
            int b = Integer.parseInt(rgb.substring(4, 6), 16);
            //DebugUtils.trace(name+"->"+rgb+"->"+r+"."+g+"."+b);
            Color ret = getColor(r, g, b);
            COLOR_MAP.put(name, ret);
            return ret;
        }
    }
    
    public static Color getMappedColor(String name)
    {
        String path = (String)mColorNameMap.get(name);
        if (path == null)
            return null;
        else
            return getColor(path);
    }
}
