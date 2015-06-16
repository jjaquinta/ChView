/*
 * Created on May 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.viewers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.util.beans.PCSBean;
import jo.util.logic.ThreadLogic;
import jo.util.utils.ArrayUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GenericTableTreeViewer implements ISelectionChangedListener, ISelectionProvider, PropertyChangeListener
{
	public static final int SORT_BY_TEXT = 0;
	public static final int SORT_BY_NUMBER = 1;
	
    private TreeViewer 				    mViewer;
    private IStructuredContentProvider	mContent;
    private ITableLabelProvider			mLabels;
    private PCSBean                     mRefreshObject;
    private String                      mRefreshProperty;

    /**
     * The constructor.
     */
    public GenericTableTreeViewer()
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
    
    protected TreeColumn addColumn(String name, int width)
    {
        TreeColumn col = new TreeColumn(mViewer.getTree(), SWT.LEFT);
        col.setText(name);
        col.setWidth(width);
        return col;
    }
    
    protected TreeColumn addSortedColumn(String name, int width)
    {
    	return addSortedColumn(name, width, SORT_BY_TEXT, SWT.UP, true);
    }
    protected TreeColumn addSortedColumn(String name, int width, int type)
    {
    	return addSortedColumn(name, width, type, SWT.UP, true);
    }
    protected TreeColumn addSortedColumn(String name, int width, int type, int initialDirection)
    {
    	return addSortedColumn(name, width, type, initialDirection, true);
    }
    protected TreeColumn addSortedColumn(String name, int width, int type, int initialDirection, boolean keepDirection)
    {
        TreeColumn col = new TreeColumn(mViewer.getTree(), SWT.LEFT);
        col.setText(name);
        col.setWidth(width);
        int idx = mViewer.getTree().indexOf(col);
        AbstractInvertableTableSorter sorter;
        if (type == SORT_BY_TEXT)
        	sorter = new TextSorter(idx);
        else if (type == SORT_BY_NUMBER)
        	sorter = new NumberSorter(idx);
        else
        	return col;
        new TableTreeSortSelectionListener(mViewer, col, sorter, initialDirection, keepDirection);
    	return col;
    }
    
    public void setInput(Object input)
    {
        disengage();
        mViewer.setInput(input);
        if ((input != null) && (input instanceof PCSBean))
            mRefreshObject = (PCSBean)input;
        else
            mRefreshObject = null;
        engage();
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
        ISelection selected = mViewer.getSelection();
        if (selected instanceof IStructuredSelection)
            return ((IStructuredSelection)selected).toArray();
        else
            return null;
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
    
    public int getSelectedIndex()
    {
        Object sel = getSelectedItem();
        Object root = mViewer.getInput();
        Object[] children = ((IStructuredContentProvider)mViewer.getContentProvider()).getElements(root);
        return ArrayUtils.indexOf(children, sel);
    }
    
    public void setSelectedIndex(int idx)
    {
        Object root = mViewer.getInput();
        Object[] children = ((IStructuredContentProvider)mViewer.getContentProvider()).getElements(root);
        if (idx < children.length)
            setSelectedItem(children[idx]);
    }
    
    public void setSelectedItem(Object obj)
    {
        if (obj != getSelectedItem())
        {
            ISelection selected;
            if (obj != null)
            	selected = new StructuredSelection(obj);
            else
            	selected = new StructuredSelection();
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

    public IStructuredContentProvider getContent()
    {
        return mContent;
    }
    public void setContent(IStructuredContentProvider content)
    {
        mContent = content;
        if (mViewer != null)
            mViewer.setContentProvider(content);
    }
    public ITableLabelProvider getLabels()
    {
        return mLabels;
    }
    public void setLabels(ITableLabelProvider labels)
    {
        mLabels = labels;
        if (mViewer != null)
            mViewer.setLabelProvider(labels);
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

    public String getRefreshProperty()
    {
        return mRefreshProperty;
    }

    public void setRefreshProperty(String refreshProperty)
    {
        disengage();
        mRefreshProperty = refreshProperty;
        engage();
    }

    /**
     * 
     */
    private void engage()
    {
        if ((mRefreshObject != null) && (mRefreshProperty != null))
        {
            mRefreshObject.addPropertyChangeListener(mRefreshProperty, this);
        }
    }

    /**
     * 
     */
    private void disengage()
    {
        if ((mRefreshObject != null) && (mRefreshProperty != null))
        {
            mRefreshObject.removePropertyChangeListener(this);
        }
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        if (StringUtils.isTrivial(mRefreshProperty))
            return;
        if (evt == null)
            return;
        if (evt.getPropertyName().equals(mRefreshProperty))
        {
            ThreadLogic.runOnUIThread(new Thread() { public void run() {if (mViewer != null) mViewer.refresh();} });
        }
    }
    
    public void refresh()
    {
    	if (!mViewer.getControl().isDisposed())
    		mViewer.refresh();
    }
    
    public void addDragSupport()
    {
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] { TextTransfer.getInstance()};
        mViewer.addDragSupport(ops, transfers, new BeanDragListener(mViewer));            
    }
    
    private class TextSorter extends AbstractInvertableTableSorter
    {
    	private int			mIndex;
    	
    	public TextSorter(int idx)
    	{
    		mIndex = idx;
    	}
    	
		public int compare(Viewer viewer, Object e1, Object e2) {
			ITableLabelProvider labels = (ITableLabelProvider)((TableViewer)viewer).getLabelProvider(); 
			String s1 = labels.getColumnText(e1, mIndex);
			String s2 = labels.getColumnText(e2, mIndex);
			return s1.compareTo(s2);
		}
    }
    
    private class NumberSorter extends AbstractInvertableTableSorter
    {
    	private int			mIndex;
    	
    	public NumberSorter(int idx)
    	{
    		mIndex = idx;
    	}
    	
		public int compare(Viewer viewer, Object e1, Object e2) {
			ITableLabelProvider labels = (ITableLabelProvider)((TableViewer)viewer).getLabelProvider(); 
			String s1 = labels.getColumnText(e1, mIndex);
			String s2 = labels.getColumnText(e2, mIndex);
			double d1 = DoubleUtils.parseDouble(s1);
			double d2 = DoubleUtils.parseDouble(s2);
			DebugUtils.trace("Comparing "+s1+"<>"+s2+" as "+d1+"<>"+d2);
			if (d1 > d2)
				return 1;
			else if (d2 > d1)
				return -1;
			else
				return 0;
		}
    }
}
