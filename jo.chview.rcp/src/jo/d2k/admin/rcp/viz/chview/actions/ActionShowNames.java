package jo.d2k.admin.rcp.viz.chview.actions;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;

public class ActionShowNames extends Action
{
    public ActionShowNames()
    {
        setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_names"));
        setToolTipText("Show star names");
        setText("Names");
        setChecked(ChViewVisualizationLogic.mPreferences.isShowNames());
    }
    
    @Override
    public void run()
    {
        ChViewVisualizationLogic.toggleNames();
        setChecked(ChViewVisualizationLogic.mPreferences.isShowNames());
    }
}
