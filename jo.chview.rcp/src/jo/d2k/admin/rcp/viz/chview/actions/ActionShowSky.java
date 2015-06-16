package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShowSky extends Action
{
    public ActionShowSky()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_sky"));
        setToolTipText("Show sky");
        setText("Sky");
        setChecked(ChViewVisualizationLogic.mPreferences.isShowSky());
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.toggleSky();
        setChecked(ChViewVisualizationLogic.mPreferences.isShowSky());
    }
}
