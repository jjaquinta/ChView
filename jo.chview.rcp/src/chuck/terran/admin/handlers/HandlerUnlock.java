package chuck.terran.admin.handlers;

import jo.d2k.data.logic.DataLogic;
import jo.d2k.data.logic.MetadataLogic;
import jo.util.ui.act.GenericAction;
import jo.util.utils.obj.StringUtils;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class HandlerUnlock extends HandlerBaseReadOnly
{
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        doUnlock();
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return !mEnabled;
    }

    public static void doUnlock()
    {
        String pw = MetadataLogic.getValue("db.info", -1, "password");
        if (StringUtils.isTrivial(pw))
        {
            if (!GenericAction.openQuestion("Unlock Data Source", "Do you want to set the current data source into read-write mode?"))
                return;
        }
        else
        {
            for (;;)
            {
                InputDialog dlg = new InputDialog(Display.getDefault().getActiveShell(), "Unlock Data Source", "Enter in password to unlock data source", "", null)
                {
                    @Override
                    protected int getInputTextStyle()
                    {
                        return SWT.PASSWORD;
                    }
                };
                if (dlg.open() != Dialog.OK)
                    return;
                if (pw.equals(dlg.getValue()))
                    break;
            }                
        }
        DataLogic.unlockDataSource();
    }

}
