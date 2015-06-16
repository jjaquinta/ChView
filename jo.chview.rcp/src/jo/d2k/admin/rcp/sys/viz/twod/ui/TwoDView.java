package jo.d2k.admin.rcp.sys.viz.twod.ui;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.sys.viz.twod.actions.ActionOptimize;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class TwoDView extends ViewPart
{
    public static final String     ID = TwoDView.class.getName();

    private List<Action>    mActions;

    private TwoDPanel       mViewer;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mViewer = new TwoDPanel(parent, SWT.NULL);
        makeActions();
        addActions();
    }
    
    private void addActions()
    {
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager dropDownMenu = actionBars.getMenuManager();
        IToolBarManager toolBar = actionBars.getToolBarManager();
        for (Action action : mActions)
        {
            dropDownMenu.add(action);
            toolBar.add(action);
        }
    }

    private void makeActions()
    {
        mActions = new ArrayList<Action>();
        mActions.add(new ActionOptimize(mViewer));
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        mViewer.setFocus();
    }
    
    public TwoDDisplay getTwoDDisplay()
    {
        return mViewer.getDisp();
    }
    
    public void setTwoDDisplay(TwoDDisplay disp)
    {
        mViewer.setDisp(disp);
    }
}