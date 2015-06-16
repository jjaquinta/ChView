package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShowLinks extends Action
{
    public ActionShowLinks()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_links"));
        setToolTipText("Show links between stars");
        setText("Links");
        setChecked(ChViewVisualizationLogic.mPreferences.isShowLinks());
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.toggleLinks();
        setChecked(ChViewVisualizationLogic.mPreferences.isShowLinks());
    }
}
