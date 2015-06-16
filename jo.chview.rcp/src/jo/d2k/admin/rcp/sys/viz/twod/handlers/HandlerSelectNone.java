package jo.d2k.admin.rcp.sys.viz.twod.handlers;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.logic.TwoDDataLogic;
import jo.d2k.admin.rcp.sys.viz.twod.ui.TwoDView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerSelectNone extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        IWorkbenchPage page = window.getActivePage();
        IWorkbenchPart part = page.getActivePart();
        TwoDView view = (TwoDView)part;
        TwoDDisplay disp = view.getTwoDDisplay();
        TwoDDataLogic.selectNone(disp);
        return null;
    }

}
