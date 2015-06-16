/*
 * Created on Oct 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.utils;

public class MediaUtils
{
    public static final String COLOR_PREFIX = "color:";
    public static final String COLORMAP_PREFIX = "colormap:";
    public static final String IMAGE_PREFIX = "image:";
    public static final String IMAGEMAP_PREFIX = "imagemap:";
    public static final String PATTERN_PREFIX = "pattern:";
    public static final String PATTERNMAP_PREFIX = "patternmap:";
    
    public static Object getMedia(Object ref)
    {
        if (ref == null)
            return null;
        String name = ref.toString();
        if (isColor(name))
            return ColorUtils.getColor(name.substring(COLOR_PREFIX.length()));
        if (isImage(name))
            return ImageUtils.getImage(name.substring(IMAGE_PREFIX.length()));
        if (isPattern(name))
            return PatternUtils.getPattern(name.substring(PATTERN_PREFIX.length()));
        if (isColorMap(name))
            return ColorUtils.getMappedColor(name.substring(COLORMAP_PREFIX.length()));
        if (isImageMap(name))
            return ImageUtils.getMappedImage(name.substring(IMAGEMAP_PREFIX.length()));
        if (isPatternMap(name))
            return PatternUtils.getMappedPattern(name.substring(PATTERNMAP_PREFIX.length()));
        return ref;
    }
    
    public static boolean isColor(String name)
    {
        return name.startsWith(COLOR_PREFIX);
    }
    
    public static boolean isImage(String name)
    {
        return name.startsWith(IMAGE_PREFIX);
    }
    
    public static boolean isImageMap(String name)
    {
        return name.startsWith(IMAGEMAP_PREFIX);
    }
    
    public static boolean isColorMap(String name)
    {
        return name.startsWith(COLORMAP_PREFIX);
    }
    
    public static boolean isPattern(String name)
    {
        return name.startsWith(PATTERN_PREFIX);
    }
    
    public static boolean isPatternMap(String name)
    {
        return name.startsWith(PATTERNMAP_PREFIX);
    }
}
