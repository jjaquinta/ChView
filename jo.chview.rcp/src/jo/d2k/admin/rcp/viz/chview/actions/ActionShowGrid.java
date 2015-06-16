package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShowGrid extends Action
{
    public ActionShowGrid()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_grid"));
        setToolTipText("Show grid lines");
        setText("Grid");
        setChecked(ChViewVisualizationLogic.mPreferences.isShowGrid());
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.toggleGrid();
        setChecked(ChViewVisualizationLogic.mPreferences.isShowGrid());
    }
}
