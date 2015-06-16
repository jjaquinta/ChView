/*
 * Created on May 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.viewers;

import jo.util.utils.ArrayUtils;
import jo.util.utils.DebugUtils;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GenericComboViewer implements ISelectionChangedListener, ISelectionProvider
{
    private ComboViewer 			    mViewer;
    private IStructuredContentProvider	mContent;
    private ILabelProvider				mLabels;

    /**
     * The constructor.
     */
    public GenericComboViewer()
    {
    }
    
    public void init(Composite parent, int style)
    {
        mViewer = new ComboViewer(parent, style);
        mViewer.setContentProvider(mContent);
        mViewer.setLabelProvider(mLabels);
        addSelectionChangedListener(this);
    }
    
    public void setInput(Object input)
    {
        mViewer.setInput(input);
    }
    public Control getControl()
    {
        return mViewer.getControl();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        mViewer.getControl().setFocus();
    }
    
    public Object[] getSelectedItems()
    {
        ISelection selected = mViewer.getSelection();
        if (selected instanceof IStructuredSelection)
            return ((IStructuredSelection)selected).toArray();
        else
            return null;
    }
    
    public void setSelectedItems(Object[] objs)
    {
        Object[] sel = getSelectedItems();
        if (!ArrayUtils.equals(objs, sel))
        {
            ISelection selected = new StructuredSelection(objs);
            mViewer.setSelection(selected);
        }
        sel = getSelectedItems();
        if (!ArrayUtils.equals(objs, sel))
            DebugUtils.error("WFT? Why can't we set selection?");
    }
    
    public void setSelectedItem(Object obj)
    {
        Object[] objs = new Object[1];
        objs[0] = obj;
        setSelectedItems(objs);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        mViewer.addSelectionChangedListener(listener);        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection()
    {
        return mViewer.getSelection();        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        mViewer.removeSelectionChangedListener(listener);        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection)
    {
        mViewer.setSelection(selection);        
    }
    
    public void addDoubleClickListener(IDoubleClickListener listener)
    {
        mViewer.addDoubleClickListener(listener);
    }
    
    public void removeDoubleClickListener(IDoubleClickListener listener)
    {
        mViewer.removeDoubleClickListener(listener);
    }

    public IStructuredContentProvider getContent()
    {
        return mContent;
    }
    public void setContent(IStructuredContentProvider content)
    {
        mContent = content;
    }
    public ILabelProvider getLabels()
    {
        return mLabels;
    }
    public void setLabels(ILabelProvider labels)
    {
        mLabels = labels;
    }
    public ComboViewer getViewer()
    {
        return mViewer;
    }
    public void setViewer(ComboViewer viewer)
    {
        mViewer = viewer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent ev)
    {
    }
}
