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

import jo.util.utils.ExtensionPointUtils;
import jo.util.utils.FormatUtils;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class FontUtils
{
    private static Map<String, Object> FONT_MAP;
    private static final String[] FONTS_TEXT = { "name", "fontname", "size", "style" };
    
    public static synchronized void init()
    {
        if (FONT_MAP != null)
            return;
        List<Map<String,Object>> map = ExtensionPointUtils.getExecutableExtensionInfo("jo.chview.util.fonts", FONTS_TEXT, null);
        FONT_MAP = new HashMap<String, Object>(map.size());
        for (int i = 0; i < map.size(); i++)
        {  
            String name = (String)map.get(i).get(FONTS_TEXT[0]);
            String[] info = new String[3];
            info[0] = (String)map.get(i).get(FONTS_TEXT[1]);
            info[1] = (String)map.get(i).get(FONTS_TEXT[2]);
            info[2] = (String)map.get(i).get(FONTS_TEXT[3]);
            FONT_MAP.put(name, info);
        }
        /*
        DebugUtils.trace("SCALABLE FONTS:");
        FontData[] f = Display.getDefault().getFontList(null, true);
        for (int i = 0; i < f.length; i++)
            DebugUtils.trace("  "+f[i].getName()+"/"+f[i].getHeight()+"/"+f[i].getStyle());
        DebugUtils.trace("UNSCALABLE FONTS:");
        f = Display.getDefault().getFontList(null, false);
        for (int i = 0; i < f.length; i++)
            DebugUtils.trace("  "+f[i].getName()+"/"+f[i].getHeight()+"/"+f[i].getStyle());
        */
    }
    
    public static void addFont(String name, String[] info)
    {
        synchronized (FONT_MAP)
        {
            FONT_MAP.put(name, info);
        }
    }
    
    public static void addFont(String name, Font f)
    {
        synchronized (FONT_MAP)
        {
            FONT_MAP.put(name, f);
        }
    }
    
    public static Font getFont(Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof Font)
            return (Font)obj;
        if (obj instanceof FontData)
            return new Font(Display.getDefault(), (FontData)obj);
        return getFont(obj.toString());
    }
    
    public static Font getFont(String name)
    {
        synchronized (FONT_MAP)
        {
            Object val = FONT_MAP.get(name);
            if (val == null)
            {
                if (name.indexOf("|") > 0)
                {
                    FontData[] fd = PreferenceConverter.readFontData(name);
                    if (fd != null)
                    {
                        //DebugUtils.trace("Creating font from "+name);
                        val = new Font(Display.getDefault(), fd);
                        FONT_MAP.put(name, val);
                    }
                }
                if (val == null)
                    val = FONT_MAP.values().iterator().next();
            }
            if (val instanceof Font)
                return (Font)val;
            String[] info = (String[])val;
            int size = FormatUtils.parseInt(info[1], 10);
            int style = SWT.NULL;
            if (info[2].indexOf("b") >= 0)
                style |= SWT.BOLD;
            if (info[2].indexOf("i") >= 0)
                style |= SWT.ITALIC;
            if (info[2].indexOf("n") >= 0)
                style |= SWT.NORMAL;
            //DebugUtils.trace("Creating font for "+name);
            Font ret = new Font(Display.getDefault(), info[0], size, style);
            FONT_MAP.put(name, ret);
            return ret;
        }
    }
    
    public static Font getFont(String name, int size, int style)
    {
        String key = "FONT:"+name+"/"+size+"/"+style;
        synchronized (FONT_MAP)
        {
            Object val = FONT_MAP.get(key);
            if (val instanceof Font)
                return (Font)val;
            Font ret = new Font(Display.getDefault(), name, size, style);
            FONT_MAP.put(key, ret);
            return ret;
        }
    }
    
    public static Font getFont(Font f, int sizeDelta, int newStyles)
    {
        String name = f.getFontData()[0].getName();
        int size = f.getFontData()[0].getHeight() + sizeDelta;
        int style = f.getFontData()[0].getStyle() | newStyles;
        return getFont(name, size, style);
    }

    public static void makeTextFitV(Control ctrl, String txt)
    {
        int targetH = ctrl.getSize().y;
        Font f = ctrl.getFont();
        for (;;)
        {
            GC gc = new GC(ctrl);
            gc.setFont(f);
            FontMetrics fm = gc.getFontMetrics();
            int h = fm.getHeight();
            if (h < targetH)
                break;
            FontData fd = f.getFontData()[0];
            f = FontUtils.getFont(fd.getName(), fd.getHeight() - 1, fd.getStyle());
        }
        ctrl.setFont(f);
    }
    
    public static String toString(Object font)
    {
        if (font instanceof Font)
        {
            Font f = (Font)font;
            return f.getFontData()[0].toString();
        }
        else if (font instanceof FontData)
        {
            FontData f = (FontData)font;
            return f.toString();
        }
        else if (font instanceof FontData[])
        {
            FontData[] f = (FontData[])font;
            return f[0].toString();
        }
        else if (font == null)
            return null;
        return font.toString();
    }
}
