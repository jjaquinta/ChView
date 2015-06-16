package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.DlgGoto;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

public class ActionGoto extends Action
{
    public ActionGoto()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_goto"));
        setToolTipText("Go to coordinate");
        setText("Goto...");
    }
    
    @Override
    public void run()
    {
        DlgGoto dlg = new DlgGoto(Display.getDefault().getActiveShell());
        dlg.setCenter(ChViewVisualizationLogic.mPreferences.getCenter());
        dlg.open();
        ChViewVisualizationLogic.setCenter(dlg.getCenter());
    }
}
