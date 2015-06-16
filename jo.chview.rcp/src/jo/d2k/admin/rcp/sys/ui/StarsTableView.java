package jo.d2k.admin.rcp.sys.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarExtraLogic;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.logic.ThreadLogic;
import jo.util.ui.act.GenericAction;
import jo.util.ui.utils.ClipboardLogic;
import jo.util.ui.utils.GridUtils;
import jo.util.ui.utils.ImageUtils;
import jo.util.utils.FormatUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

import chuck.terran.admin.ui.DlgPickStarColumns;
import chuck.terran.admin.ui.StarsViewer;

public class StarsTableView extends ViewPart implements PropertyChangeListener
{
    public static final String     ID = StarsTableView.class.getName();

    private String  mMenuSelectText;
    
    private StarsViewer mResults;
    
    private MenuItem mMenuDel;
    private MenuItem mMenuEdit;
    private Action mActionDel;
    private Action mActionEdit;
    
    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mResults = new StarsViewer(parent, SWT.FULL_SELECTION|SWT.MULTI);
        mResults.setInput(ChViewVisualizationLogic.mPreferences.getStars());
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
                doGoto();
            }
        });
        mResults.getViewer().getTree().addMenuDetectListener(new MenuDetectListener() {            
            @Override
            public void menuDetected(MenuDetectEvent ev)
            {
                Point p = new Point(ev.x, ev.y);
                doMenu(p);
            }
        });
        addActions();
        addMenu();
        updateEnablement();
        
        doResultsSelectionChanged();
        ChViewVisualizationLogic.mPreferences.addUIPropertyChangeListener(this);
        mResults.getControl().addDisposeListener(new DisposeListener() {            
            @Override
            public void widgetDisposed(DisposeEvent ev)
            {
                ChViewVisualizationLogic.mPreferences.removePropertyChangeListener(StarsTableView.this);
            }
        });
        RuntimeLogic.getInstance().addUIPropertyChangeListener("dataSource", new PropertyChangeInvoker(this, "updateEnablement", mResults.getControl()));
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
        mMenuDel = new MenuItem(menu, SWT.PUSH);
        mMenuDel.setText("Delete");
        mMenuDel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDel();
            }
        });
        mMenuEdit = new MenuItem(menu, SWT.PUSH);
        mMenuEdit.setText("Edit");
        mMenuEdit.addSelectionListener(new SelectionAdapter() {
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
        mActionEdit = new Action("Edit", ImageUtils.getMappedImageDescriptor("tb_edit"))
        {
            public void run() { doEdit(); };
        };
        dropDownMenu.add(mActionEdit);
        toolBar.add(mActionEdit);
        mActionDel = new Action("Del", ImageUtils.getMappedImageDescriptor("tb_del"))
        {
            public void run() { doDel(); };
        };
        dropDownMenu.add(mActionDel);
        toolBar.add(mActionDel);
        Action cols = new Action("Columns", ImageUtils.getMappedImageDescriptor("tb_columns"))
        {
            public void run() { doCols(); };
        };
        dropDownMenu.add(cols);
        toolBar.add(cols);
    }
    
    private void doCols()
    {
        DlgPickStarColumns dlg = new DlgPickStarColumns(mResults.getControl().getShell());
        dlg.setColumns(mResults.getColumns());
        if (dlg.open() != Dialog.OK)
            return;
        mResults.setColumns(dlg.getColumns());
    }
    
    private void doDel()
    {
        List<StarBean> sel = mResults.getSelectedItems();
        if (sel.size() == 0)
            return;
        if (!GenericAction.openQuestion("Routes", "Delete "+sel.size()+" stars(s)?"))
            return;
        ChViewVisualizationLogic.deleteStars(sel);
        doRefresh();
    }
    
    private void doGoto()
    {
        List<StarBean> sel = mResults.getSelectedItems();
        if (sel.size() == 0)
            return;
        StarBean star = sel.get(0);
        ChViewVisualizationLogic.setCenter(StarExtraLogic.getLocation(star));
        doRefresh();
    }
    
    private void doEdit()
    {
        StarBean star = mResults.getSelectedItem();
        if (star == null)
            return;
        DlgStarEdit dlg = new DlgStarEdit(mResults.getControl().getShell());
        dlg.setStar(star);
        if (dlg.open() != Dialog.OK)
            return;
        star = dlg.getStar();
        ChViewVisualizationLogic.updateStar(star);
    }
    
    private void doRefresh()
    {
        mResults.setInput(ChViewVisualizationLogic.mPreferences.getStars());
        mResults.refresh();
    }

    private void doResultsSelectionChanged()
    {
        List<StarBean> sel = mResults.getSelectedItems();
        StarBean focus = ChViewVisualizationLogic.mPreferences.getFocus();
        if (sel.size() == 0)
            ChViewVisualizationLogic.setFocused(null);
        if ((focus != null) && (sel.contains(focus)))
            return;
        ChViewVisualizationLogic.setFocused(mResults.getSelectedItem());
    }
    
    private void doMenu(Point p)
    {
        int[] rc = mResults.getSelectedCell(p);
        StarBean star = mResults.getInput().get(rc[0]);
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
    
    private void doChangeFocus(StarBean oldFocus, StarBean newFocus)
    {
        List<StarBean> currentFocus = mResults.getSelectedItems();
        if (oldFocus != null)
            currentFocus.remove(oldFocus);
        if (newFocus != null)
        {
            currentFocus.add(newFocus);
            String msg = ChViewRenderLogic.getStarName(newFocus)
                    +" "+FormatUtils.formatDouble(newFocus.getX(), 1)
                    +","+FormatUtils.formatDouble(newFocus.getY(), 1)
                    +","+FormatUtils.formatDouble(newFocus.getZ(), 1);
            getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
        }
        else
            getViewSite().getActionBars().getStatusLineManager().setMessage("");
        mResults.setSelectedItems(currentFocus);
    }

    private void copyList(List<StarBean> stars)
    {
        ITableLabelProvider labels = mResults.getLabels();
        int cols = mResults.getViewer().getTree().getColumnCount();
        StringBuffer sb = new StringBuffer();
        for (StarBean star : stars)
        {
            if (sb.length() > 0)
                sb.append(System.getProperty("line.separator"));
            for (int i = 0; i < cols; i++)
            {
                if (i > 0)
                    sb.append(",");
                sb.append(labels.getColumnText(star, i));
            }
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

    public void updateEnablement()
    {
        boolean readOnly = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        mMenuDel.setEnabled(!readOnly);
        mMenuEdit.setEnabled(!readOnly);
        mActionDel.setEnabled(!readOnly);
        mActionEdit.setEnabled(!readOnly);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if ("context".equals(evt.getPropertyName()) || "focus".equals(evt.getPropertyName()))
            ThreadLogic.runMethodOnUIThread(this, "doPropertyChange", evt);
    }
    
    public void doPropertyChange(PropertyChangeEvent evt)
    {
        if ("context".equals(evt.getPropertyName()))
            doRefresh();
        else if ("focus".equals(evt.getPropertyName()))
            doChangeFocus((StarBean)evt.getOldValue(), (StarBean)evt.getNewValue());
    }
}
