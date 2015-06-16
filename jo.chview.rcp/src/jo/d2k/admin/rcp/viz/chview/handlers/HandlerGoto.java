package jo.d2k.admin.rcp.viz.chview.handlers;

import jo.d2k.admin.rcp.viz.chview.DlgGoto;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerGoto extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        doGoto(window.getShell());
        return null;
    }

    public static void doGoto(Shell shell)
    {
        DlgGoto dlg = new DlgGoto(shell);
        dlg.setCenter(ChViewVisualizationLogic.mPreferences.getCenter());
        dlg.setRadius(ChViewVisualizationLogic.mPreferences.getRadius());
        if (dlg.open() != Dialog.OK)
            return;
        ChViewVisualizationLogic.setCenter(dlg.getCenter());
        ChViewVisualizationLogic.setRadius(dlg.getRadius());
    }

}
