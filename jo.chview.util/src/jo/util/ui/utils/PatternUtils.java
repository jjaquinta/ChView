/*
 * Created on Oct 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

public class PatternUtils
{
    private static Map<String, Pattern>      mPatterns;
    private static Map<String, Pattern>      mMappedPatterns;
    
    private static synchronized void init()
    {
        if (mPatterns != null)
            return;
        mPatterns = new HashMap<String, Pattern>();
        mMappedPatterns = new HashMap<String, Pattern>();
    }
    
    public static Pattern getPattern(String path)
    {
        init();
        Pattern ret = (Pattern)mPatterns.get(path);
        if (ret != null)
            return ret;
        Image id = ImageUtils.getImage(path);
        if (id == null)
            return null;
        ret = new Pattern(Display.getDefault(), id);
        mPatterns.put(path, ret);
        return ret;
    }
    
    public static void setPattern(String path, Pattern pat)
    {
        if (pat == null)
            mPatterns.remove(path);
        else
            mPatterns.put(path, pat);
    }
    
    public static Pattern getMappedPattern(String name)
    {
        init();
        String path = ImageUtils.getMappedName(name);
        if (path == null)
            return null;
        Pattern ret = (Pattern)mMappedPatterns.get(path);
        if (ret != null)
            return ret;
        Image id = ImageUtils.getImage(path);
        if (id == null)
            return null;
        ret = new Pattern(Display.getDefault(), id);
        mMappedPatterns.put(path, ret);
        return ret;
    }
}
