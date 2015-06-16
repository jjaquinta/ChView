package chuck.terran.admin.handlers;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import jo.util.ui.act.GenericAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import chuck.terran.admin.logic.UpdateLogic;

public class HandlerUpdate extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        doUpdate();
        return null;
    }

    public static void doUpdate()
    {
        final Shell window = Display.getDefault().getActiveShell();
        final List<URL> updates = UpdateLogic.checkForUpdates();
        if (updates.size() == 0)
        {
            MessageDialog.openInformation(window, "Update Software", "Your software is up to date.");
            return;
        }
        if (!GenericAction.openQuestion(window, "Update Software", "Updates are available, do you want to install them now?"))
            return;
        final boolean[] success = new boolean[] { false };
        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(window);  
        try {  
            progressDialog.run(false, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor pm) throws InvocationTargetException,
                        InterruptedException
                {
                    try
                    {
                        success[0] = UpdateLogic.performUpdates(updates);
                    }
                    catch (Exception e)
                    {
                        GenericAction.openError(window, "Update", "Error while updating", e);
                        e.printStackTrace();
                    }
                }
            }); 
            if (!success[0])
            {
                MessageDialog.openInformation(window, "Update Software", "There was a problem performing updates.");
                return;
            }
            if (!GenericAction.openQuestion(window, "Update Software", "Updates complete. Would you like to restart?"))
                return;
            PlatformUI.getWorkbench().restart();
        }
        catch (Exception ex)
        {
            GenericAction.openError(window, "Update", "Error while updating", ex);
        }

    }

}
