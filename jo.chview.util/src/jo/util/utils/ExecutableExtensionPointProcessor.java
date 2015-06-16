package jo.util.utils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class ExecutableExtensionPointProcessor implements IExtensionPointProcessor
{
    private String  mRequiredAttribute;
    private String  mRequiredAttributeValue;
	private String	mAttributeName;
	
	public ExecutableExtensionPointProcessor(String attributeName, String requiredAttribute, String requiredAttributeValue)
	{
		mAttributeName = attributeName;
        mRequiredAttribute = requiredAttribute;
        mRequiredAttributeValue = requiredAttributeValue;
	}
	
    public ExecutableExtensionPointProcessor(String attributeName)
    {
        this(attributeName, null, null);
    }
    
	public Object process(IConfigurationElement element) {
		try {
            if (mRequiredAttribute != null)
            {
                String value = element.getAttribute(mRequiredAttribute);
                if (!mRequiredAttributeValue.equals(value))
                    return null;
            }
			return element.createExecutableExtension(mAttributeName);
		} catch (CoreException e) {
			return null;
		}
	}
	
}
