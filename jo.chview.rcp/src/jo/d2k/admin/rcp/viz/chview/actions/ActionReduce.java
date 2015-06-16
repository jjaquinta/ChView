package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionReduce extends Action
{
    public ActionReduce()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_shrink"));
        setToolTipText("Shrink view radius");
        setText("Reduce");
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.reduce();
    }
}
