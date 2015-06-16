package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShowScope extends Action
{
    public ActionShowScope()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_scope"));
        setToolTipText("Show scope");
        setText("Scope");
        setChecked(ChViewVisualizationLogic.mPreferences.isShowScope());
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.toggleScope();
        setChecked(ChViewVisualizationLogic.mPreferences.isShowScope());
    }
}
