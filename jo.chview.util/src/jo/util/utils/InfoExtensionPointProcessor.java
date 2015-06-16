package jo.util.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

class InfoExtensionPointProcessor implements IExtensionPointProcessor
{
	private String[]	mTextAttributes;
	private String[]	mExeAttributes;
	
	public InfoExtensionPointProcessor(String[] textAttributes, String[] exeAttributes)
	{
		mTextAttributes = textAttributes;
		mExeAttributes = exeAttributes;
	}
	
	public Object process(IConfigurationElement element) 
	{
        Map<String,Object> map = new HashMap<String,Object>();
        if (mTextAttributes != null)
            for (int k = 0; k < mTextAttributes.length; k++)
                map.put(mTextAttributes[k], element.getAttribute(mTextAttributes[k]));
        if (mExeAttributes != null)
            for (int k = 0; k < mExeAttributes.length; k++)
                try
                {
                    Object extn = element.createExecutableExtension(mExeAttributes[k]);
                    map.put(mExeAttributes[k], extn);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
        map.put("$NamespaceIdentifier", element.getDeclaringExtension().getNamespaceIdentifier());
        return map;
	}
	
}
