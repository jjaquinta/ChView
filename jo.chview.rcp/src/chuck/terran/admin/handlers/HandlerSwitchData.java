package chuck.terran.admin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import chuck.terran.admin.ui.DlgPickDataSource;

public class HandlerSwitchData extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        DlgPickDataSource dlg = new DlgPickDataSource(Display.getDefault().getActiveShell());
        dlg.open();
        return null;
    }

}
