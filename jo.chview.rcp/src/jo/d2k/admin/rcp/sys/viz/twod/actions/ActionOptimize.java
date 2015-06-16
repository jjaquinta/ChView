package jo.d2k.admin.rcp.sys.viz.twod.actions;

import jo.d2k.admin.rcp.sys.viz.twod.logic.TwoDIdealLogic;
import jo.d2k.admin.rcp.sys.viz.twod.ui.TwoDPanel;

import org.eclipse.jface.action.Action;

public class ActionOptimize extends Action
{
    private TwoDPanel mViewer;
    
    public ActionOptimize(TwoDPanel viewer)
    {
        mViewer = viewer;
        setToolTipText("Optimize arrangement");
        setText("\u03a9");
    }
    
    @Override
    public void run()
    {
        TwoDIdealLogic.findIdeal(mViewer.getDisp(), 2000);
    }
}
