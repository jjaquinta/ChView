package jo.d2k.admin.rcp.viz.chview.handlers.rep;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.logic.report.MassWarpReport;
import jo.util.ui.act.GenericAction;
import jo.util.utils.io.FileUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class HandlerReportMassWarp extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        String output = GenericAction.getSaveFile(HandlerReportMassWarp.class, null, "HTML File", "*.html");
        if (output == null)
            return null;
        File outFile = new File(output);
        String html = MassWarpReport.generateReport(ChViewVisualizationLogic.mPreferences);
        try
        {
            FileUtils.writeFile(html, outFile);
            Desktop.getDesktop().open(outFile);
        }
        catch (IOException e)
        {
            GenericAction.openError("Mass Warp", "Error saving report", e);
        }
        return null;
    }

}
