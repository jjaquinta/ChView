package jo.util.utils;

import org.eclipse.core.runtime.IConfigurationElement;

public interface IExtensionPointProcessor {
	public Object process(IConfigurationElement element);
}
