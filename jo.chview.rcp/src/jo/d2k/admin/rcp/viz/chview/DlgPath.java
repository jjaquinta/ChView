package jo.d2k.admin.rcp.viz.chview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.ui.ctrl.ImageCombo;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;
import jo.util.ui.utils.SwathUtils;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgPath extends GenericDialog implements PropertyChangeListener
{
    public static final int DISPLAY = 1001;
    
    private StarBean                mStar1;
    private StarBean                mStar2;
    
    private PathPanel              mClient;
    
    public DlgPath(Shell parentShell)
    {
        super(parentShell);
    }
    
    public DlgPath(Shell parentShell, boolean modal)
    {
        super(parentShell);
        if (!modal)
            setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Path Finder");
        mClient = new PathPanel(parent, SWT.BORDER);
        GridUtils.setLayoutData(mClient, "fill=hv");
        mClient.setStar1(mStar1);
        mClient.setStar2(mStar2);
        StarLogic.addPropertyChangeListener("data", this);
        return mClient;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createRouteButton(parent);
        //createRouteButton2(parent);
        createButton(parent, DISPLAY, "Display", true);
        createButton(parent, CANCEL, "Cancel", true);
    }
    
    private void createRouteButton(Composite parent) 
    {
        // increment the number of columns in the button bar
        ((GridLayout) parent.getLayout()).numColumns++;
        ImageCombo makeRoute = new ImageCombo(parent, SWT.BORDER|SWT.READ_ONLY);
        makeRoute.add(null, "Make Route");
        for (int i = 0; i < 8; i++)
            makeRoute.add(SwathUtils.getImage(ChViewRenderLogic.getRouteColor(i), 16, 16), ChViewRenderLogic.getRouteName(i));
        makeRoute.setFont(JFaceResources.getDialogFont());
        makeRoute.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ImageCombo makeRoute = (ImageCombo)event.widget;
                int idx = makeRoute.getSelectionIndex();
                if (idx == 0)
                    return;
                doMakeRoute(idx - 1);
                makeRoute.select(0);
            }
        });
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = makeRoute.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        makeRoute.setLayoutData(data);
        makeRoute.select(0);
        makeRoute.setEnabled(!RuntimeLogic.getInstance().getDataSource().isReadOnly());
    }
    
    /*
    private void createRouteButton2(Composite parent) 
    {
        // increment the number of columns in the button bar
        ((GridLayout) parent.getLayout()).numColumns++;
        Combo makeRoute = new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
        makeRoute.add("Make Route");
        makeRoute.add("Route 1");
        makeRoute.add("Route 2");
        makeRoute.add("Route 3");
        makeRoute.add("Route 4");
        makeRoute.add("Route 5");
        makeRoute.add("Route 6");
        makeRoute.add("Route 7");
        makeRoute.add("Route 8");
        makeRoute.setFont(JFaceResources.getDialogFont());
        makeRoute.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Combo makeRoute = (Combo)event.widget;
                int idx = makeRoute.getSelectionIndex();
                if (idx == 0)
                    return;
                doMakeRoute(idx - 1);
                makeRoute.select(0);
            }
        });
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = makeRoute.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        makeRoute.setLayoutData(data);
        makeRoute.select(0);
        makeRoute.setEnabled(!ApplicationLogic.getInstance().getDataSource().isReadOnly());
    }
    */

    private void doMakeRoute(int type)
    {
        List<StarBean> sel = mClient.getStarPath();
        if ((sel == null) || (sel.size() == 0))
            return;
        List<StarRouteBean> routes = new ArrayList<StarRouteBean>();
        for (int i = 0; i < sel.size() - 1; i++)
        {
            StarRouteBean route = new StarRouteBean();
            route.setStar1Ref(sel.get(i));
            route.setStar2Ref(sel.get(i + 1));
            route.setType(type);
            routes.add(route);
        }        
        ChViewVisualizationLogic.makeRoutes(routes);
    }
    
    @Override
    protected void buttonPressed(int buttonId)
    {
        if (buttonId == DISPLAY)
        {
            List<StarBean> sel = mClient.getStarPath();
            if ((sel != null) && (sel.size() > 0))
            {
                ChViewVisualizationLogic.showOnly(sel);
            }
        }
        else
            super.buttonPressed(buttonId);
    }

    public StarBean getStar1()
    {
        return mStar1;
    }

    public void setStar1(StarBean star1)
    {
        mStar1 = star1;
    }

    public StarBean getStar2()
    {
        return mStar2;
    }

    public void setStar2(StarBean star2)
    {
        mStar2 = star2;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        mClient.updateDistance();
        mClient.updateProposals();
        mClient.setStarPath(new ArrayList<StarBean>());
    }
}
