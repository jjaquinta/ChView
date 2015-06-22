package jo.d2k.admin.rcp.viz.chview;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.logic.StarColumnLogic;
import jo.util.beans.PropChangeSupport;
import jo.util.ui.utils.GridUtils;
import jo.util.ui.utils.ImageUtils;
import jo.util.ui.viewers.GenericTreeContentProvider;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class AdvancedFilterPanel extends Composite
{
    private StarFilter  mFilter;
    private PropChangeSupport   mPCS;

    private Combo mMode;
    private TreeViewer  mTree;
    private Button mAdd;
    private Button mEdit;
    private Button mDel;
    private Button mUp;
    private Button mDown;
    private Button mIn;
    private Button mOut;
    
    public AdvancedFilterPanel(Composite parent, int style)
    {
        super(parent, style);
        mPCS = new PropChangeSupport(this);
        setLayout(new GridLayout(7, false));
        
        mMode = GridUtils.makeCombo(this, new String[] {
                "Any of the following:",
                "All of the following:"
        }, "7x1 fill=h");
        mTree = new TreeViewer(this, SWT.FULL_SELECTION);
        mTree.setContentProvider(new FilterConditionContentProvider());
        mTree.setLabelProvider(new FilterConditionLabelProvider());
        GridUtils.setLayoutData(mTree.getControl(), "7x1 fill=hv");
        mAdd = GridUtils.makeButton(this, "Add", "");
        mEdit = GridUtils.makeButton(this, "Edit", "");
        mDel = GridUtils.makeButton(this, "Del", "");
        mUp = GridUtils.makeButton(this, "Up", "");
        mDown = GridUtils.makeButton(this, "Down", "");
        mIn = GridUtils.makeButton(this, "In", "");
        mOut = GridUtils.makeButton(this, "Out", "");
        mAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doAdd();
            }
        });
        mEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doEdit();
            }
        });
        mDel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDel();
            }
        });
        mUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doUp();
            }
        });
        mDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDown();
            }
        });
        mIn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doIn();
            }
        });
        mOut.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doOut();
            }
        });
    }
    
    private void doAdd()
    {
        DlgFilterCondition dlg = new DlgFilterCondition(getShell());
        if (dlg.open() != DlgFilterCondition.OK)
            return;
        FilterConditionBean cond = dlg.getFilter();
        if (cond == null)
            return;        
        IStructuredSelection sel = (IStructuredSelection)mTree.getSelection();
        if (sel.isEmpty())
            mFilter.getConditions().add(cond);
        else
        {
            FilterConditionBean wrt = (FilterConditionBean)sel.getFirstElement();
            StarColumn col = StarColumnLogic.getColumn(wrt.getID());
            if (col.getType() == StarColumn.TYPE_PSEUDO)
            {
                @SuppressWarnings("unchecked")
                List<FilterConditionBean> arg = (List<FilterConditionBean>)wrt.getArgument();
                if (!wrt.getID().equals("NOT") || (arg.size() != 1))
                    arg.add(cond);
                else
                    add(null, mFilter.getConditions(), wrt, cond);
            }
            else
                add(null, mFilter.getConditions(), wrt, cond);
        }
        resetTree();
    }
    
    private boolean add(FilterConditionBean parent, List<FilterConditionBean> conditions,
            FilterConditionBean wrt, FilterConditionBean cond)
    {
        int idx = conditions.indexOf(wrt);
        if (idx >= 0)
        {
            if ((parent == null) || !parent.getID().equals("NOT") || (conditions.size() != 1))
                conditions.add(idx, cond);
            return true;
        }
        for (FilterConditionBean c : conditions)
        {
            StarColumn col = StarColumnLogic.getColumn(c.getID());
            if (col.getType() == StarColumn.TYPE_PSEUDO)
            {
                @SuppressWarnings("unchecked")
                List<FilterConditionBean> subConds = (List<FilterConditionBean>)c.getArgument();
                if (add(c, subConds, wrt, cond))
                    return true;
            }
        }
        return false;
    }
    
    private void doEdit()
    {
        IStructuredSelection sel = (IStructuredSelection)mTree.getSelection();
        if (sel.isEmpty())
            return;
        FilterConditionBean cond = (FilterConditionBean)sel.getFirstElement();
        DlgFilterCondition dlg = new DlgFilterCondition(getShell());
        dlg.setFilter(cond);
        if (dlg.open() != DlgFilterCondition.OK)
            return;
        resetTree();
    }

    private void doDel()
    {
        IStructuredSelection sel = (IStructuredSelection)mTree.getSelection();
        if (sel.isEmpty())
            return;
        FilterConditionBean cond = (FilterConditionBean)sel.getFirstElement();
        remove(mFilter.getConditions(), cond);
        resetTree();
    }
    
    private boolean remove(List<FilterConditionBean> conditions,
            FilterConditionBean cond)
    {
        if (conditions.contains(cond))
        {
            conditions.remove(cond);
            return true;
        }
        for (FilterConditionBean c : conditions)
        {
            StarColumn col = StarColumnLogic.getColumn(c.getID());
            if (col.getType() == StarColumn.TYPE_PSEUDO)
            {
                @SuppressWarnings("unchecked")
                List<FilterConditionBean> subConds = (List<FilterConditionBean>)c.getArgument();
                if (remove(subConds, cond))
                    return true;
            }
        }
        return false;
    }

    
    private void doUp()
    {
        IStructuredSelection sel = (IStructuredSelection)mTree.getSelection();
        if (sel.isEmpty())
            return;
        FilterConditionBean cond = (FilterConditionBean)sel.getFirstElement();
        moveUp(mFilter.getConditions(), cond);
        resetTree();
    }
    
    private boolean moveUp(List<FilterConditionBean> conditions,
            FilterConditionBean cond)
    {
        if (conditions.contains(cond))
        {
            int idx = conditions.indexOf(cond);
            if (idx >= 0)
            {
                if (idx > 0)
                {
                    conditions.remove(idx);
                    conditions.add(idx - 1, cond);
                }
                return true;
            }
        }
        for (FilterConditionBean c : conditions)
        {
            StarColumn col = StarColumnLogic.getColumn(c.getID());
            if (col.getType() == StarColumn.TYPE_PSEUDO)
            {
                @SuppressWarnings("unchecked")
                List<FilterConditionBean> subConds = (List<FilterConditionBean>)c.getArgument();
                if (moveUp(subConds, cond))
                    return true;
            }
        }
        return false;
    }

    private void doDown()
    {
        IStructuredSelection sel = (IStructuredSelection)mTree.getSelection();
        if (sel.isEmpty())
            return;
        FilterConditionBean cond = (FilterConditionBean)sel.getFirstElement();
        moveDown(mFilter.getConditions(), cond);
        resetTree();
    }
    
    private boolean moveDown(List<FilterConditionBean> conditions,
            FilterConditionBean cond)
    {
        if (conditions.contains(cond))
        {
            int idx = conditions.indexOf(cond);
            if (idx < conditions.size() - 1)
            {
                if (idx > 0)
                {
                    conditions.remove(idx);
                    conditions.add(idx + 1, cond);
                }
                return true;
            }
        }
        for (FilterConditionBean c : conditions)
        {
            StarColumn col = StarColumnLogic.getColumn(c.getID());
            if (col.getType() == StarColumn.TYPE_PSEUDO)
            {
                @SuppressWarnings("unchecked")
                List<FilterConditionBean> subConds = (List<FilterConditionBean>)c.getArgument();
                if (moveDown(subConds, cond))
                    return true;
            }
        }
        return false;
    }

    private void doIn()
    {
        IStructuredSelection sel = (IStructuredSelection)mTree.getSelection();
        if (sel.isEmpty())
            return;
        FilterConditionBean cond = (FilterConditionBean)sel.getFirstElement();
        moveIn(mFilter.getConditions(), cond);
        resetTree();
    }
    
    private boolean moveIn(List<FilterConditionBean> conditions,
            FilterConditionBean cond)
    {
        int idx = conditions.indexOf(cond);
        if (idx >= 0)
        {
            if (idx > 0)
            {
                FilterConditionBean wrt = conditions.get(idx - 1);
                StarColumn col = StarColumnLogic.getColumn(wrt.getID());
                if (col.getType() == StarColumn.TYPE_PSEUDO)
                {
                    @SuppressWarnings("unchecked")
                    List<FilterConditionBean> subConds = (List<FilterConditionBean>)wrt.getArgument();
                    if (!wrt.getID().equals("NOT") || (subConds.size() != 1))
                    {
                        conditions.remove(idx);
                        subConds.add(cond);
                    }
                }
            }
            return true;
        }
        for (FilterConditionBean c : conditions)
        {
            StarColumn col = StarColumnLogic.getColumn(c.getID());
            if (col.getType() == StarColumn.TYPE_PSEUDO)
            {
                @SuppressWarnings("unchecked")
                List<FilterConditionBean> subConds = (List<FilterConditionBean>)c.getArgument();
                if (moveDown(subConds, cond))
                    return true;
            }
        }
        return false;
    }
    
    private void doOut()
    {
        IStructuredSelection sel = (IStructuredSelection)mTree.getSelection();
        if (sel.isEmpty())
            return;
        FilterConditionBean cond = (FilterConditionBean)sel.getFirstElement();
        moveOut(mFilter.getConditions(), null, null, cond);
        resetTree();
    }

    private boolean moveOut(List<FilterConditionBean> conditions,
            List<FilterConditionBean> grandparent, FilterConditionBean parent, FilterConditionBean cond)
    {
        int idx = conditions.indexOf(cond);
        if (idx >= 0)
        {
            if (parent != null)
            {
                conditions.remove(idx);
                idx = grandparent.indexOf(parent);
                grandparent.add(idx+1, cond);
            }
            return true;
        }
        for (FilterConditionBean c : conditions)
        {
            StarColumn col = StarColumnLogic.getColumn(c.getID());
            if (col.getType() == StarColumn.TYPE_PSEUDO)
            {
                @SuppressWarnings("unchecked")
                List<FilterConditionBean> subConds = (List<FilterConditionBean>)c.getArgument();
                if (moveOut(subConds, conditions, c, cond))
                    return true;
            }
        }
        return false;
    }
    
    private void resetTree()
    {
        ((FilterConditionContentProvider)mTree.getContentProvider()).reset();
        mTree.refresh();
        mPCS.fireMonotonicPropertyChange("data", true);
    }

    public StarFilter getFilter()
    {
        mFilter.setAnd(mMode.getSelectionIndex() == 1);
        return mFilter;
    }

    public void setFilter(StarFilter filter)
    {
        mFilter = filter;
        mMode.select(filter.isAnd() ? 1 : 0);
        mTree.setInput(mFilter.getConditions());
        mPCS.fireMonotonicPropertyChange("data", true);
    }
    
    class FilterConditionLabelProvider extends LabelProvider
    {
        @Override
        public Image getImage(Object element)
        {
            FilterConditionBean cond = (FilterConditionBean)element;
            if (!FilterPanel.isValid(cond))
                return ImageUtils.getMappedImage("error16");
            return super.getImage(element);
        }
    }
    
    class FilterConditionContentProvider extends GenericTreeContentProvider
    {
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            super.inputChanged(viewer, oldInput, newInput);
            reset();
        }
        
        public void reset()
        {
            mHierarchy.clear();
            mParents.clear();
        }
        
        @Override
        protected Object[] doGetChildren(Object parent)
        {
            if (parent instanceof Collection<?>)
                return ((Collection<?>)parent).toArray();
            if (parent instanceof FilterConditionBean)
            {
                FilterConditionBean cond = (FilterConditionBean)parent;
                StarColumn col = StarColumnLogic.getColumn(cond.getID());
                if (col.getType() == StarColumn.TYPE_PSEUDO)
                    return doGetChildren(cond.getArgument());
            }
            return super.doGetChildren(parent);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(pcl);
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(prop, pcl);
    }

    public void addUIPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(pcl);
    }

    public void addUIPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(prop, pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.removePropertyChangeListener(pcl);
    }
}
