package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.data.data.StarRouteBean;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgRoute extends GenericDialog
{
    private StarRouteBean           mRoute;
    
    private RoutePanel              mClient;
    
    public DlgRoute(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Edit Route");
        mClient = new RoutePanel(parent, SWT.NULL);
        GridUtils.setLayoutData(mClient, "fill=hv");
        mClient.setRoute(mRoute);
        return mClient;
    }
    
    @Override
    protected void okPressed()
    {
        mRoute = mClient.getRoute();
        if (mRoute == null)
        {
            MessageDialog.openError(getShell(), "Edit Route", "Invalid Route");
            return;
        }
        super.okPressed();
    }

    public StarRouteBean getRoute()
    {
        return mRoute;
    }

    public void setRouteStar(StarRouteBean route)
    {
        mRoute = route;
    }
}
