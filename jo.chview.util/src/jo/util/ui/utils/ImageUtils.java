/*
 * Created on Oct 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.util.utils.ExtensionPointUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ImageUtils
{
    private static List<String> mPlugins;
    private static Map<String,String>      mImageNameMap;
    private static Map<String,ImageDescriptor>      mImageDescriptors;
    private static Map<String,Image>      mImages;
    
    private static final String[] IMAGEPLUGIN_TEXT = { "id" };
    private static final String[] IMAGEMAP_TEXT = { "name", "path" };

    
    private static synchronized void init()
    {
        if (mPlugins != null)
            return;
        mPlugins = new ArrayList<String>();
        mImageNameMap = new HashMap<String, String>();
        mImageDescriptors = new HashMap<String, ImageDescriptor>();
        mImages = new HashMap<String, Image>();
        List<Map<String,Object>> imageplugins = ExtensionPointUtils.getExecutableExtensionInfo("jo.chview.util.imageplugin", IMAGEPLUGIN_TEXT, null);
        for (int i = 0; i < imageplugins.size(); i++)
        {
            String id = (String)imageplugins.get(i).get(IMAGEPLUGIN_TEXT[0]);
            mPlugins.add(id);
        }
        List<Map<String,Object>> imagemap = ExtensionPointUtils.getExecutableExtensionInfo("jo.chview.util.imagemap", IMAGEMAP_TEXT, null);
        for (int i = 0; i < imagemap.size(); i++)
        {
            String plugin = (String)imagemap.get(i).get("$NamespaceIdentifier");
            String name = (String)imagemap.get(i).get(IMAGEMAP_TEXT[0]);
            String path = (String)imagemap.get(i).get(IMAGEMAP_TEXT[1]);
            if (path.indexOf("$") < 0)
                path = plugin + "$" + path;
            mImageNameMap.put(name, path);
        }
    }
    
    public static void addPluginID(String id)
    {
        init();
        mPlugins.add(id);
    }
    
    public static ImageDescriptor getImageDescriptor(String path)
    {
        init();
        ImageDescriptor ret = (ImageDescriptor)mImageDescriptors.get(path);
        if (ret != null)
            return ret;
        int o = path.indexOf('$');
        if (o >= 0)
        {
            ret = AbstractUIPlugin.imageDescriptorFromPlugin(path.substring(0, o), path.substring(o + 1));
            if (ret != null)
            {
                mImageDescriptors.put(path, ret);
                return ret;
            }
        }
        for (String id : mPlugins)
        {
            ret = AbstractUIPlugin.imageDescriptorFromPlugin(id, path);
            if (ret != null)
            {
                mImageDescriptors.put(path, ret);
                return ret;
            }
        }
        return null;
    }
    
    public static Image getImage(String path)
    {
        init();
        Image ret = (Image)mImages.get(path);
        if (ret != null)
            return ret;
        ImageDescriptor id = getImageDescriptor(path);
        if (id == null)
            return null;
        ret = id.createImage();
        mImages.put(path, ret);
        return ret;
    }
    
    public static void setImage(String path, Image img)
    {
        if (img == null)
            mImages.remove(path);
        else
            mImages.put(path, img);
    }

    public static ImageDescriptor getMappedImageDescriptor(String name)
    {
        init();
        String path = (String)mImageNameMap.get(name);
        if (path == null)
            return null;
        else
            return getImageDescriptor(path);
    }
    
    public static String getMappedName(String name)
    {
        init();
        return (String)mImageNameMap.get(name);
    }
    
    public static Image getMappedImage(String name)
    {
        init();
        String path = getMappedName(name);
        if (path == null)
            return null;
        else
            return getImage(path);
    }

    public static void setMappedImage(String name, Image image)
    {
        init();
        mImageNameMap.put(name, name);
        mImages.put(name, image);
    }
}
