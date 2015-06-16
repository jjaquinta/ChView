/*
 * Created on May 16, 2005
 *
 */
package jo.util.ui.viewers;

import java.util.ArrayList;
import java.util.List;

import jo.util.utils.ArrayUtils;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Jo
 *
 */
public class MultipleStructuredContentProvider implements
        IStructuredContentProvider
{
    private List<IStructuredContentProvider>	mProviders;
    
    public MultipleStructuredContentProvider()
    {
        mProviders = new ArrayList<IStructuredContentProvider>();
    }
    
    public MultipleStructuredContentProvider(IStructuredContentProvider prov1, IStructuredContentProvider prov2)
    {
        this();
        addProvider(prov1);
        addProvider(prov2);
    }
    
    public void addProvider(IStructuredContentProvider provider)
    {
        mProviders.add(provider);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {
        List<Object> ret = new ArrayList<Object>();
        for (IStructuredContentProvider prov : mProviders)
            ArrayUtils.addAll(ret, prov.getElements(inputElement));
        return ret.toArray();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        for (IStructuredContentProvider prov : mProviders)
            prov.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        for (IStructuredContentProvider prov : mProviders)
            prov.inputChanged(viewer, oldInput, newInput);
    }

}
