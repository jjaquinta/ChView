package jo.d2k.admin.rcp.viz.chview.handlers;

import jo.d2k.admin.rcp.viz.chview.DlgFilter;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarFilter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerFilter extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        StarFilter filter = new StarFilter();
        filter.set(ChViewVisualizationLogic.mPreferences.getFilter());
        DlgFilter dlg = new DlgFilter(window.getShell());
        dlg.setFilter(filter);
        if (dlg.open() == Dialog.OK)
            ChViewVisualizationLogic.setFilter(dlg.getFilter());
        return null;
    }

}
