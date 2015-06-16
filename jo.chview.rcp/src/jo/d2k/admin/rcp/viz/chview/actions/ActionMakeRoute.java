package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.handlers.HandlerMakeRoute;
import jo.d2k.data.logic.IDataSource;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.beans.PropertyChangeInvoker;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

public class ActionMakeRoute extends Action
{
    public ActionMakeRoute()
    {
        setToolTipText("Link stars with a route");
        setText("Route");
        RuntimeLogic.getInstance().addPropertyChangeListener("dataSource", new PropertyChangeInvoker(this, "updateEnablement"));
        updateEnablement();
    }
    
    @Override
    public void run()
    {
        HandlerMakeRoute.makeRoute(Display.getDefault().getActiveShell());
    }

    public void updateEnablement()
    {
        IDataSource ds = RuntimeLogic.getInstance().getDataSource();
        if (ds == null)
            return;
        setEnabled(!ds.isReadOnly());
    }
}
