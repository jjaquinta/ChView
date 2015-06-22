package jo.d2k.admin.rcp.viz.chview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.logic.StarColumnLogic;
import jo.util.beans.PropChangeSupport;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class FilterPanel extends Composite
{
    private StarFilter  mFilter;
    private PropChangeSupport mPCS;

    private Composite   mClient;
    private SimpleFilterPanel   mSimple;
    private AdvancedFilterPanel   mAdvanced;
    private Button      mMode;
    
    public FilterPanel(Composite parent, int style)
    {
        super(parent, style);
        mPCS = new PropChangeSupport(this);
        setLayout(new GridLayout(1, false));
        
        mClient = GridUtils.makeComposite(this, SWT.NULL, "fill=hv");
        mClient.setLayout(new StackLayout());
        mSimple = new SimpleFilterPanel(mClient, SWT.NULL);
        mAdvanced = new AdvancedFilterPanel(mClient, SWT.NULL);
        mMode = GridUtils.makeCheck(this, "Advanced", "fill=h align=e");
        mMode.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doMode();
            }
        });
        doMode();
        mSimple.addUIPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateValidity();
            }
        });
        mAdvanced.addUIPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateValidity();
            }
        });
        updateValidity();
    }

    private void doMode()
    {
        if (mFilter != null)
            if (mMode.getSelection())
            {
                mFilter = mSimple.getFilter();
                mAdvanced.setFilter(mFilter);
            }
            else
            {
                mFilter = mAdvanced.getFilter();
                mSimple.setFilter(mFilter);
            }
        ((StackLayout)mClient.getLayout()).topControl = (mMode.getSelection() ? mAdvanced : mSimple);
        mClient.layout();
    }
    
    public StarFilter getFilter()
    {
        if (mMode.getSelection())
            mFilter = mAdvanced.getFilter();
        else
            mFilter = mSimple.getFilter();
        return mFilter;
    }

    public void setFilter(StarFilter filter)
    {
        mMode.setSelection(isAdvanced(filter));
        mFilter = filter;
        if (mMode.getSelection())
            mAdvanced.setFilter(mFilter);
        else
            mSimple.setFilter(mFilter);
        ((StackLayout)mClient.getLayout()).topControl = (mMode.getSelection() ? mAdvanced : mSimple);
        mClient.layout();
    }
    
    private boolean isAdvanced(StarFilter filter)
    {
        if (filter.isAnd())
            return true;
        for (FilterConditionBean cond : filter.getConditions())
        {
            StarColumn col = StarColumnLogic.getColumn(cond.getID());
            if (col.getType() == StarColumn.TYPE_INTRINSIC)
            {
                if (!col.getID().equals("generated") && !col.getID().equals("spectrum"))
                    return true;
            }
            else if (col.getType() == StarColumn.TYPE_EXTRA)
            {
                if (col.getComparator().getDefaultOption() != cond.getOption())
                    return true;
            }
            else
                return true;
        }
        return false;
    }

    public boolean isValid()
    {
        getFilter();
        if (mFilter == null)
            return false;
        for (FilterConditionBean cond : mFilter.getConditions())
            if (!isValid(cond))
                return false;
        return true;
    }
    
    public static boolean isValid(FilterConditionBean cond)
    {
        StarColumn col = StarColumnLogic.getColumn(cond.getID());
        if (col.getComparator().isArgFor(cond.getOption()))
            if (col.getComparator().isValidArgFor(cond.getOption(), cond.getArgument()) == null)
                return false;
        if (col.getType() == StarColumn.TYPE_PSEUDO)
        {
            if (col.getComparator().isValidArgFor(cond.getOption(), cond.getArgument()) == null)
                return false;
            @SuppressWarnings("unchecked")
            List<FilterConditionBean> subConds = (List<FilterConditionBean>)cond.getArgument();
            for (FilterConditionBean subCond : subConds)
                if (!isValid(subCond))
                    return false;
        }
        return true;
    }
    
    private void updateValidity()
    {
        mPCS.fireMonotonicPropertyChange("valid", isValid());
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
