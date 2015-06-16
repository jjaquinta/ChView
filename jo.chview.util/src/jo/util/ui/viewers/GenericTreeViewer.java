/*
 * Created on May 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.viewers;

import jo.util.utils.ArrayUtils;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GenericTreeViewer implements ISelectionChangedListener, ISelectionProvider
{
    private TreeViewer 					mViewer;
    private ITreeContentProvider		mContent;
    private ILabelProvider				mLabels;

    /**
     * The constructor.
     */
    public GenericTreeViewer()
    {
    }
    
    public void init(Composite parent, int style)
    {
        mViewer = new TreeViewer(parent, style);
        mViewer.setContentProvider(mContent);
        mViewer.setLabelProvider(mLabels);
        if (addColumns())
        {
            mViewer.getTree().setHeaderVisible(true);
            mViewer.getTree().setLinesVisible(true);
        }
        addSelectionChangedListener(this);
    }

    protected boolean addColumns()
    {
        return false;
    }

    protected void addColumn(String name, int width)
    {
        TreeColumn col = new TreeColumn(mViewer.getTree(), SWT.LEFT);
        col.setText(name);
        col.setWidth(width);
    }
    
    public void setInput(Object input)
    {
        mViewer.setInput(input);
    }
    public Object getInput()
    {
        return mViewer.getInput();
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
        TreeItem[] sel = mViewer.getTree().getSelection();
        Object[] ret = new Object[sel.length];
        for (int i = 0; i < sel.length; i++)
            ret[i] = sel[i].getData();
        return ret;
    }
    
    public void setSelectedItems(Object[] objs)
    {
        if (!ArrayUtils.equals(objs, getSelectedItems()))
        {
            ISelection selected = new StructuredSelection(objs);
            mViewer.setSelection(selected);
        }
    }
    
    public Object getSelectedItem()
    {
        ISelection selected = mViewer.getSelection();
        if (selected instanceof IStructuredSelection)
            return ((IStructuredSelection)selected).getFirstElement();
        else
            return null;
    }
    
    public void setSelectedItem(Object obj)
    {
        if (obj != getSelectedItem())
        {
            ISelection selected = new StructuredSelection(obj);
            mViewer.setSelection(selected);
        }
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

    public ITreeContentProvider getContent()
    {
        return mContent;
    }
    public void setContent(ITreeContentProvider content)
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
    public TreeViewer getViewer()
    {
        return mViewer;
    }
    public void setViewer(TreeViewer viewer)
    {
        mViewer = viewer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent ev)
    {
    }
    
    public void refresh()
    {
        mViewer.refresh();
    }
    
    public void addDragSupport()
    {
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] { TextTransfer.getInstance()};
        mViewer.addDragSupport(ops, transfers, new BeanDragListener(mViewer));            
    }
}
