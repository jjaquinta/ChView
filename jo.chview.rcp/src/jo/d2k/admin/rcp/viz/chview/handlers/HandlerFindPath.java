package jo.d2k.admin.rcp.viz.chview.handlers;

import java.util.Iterator;

import jo.d2k.admin.rcp.viz.chview.DlgPath;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerFindPath extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        DlgPath dlg = new DlgPath(window.getShell(), false);
        Iterator<StarBean> i = ChViewVisualizationLogic.mPreferences.getSelected().iterator();
        if (i.hasNext())
        {
            dlg.setStar1(i.next());
            if (i.hasNext())
                dlg.setStar2(i.next());
            else if (ChViewVisualizationLogic.mPreferences.getFocus() != null)
                dlg.setStar2(ChViewVisualizationLogic.mPreferences.getFocus());
        }
        else if (ChViewVisualizationLogic.mPreferences.getFocus() != null)
            dlg.setStar1(ChViewVisualizationLogic.mPreferences.getFocus());
        dlg.setBlockOnOpen(false);
        if (dlg.open() != Dialog.OK)
            return null;
        return null;
    }

}
