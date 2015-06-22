package jo.d2k.admin.rcp.viz.chview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.d2k.data.data.FilterConditionBean;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgFilterCondition extends GenericDialog
{
    private FilterConditionBean mCondition;
    
    private FilterConditionPanel mClient;
    
    public DlgFilterCondition(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Star Filter");
        GridUtils.makeLabel(parent, "Choose which stars to filter out:", "");
        mClient = new FilterConditionPanel(parent, SWT.NULL);
        GridUtils.setLayoutData(mClient, "fill=hv");
        mClient.setCondition(mCondition);
        mClient.addUIPropertyChangeListener("valid", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateEnablement();
            }
        });
        updateEnablement();
        return mClient;
    }
    
    private void updateEnablement()
    {
        Button ok = getButton(OK); 
        if (ok != null)
            ok.setEnabled(mClient.isValid());
    }
    
    @Override
    protected void okPressed()
    {
        mCondition = mClient.getCondition();
        super.okPressed();
    }

    public FilterConditionBean getFilter()
    {
        return mCondition;
    }

    public void setFilter(FilterConditionBean filter)
    {
        mCondition = filter;
    }
}
