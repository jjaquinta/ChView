package jo.d2k.admin.rcp.sys.ui;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.ChViewStarsPanel;
import jo.d2k.admin.rcp.viz.chview.actions.ActionEnlarge;
import jo.d2k.admin.rcp.viz.chview.actions.ActionExpand;
import jo.d2k.admin.rcp.viz.chview.actions.ActionGoto;
import jo.d2k.admin.rcp.viz.chview.actions.ActionMakeRoute;
import jo.d2k.admin.rcp.viz.chview.actions.ActionReduce;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShowGrid;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShowLinkNumbers;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShowLinks;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShowNames;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShowRoutes;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShowScope;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShowSky;
import jo.d2k.admin.rcp.viz.chview.actions.ActionShrink;
import jo.d2k.admin.rcp.viz.chview.actions.ActionThemes;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.ui.utils.ImageUtils;
import jo.util.utils.FormatUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class StarsView extends ViewPart
{
    public static final String     ID = StarsView.class.getName();

    private List<Action>           mActions;

    private ChViewStarsPanel           mViewer;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mViewer = new ChViewStarsPanel(parent, SWT.NULL);
        makeActions();
        addActions();
        getViewSite().setSelectionProvider(ChViewVisualizationLogic.mPreferences);
        ChViewVisualizationLogic.mPreferences.addUIPropertyChangeListener("focus", new PropertyChangeInvoker(this, "doFocusChanged", mViewer));
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
        //ChViewStatus status = new ChViewStatus();
        //actionBars.getStatusLineManager().add(status);
    }

    private void makeActions()
    {
        mActions = new ArrayList<Action>();
        Action refresh = new Action("Refresh", ImageUtils.getMappedImageDescriptor("tb_refresh")) {
            @Override
            public void run()
            {
                doRefresh();
            }
        };
        mActions.add(refresh);
        mActions.add(new ActionEnlarge());
        mActions.add(new ActionReduce());
        mActions.add(new ActionExpand());
        mActions.add(new ActionShrink());
        mActions.add(new ActionShowNames());
        mActions.add(new ActionShowLinks());
        mActions.add(new ActionShowLinkNumbers());
        mActions.add(new ActionShowRoutes());
        mActions.add(new ActionShowScope());
        mActions.add(new ActionShowGrid());
        mActions.add(new ActionShowSky());
        mActions.add(new ActionGoto());
        mActions.add(new ActionMakeRoute());
        mActions.add(new ActionThemes());
    }
    
    public void doFocusChanged()
    {
        StarBean star = ChViewVisualizationLogic.mPreferences.getFocus();
        if (star == null)
            getViewSite().getActionBars().getStatusLineManager().setMessage("");
        else
        {
            String msg = ChViewRenderLogic.getStarName(star)
                    +" "+FormatUtils.formatDouble(star.getX(), 1)
                    +","+FormatUtils.formatDouble(star.getY(), 1)
                    +","+FormatUtils.formatDouble(star.getZ(), 1);
            getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
        }
    }
    
    private void doRefresh()
    {
        mViewer.updateData();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        mViewer.setFocus();
    }
}