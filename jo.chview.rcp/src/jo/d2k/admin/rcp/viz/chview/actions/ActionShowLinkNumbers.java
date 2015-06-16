package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShowLinkNumbers extends Action
{
    public ActionShowLinkNumbers()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_linknums"));
        setToolTipText("Show distance between stars");
        setText("Distances");
        setChecked(ChViewVisualizationLogic.mPreferences.isShowLinkNumbers());
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.toggleLinkNumbers();
        setChecked(ChViewVisualizationLogic.mPreferences.isShowLinkNumbers());
    }
}
