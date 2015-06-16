package jo.d2k.admin.rcp.viz.sky;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.admin.rcp.viz.sky.actions.ActionShowBorder;
import jo.d2k.admin.rcp.viz.sky.actions.ActionShowConstellations;
import jo.d2k.admin.rcp.viz.sky.actions.ActionShowGrid;
import jo.d2k.data.data.SkyBean;
import jo.d2k.data.data.SkyConstellationBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.SkyLogic;
import jo.util.geom3d.Point3D;
import jo.util.logic.ThreadLogic;
import jo.util.ui.utils.ImageUtils;
import jo.util.utils.FormatUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class SkyView extends ViewPart
{
    public static final String     ID = SkyView.class.getName();

    private List<Action>            mActions;
    private List<SkyBean>           mSky;
    private List<SkyConstellationBean> mConstellations;

    private ChViewSkyPanel           mClient;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mClient = new ChViewSkyPanel(parent, SWT.NULL);
        makeActions();
        addActions();
        doRefresh();
        mClient.addUIPropertyChangeListener("hover", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doHover();
            }
        });
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
        mActions.add(new Action("Refresh", ImageUtils.getMappedImageDescriptor("tb_refresh")) {
            @Override
            public void run()
            {
                doRefresh();
            }
        });
        mActions.add(new Action("Zoom In", ImageUtils.getMappedImageDescriptor("tb_enlarge")) {
            @Override
            public void run()
            {
                mClient.doMouseScroll(1);
            }
        });
        mActions.add(new Action("Zoom Out", ImageUtils.getMappedImageDescriptor("tb_reduce")) {
            @Override
            public void run()
            {
                mClient.doMouseScroll(-1);
            }
        });
        mActions.add(new ActionShowBorder(mClient));
        mActions.add(new ActionShowGrid(mClient));
        mActions.add(new ActionShowConstellations(mClient));
    }
    
    private void doRefresh()
    {
        mClient.setSky(null);
        ThreadLogic.runOnBackgroundThread(new Thread("Sky Refresh") { public void run() { doRefreshBG(); } });
    }
    
    private void doRefreshBG()
    {
        mSky = SkyLogic.getSky(ChViewVisualizationLogic.mPreferences.getCenter());
        Collections.sort(mSky, new Comparator<SkyBean>() {
            @Override
            public int compare(SkyBean o1, SkyBean o2)
            {
                return (int)Math.signum(o1.getApparentMagnitude() - o2.getApparentMagnitude());
            }
        });
        mConstellations = SkyLogic.getConstellations(mSky);
        ThreadLogic.runOnUIThread(new Thread("Sky Refresh") { public void run() { doRefreshFG(); } });
    }
    
    private void doRefreshFG()
    {
        mClient.setSky(mSky);
        mClient.setConstellations(mConstellations);
        Point3D center = ChViewVisualizationLogic.mPreferences.getCenter();
        mClient.setCenterPoint(center);
        mClient.setCenterStar(null);
        if (ChViewVisualizationLogic.mPreferences.getStars() != null)
            for (StarBean star : ChViewVisualizationLogic.mPreferences.getStars())
                if ((star.getX() == center.x) && (star.getY() == center.y) && (star.getZ() == center.z))
                {
                    mClient.setCenterStar(star);
                    break;
                }
    }

    private void doHover()
    {
        SkyBean star = mClient.getHover();
        String txt = "";
        if (star != null)
        {
            txt = star.getStar().getName();
            txt += ", "+star.getStar().getSpectra();
            txt += ", "+FormatUtils.formatDouble(star.getApparentMagnitude(), 1)+" magnitude";
            txt += " ("+FormatUtils.formatDouble(star.getStar().getAbsMag(), 1)+")";
        }
        getViewSite().getActionBars().getStatusLineManager().setMessage(txt);
    }
    
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        mClient.setFocus();
    }
}