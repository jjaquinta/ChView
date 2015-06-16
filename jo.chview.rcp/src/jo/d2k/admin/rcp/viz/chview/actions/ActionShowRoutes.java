package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShowRoutes extends Action
{
    public ActionShowRoutes()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_routes"));
        setToolTipText("Show routes between stars");
        setText("Routes");
        setChecked(ChViewVisualizationLogic.mPreferences.isShowRoutes());
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.toggleRoutes();
        setChecked(ChViewVisualizationLogic.mPreferences.isShowRoutes());
    }
}
