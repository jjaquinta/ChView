package chuck.terran.admin.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import jo.d2k.data.logic.DataLogic;
import jo.util.logic.ThreadLogic;
import jo.util.ui.act.GenericAction;
import jo.util.utils.ProgMonWrapper;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import chuck.terran.admin.ui.DlgImportData;

public class HandlerImport extends HandlerBaseReadOnly
{
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        doImport();
        return null;
    }

    public static void doImport()
    {
        DlgImportData dlg = new DlgImportData(Display.getDefault().getActiveShell());
        if (dlg.open() != Dialog.OK)
            return;
        final String dataFile = dlg.getFile();
        if (dataFile == null)
            return;
        final boolean merge = dlg.isMerge();
        final StringBuffer report = new StringBuffer();
        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());  
        try {  
            progressDialog.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor pm) throws InvocationTargetException,
                        InterruptedException
                {
                    try
                    {
                        report.append(DataLogic.importData(new File(dataFile), merge, new ProgMonWrapper(pm)));
                    }
                    catch (final Exception e)
                    {
                        Thread t = new Thread() { public void run() { GenericAction.openError("Import", "Error while importing data", e); }};
                        ThreadLogic.runOnUIThread(t);
                        e.printStackTrace();
                    }
                }
            }); 
            if (report.length() > 0)
                MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Import Data", "Import complete. Imported "+report);
        }
        catch (Exception ex)
        {
            GenericAction.openError("Import", "Error while importing data", ex);
        }
    }

}
