package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionEnlarge extends Action
{
    public ActionEnlarge()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_expand"));
        setToolTipText("Expand view radius");
        setText("Enlarge");
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.enlarge();
    }
}
