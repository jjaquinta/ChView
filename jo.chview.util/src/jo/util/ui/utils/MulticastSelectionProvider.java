package jo.util.ui.utils;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class MulticastSelectionProvider implements ISelectionProvider, ISelectionChangedListener
{
    private ArrayList<ISelectionProvider>           mProviders;
    private ArrayList<ISelectionChangedListener>    mListeners = new ArrayList<ISelectionChangedListener>();
    
    public MulticastSelectionProvider()
    {
        mProviders = new ArrayList<ISelectionProvider>();
    }
    
    public void addProvider(ISelectionProvider provider)
    {
        provider.addSelectionChangedListener(this);
        mProviders.add(provider);
    }
    
    public void removeProvider(ISelectionProvider provider)
    {
        provider.removeSelectionChangedListener(this);
        mProviders.remove(provider);
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        mListeners.add(listener);
    }

    public ISelection getSelection()
    {
        for (Iterator<ISelectionProvider> i = mProviders.iterator(); i.hasNext(); )
        {
            ISelection sel = i.next().getSelection();
            if ((sel != null) && !sel.isEmpty())
                return sel;
        }
        return new StructuredSelection();
    }

    public void removeSelectionChangedListener(
            ISelectionChangedListener listener)
    {
        mListeners.remove(listener);
    }

    public void setSelection(ISelection selection)
    {
        for (Iterator<ISelectionProvider> i = mProviders.iterator(); i.hasNext(); )
            i.next().setSelection(selection);
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
        for (Iterator<ISelectionChangedListener> i = mListeners.iterator(); i.hasNext(); )
            i.next().selectionChanged(event);
    }
}
