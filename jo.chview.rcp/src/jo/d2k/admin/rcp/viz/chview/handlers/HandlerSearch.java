package jo.d2k.admin.rcp.viz.chview.handlers;

import jo.d2k.admin.rcp.sys.ui.SearchView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerSearch extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        IWorkbenchPage page = window.getActivePage();
        try
        {
            page.showView(SearchView.ID, "id"+System.currentTimeMillis(), IWorkbenchPage.VIEW_ACTIVATE);
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
