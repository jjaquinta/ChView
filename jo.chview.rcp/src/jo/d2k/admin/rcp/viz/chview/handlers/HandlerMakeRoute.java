package jo.d2k.admin.rcp.viz.chview.handlers;

import java.util.Iterator;

import jo.d2k.admin.rcp.viz.chview.DlgRoute;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import chuck.terran.admin.handlers.HandlerBaseReadOnly;

public class HandlerMakeRoute extends HandlerBaseReadOnly
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        makeRoute(window.getShell());
        return null;
    }

    public static void makeRoute(Shell shell)
    {
        DlgRoute dlg = new DlgRoute(shell);
        StarRouteBean route = new StarRouteBean();
        Iterator<StarBean> i = ChViewVisualizationLogic.mPreferences.getSelected().iterator();
        if (i.hasNext())
        {
            route.setStar1Ref(i.next());
            if (i.hasNext())
                route.setStar2Ref(i.next());
            else if (ChViewVisualizationLogic.mPreferences.getFocus() != null)
                route.setStar2Ref(ChViewVisualizationLogic.mPreferences.getFocus());
        }
        else if (ChViewVisualizationLogic.mPreferences.getFocus() != null)
            route.setStar1Ref(ChViewVisualizationLogic.mPreferences.getFocus());
        dlg.setRouteStar(route);
        if (dlg.open() != Dialog.OK)
            return;
        route = dlg.getRoute();
        ChViewVisualizationLogic.makeRoute(route);
    }
}
