/*
 * Created on Sep 27, 2005
 *
 */
package jo.util.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ExtensionPointUtils
{
	public static List<Object> processExtensions(String extensionPointId, IExtensionPointProcessor processor)
	{
        List<Object> extns = new ArrayList<Object>();
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry
                .getExtensionPoint(extensionPointId);
        IExtension[] extensions = extensionPoint.getExtensions();
        for (int i = 0; i < extensions.length; i++)
        {
            IExtension extension = extensions[i];
            IConfigurationElement[] elements = extension
                    .getConfigurationElements();
            for (int j = 0; j < elements.length; j++)
            {
                IConfigurationElement element = elements[j];
                try
                {
                    Object extn = processor.process(element);
                    if (extn != null)
                    	extns.add(extn);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return extns;
	}
	
    public static List<Object> getExecutableExtensions(String extensionPointId, String attributeName)
    {
    	return processExtensions(extensionPointId, new ExecutableExtensionPointProcessor(attributeName));
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String,Object>> getExecutableExtensionInfo(String extensionPointId, String[] textAttributes, String[] exeAttributes)
    {
        List<Object> extns = processExtensions(extensionPointId, new InfoExtensionPointProcessor(textAttributes, exeAttributes));
        List<Map<String,Object>> ret = new ArrayList<Map<String,Object>>();
        for (Object o : extns)
            ret.add((Map<String,Object>)o);
        return ret;
    }
}
