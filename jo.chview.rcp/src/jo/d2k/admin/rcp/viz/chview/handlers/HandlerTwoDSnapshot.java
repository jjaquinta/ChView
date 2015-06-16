package jo.d2k.admin.rcp.viz.chview.handlers;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.ui.TwoDView;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerTwoDSnapshot extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        IWorkbenchPage page = window.getActivePage();
        TwoDDisplay disp = ChViewRenderLogic.takeTwoDSnapshot();
        try
        {
            IViewPart view = page.showView(TwoDView.ID, "id"+System.currentTimeMillis(), IWorkbenchPage.VIEW_ACTIVATE);
            ((TwoDView)view).setTwoDDisplay(disp);
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
