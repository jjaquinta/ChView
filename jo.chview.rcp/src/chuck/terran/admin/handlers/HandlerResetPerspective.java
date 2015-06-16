package chuck.terran.admin.handlers;

import jo.util.ui.act.GenericAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerResetPerspective extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        if (GenericAction.openQuestion("Reset Application", "Do you want to restore the program's windows to their defaults?"))
        {
            IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
            window.getActivePage().resetPerspective();
        }
        return null;
    }

}
