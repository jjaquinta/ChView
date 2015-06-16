package jo.util.ui.utils;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionProviderImpl implements ISelectionProvider
{
    private ArrayList<ISelectionChangedListener>    mListeners = new ArrayList<ISelectionChangedListener>();
    private ISelection                              mSelection = null;

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        mListeners.add(listener);
        if (mSelection != null)
            listener.selectionChanged(new SelectionChangedEvent(this, mSelection));
    }

    public ISelection getSelection()
    {
        return mSelection;
    }

    public void removeSelectionChangedListener(
            ISelectionChangedListener listener)
    {
        mListeners.remove(listener);
    }

    public void setSelection(ISelection selection)
    {
        mSelection = selection;
        fireSelection();
    }
    
    public void fireSelection()
    {
        Object[] listeners = mListeners.toArray();
        SelectionChangedEvent event = new SelectionChangedEvent(this, mSelection);
        for (int i = 0; i < listeners.length; i++)
            ((ISelectionChangedListener)listeners[i]).selectionChanged(event);
    }

    public void setSelection(Object o)
    {
        if (o == null)
            setSelection(new StructuredSelection());
        else
            setSelection(new StructuredSelection(o));
    }
}
