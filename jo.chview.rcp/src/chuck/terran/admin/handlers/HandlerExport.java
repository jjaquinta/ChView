package chuck.terran.admin.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import jo.d2k.data.logic.DataLogic;
import jo.util.ui.act.GenericAction;
import jo.util.utils.ProgMonWrapper;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class HandlerExport extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        doExport();
        return null;
    }

    public static void doExport()
    {
        final String zipFile = GenericAction.getSaveFile(HandlerExport.class, null, "ZIP File", "*.zip");
        if (zipFile == null)
            return;
        final StringBuffer report = new StringBuffer();
        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        try {  
            progressDialog.run(false, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor pm) throws InvocationTargetException,
                        InterruptedException
                {
                    try
                    {
                        report.append(DataLogic.exportData(new File(zipFile), new ProgMonWrapper(pm)));
                    }
                    catch (Exception e)
                    {
                        GenericAction.openError("Export", "Error while exporting data", e);
                        e.printStackTrace();
                    }
                }
            }); 
            if (report.length() > 0)
                MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Export Data", "Export complete. Exported "+report);
        }
        catch (Exception ex)
        {
            GenericAction.openError("Export", "Error while exporting data", ex);
        }
    }

}
