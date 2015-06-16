package jo.d2k.admin.rcp.sys.ui;

import java.util.List;

import jo.d2k.admin.rcp.viz.chview.DlgRoute;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.StarRouteLogic;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.ui.act.GenericAction;
import jo.util.ui.utils.ClipboardLogic;
import jo.util.ui.utils.GridUtils;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import chuck.terran.admin.ui.RoutesViewer;

public class RoutesView extends ViewPart
{
    public static final String     ID = RoutesView.class.getName();

    private String  mMenuSelectText;
    
    private RoutesViewer mResults;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mResults = new RoutesViewer(parent, SWT.FULL_SELECTION|SWT.MULTI);
        List<StarRouteBean> routes = StarRouteLogic.getAll();
        for (StarRouteBean route : routes)
            StarRouteLogic.getReferences(route);
        mResults.setInput(routes);
        GridUtils.setLayoutData(mResults.getControl(), "fill=hv");
        mResults.addSelectionChangedListener(new ISelectionChangedListener() {            
            @Override
            public void selectionChanged(SelectionChangedEvent ev)
            {
                doResultsSelectionChanged();
            }
        });
        mResults.addDoubleClickListener(new IDoubleClickListener() {            
            @Override
            public void doubleClick(DoubleClickEvent arg0)
            {
                //doGoto();
            }
        });
        mResults.getViewer().getTable().addMenuDetectListener(new MenuDetectListener() {            
            @Override
            public void menuDetected(MenuDetectEvent ev)
            {
                Point p = new Point(ev.x, ev.y);
                doMenu(p);
            }
        });
        addActions();
        addMenu();
        
        doResultsSelectionChanged();
        ChViewVisualizationLogic.mPreferences.addUIPropertyChangeListener("data", new PropertyChangeInvoker(this, "doRefresh", mResults.getControl()));
    }

    private void addMenu()
    {
        Menu menu = new Menu(mResults.getControl());
        MenuItem copy = new MenuItem(menu, SWT.PUSH);
        copy.setText("Copy");
        copy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doCopy();
            }
        });
        MenuItem copySel = new MenuItem(menu, SWT.PUSH);
        copySel.setText("Copy Selected");
        copySel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doCopySelected();
            }
        });
        MenuItem copyAll = new MenuItem(menu, SWT.PUSH);
        copyAll.setText("Copy All");
        copyAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doCopyAll();
            }
        });
        MenuItem del = new MenuItem(menu, SWT.PUSH);
        del.setText("Delete");
        del.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDel();
            }
        });
        MenuItem edit = new MenuItem(menu, SWT.PUSH);
        edit.setText("Edit");
        edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doEdit();
            }
        });
        mResults.getControl().setMenu(menu);
    }
    
    private void addActions()
    {
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager dropDownMenu = actionBars.getMenuManager();
        IToolBarManager toolBar = actionBars.getToolBarManager();
        Action refresh = new Action("Refresh", ImageUtils.getMappedImageDescriptor("tb_refresh"))
        {
            public void run() { doRefresh(); };
        };
        dropDownMenu.add(refresh);
        toolBar.add(refresh);
        Action del = new Action("Del", ImageUtils.getMappedImageDescriptor("tb_del"))
        {
            public void run() { doDel(); };
        };
        dropDownMenu.add(del);
        toolBar.add(del);
    }
    
    private void doDel()
    {
        List<StarRouteBean> sel = mResults.getSelectedItems();
        if (sel.size() == 0)
            return;
        if (!GenericAction.openQuestion("Routes", "Delete "+sel.size()+" route(s)?"))
            return;
        ChViewVisualizationLogic.deleteRoutes(sel);
        doRefresh();
    }
    
    private void doEdit()
    {
        StarRouteBean sel = mResults.getSelectedItem();
        if (sel == null)
            return;
        DlgRoute dlg = new DlgRoute(mResults.getControl().getShell());
        dlg.setRouteStar(sel);
        if (dlg.open() != Dialog.OK)
            return;
        ChViewVisualizationLogic.makeRoute(sel);
        doRefresh();
    }
    
    public void doRefresh()
    {
        mResults.setInput(StarRouteLogic.getAll());
        mResults.refresh();
    }

    private void doResultsSelectionChanged()
    {
        //ISelection sel = mResults.getSelection();
        //mDelete.setEnabled(!sel.isEmpty());
    }
    
    private void doMenu(Point p)
    {
        int[] rc = mResults.getSelectedCell(p);
        StarRouteBean star = mResults.getInput().get(rc[0]);
        mMenuSelectText = mResults.getLabels().getColumnText(star, rc[1]);
    }

    private void doCopy()
    {
        ClipboardLogic.setAsText(mMenuSelectText);
    }

    private void doCopySelected()
    {
        copyList(mResults.getSelectedItems());
    }

    private void doCopyAll()
    {
        copyList(mResults.getInput());
    }

    private void copyList(List<StarRouteBean> routes)
    {
        StringBuffer sb = new StringBuffer();
        for (StarRouteBean route : routes)
        {
            if (sb.length() > 0)
                sb.append(System.getProperty("line.separator"));
            sb.append(ChViewRenderLogic.getStarName(route.getStar1Ref())+","+ChViewRenderLogic.getStarName(route.getStar2Ref())+","+route.getType()+","+route.getDistance());
        }
        ClipboardLogic.setAsText(sb.toString());
    }
    
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        mResults.setFocus();
    }
}
