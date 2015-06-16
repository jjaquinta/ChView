package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShrink extends Action
{
    public ActionShrink()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_reduce"));
        setToolTipText("Reduce display scale");
        setText("Shrink");
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.shrink();
    }
}
