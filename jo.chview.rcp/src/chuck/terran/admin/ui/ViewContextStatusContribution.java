package chuck.terran.admin.ui;

import jo.d2k.admin.rcp.viz.chview.handlers.HandlerGoto;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.geom3d.Point3D;
import jo.util.utils.FormatUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class ViewContextStatusContribution extends WorkbenchWindowControlContribution {
    private CLabel  mLabel;
    
    
    
    protected Control createControl( Composite parent ) {
        mLabel = new CLabel(parent, SWT.RIGHT);

        ChViewVisualizationLogic.mPreferences.addUIPropertyChangeListener(new PropertyChangeInvoker(this, "updateViewContext", mLabel));
        mLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                doGoto();
            }
        });
        mLabel.addMenuDetectListener(new MenuDetectListener() {            
            @Override
            public void menuDetected(MenuDetectEvent ev)
            {
                updateMenu();
            }
        });
        Menu menu = new Menu(mLabel);
        mLabel.setMenu(menu);
        //updateViewContext();
        mLabel.setText("1000,1000,1000 x 100");
        mLabel.setVisible(false);
        return mLabel;
    }

    private void updateMenu()
    {
        Menu menu = mLabel.getMenu();
        while (menu.getItemCount() > 0)
            menu.getItem(0).dispose();
        MenuItem importItem = new MenuItem(menu, SWT.PUSH);
        importItem.setText("Goto...");
        importItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doGoto();
            }
        });
    }

    private void doGoto()
    {
        HandlerGoto.doGoto(mLabel.getShell());
    }
    
    public void updateViewContext()
    {
        if (mLabel.isDisposed())
            return;
        StringBuffer sb = new StringBuffer();
        Point3D c = ChViewVisualizationLogic.mPreferences.getCenter();
        sb.append(FormatUtils.formatDouble(c.x, 1)+","+FormatUtils.formatDouble(c.y, 1)+","+FormatUtils.formatDouble(c.z, 1)
                +" x "+FormatUtils.formatDouble(ChViewVisualizationLogic.mPreferences.getRadius(), 1));
        mLabel.setText(sb.toString());
        mLabel.setToolTipText(sb.toString());
        mLabel.setVisible(true);
    }
    
    
  }