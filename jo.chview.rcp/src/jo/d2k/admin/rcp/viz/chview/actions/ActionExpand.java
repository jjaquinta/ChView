package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionExpand extends Action
{
    public ActionExpand()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_enlarge"));
        setToolTipText("Enlarge display scale");
        setText("Expand");
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.expand();
    }
}
