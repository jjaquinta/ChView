package jo.d2k.admin.rcp.viz.chview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.d2k.data.data.StarFilter;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgFilter extends GenericDialog
{
    private StarFilter  mFilter;
    
    private FilterPanel mClient;
    
    public DlgFilter(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Star Filter");
        GridUtils.makeLabel(parent, "Choose which stars to filter out:", "");
        mClient = new FilterPanel(parent, SWT.NULL);
        GridUtils.setLayoutData(mClient, "fill=hv");
        mClient.setFilter(mFilter);
        mClient.addUIPropertyChangeListener(new PropertyChangeListener() {            
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
        if (ok == null)
            return;
        ok.setEnabled(mClient.isValid());
    }
    
    @Override
    protected void okPressed()
    {
        mFilter = mClient.getFilter();
        super.okPressed();
    }

    public StarFilter getFilter()
    {
        return mFilter;
    }

    public void setFilter(StarFilter filter)
    {
        mFilter = filter;
    }
}
