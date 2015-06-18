package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.logic.StarColumnLogic;
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

    private Composite   mClient;
    private SimpleFilterPanel   mSimple;
    private AdvancedFilterPanel   mAdvanced;
    private Button      mMode;
    
    public FilterPanel(Composite parent, int style)
    {
        super(parent, style);
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
}
