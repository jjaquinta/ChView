package jo.d2k.admin.rcp.viz.chview.handlers.rep;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import jo.d2k.data.logic.imp.ImportLogic;
import jo.util.ui.act.GenericAction;
import jo.util.utils.io.FileUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class HandlerReportMerge extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        String[][] filters = ImportLogic.getFilters();
        final String dataFile = GenericAction.getOpenFile(HandlerReportMerge.class, null, filters[0], filters[1]);
        final StringBuffer html = new StringBuffer();
        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());  
        try {  
            progressDialog.run(false, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor pm) throws InvocationTargetException,
                        InterruptedException
                {
                    try
                    {
                        MergeCallback cb = new MergeCallback(pm);
                        ImportLogic.importFile(new File(dataFile), cb);
                        html.append(cb.toHTML());
                    }
                    catch (Exception e)
                    {
                        GenericAction.openError("Merge Report", "Error while reading import data", e);
                        e.printStackTrace();
                    }
                }
            }); 
        }
        catch (Exception ex)
        {
            GenericAction.openError("Merge Report", "Error while reading import data", ex);
        }
        if (html.length() == 0)
            return null;
        String output = GenericAction.getSaveFile(HandlerReportMerge.class, null, "HTML File", "*.html");
        if (output == null)
            return null;
        File outFile = new File(output);
        try
        {
            FileUtils.writeFile(html.toString(), outFile);
            Desktop.getDesktop().open(outFile);
        }
        catch (IOException e)
        {
            GenericAction.openError("Merge Report", "Error saving report", e);
        }
        return null;
    }

}
