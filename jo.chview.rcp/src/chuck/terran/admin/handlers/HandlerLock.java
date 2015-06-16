package chuck.terran.admin.handlers;

import jo.d2k.data.logic.DataLogic;
import jo.util.ui.act.GenericAction;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class HandlerLock extends HandlerBaseReadOnly
{
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        doLock();
        return null;
    }

    public static void doLock()
    {
        if (!GenericAction.openQuestion("Lock Data Source", "Do you want to set the current data source into read-only mode?"))
            return;
        DataLogic.lockDataSource();
    }    
}
