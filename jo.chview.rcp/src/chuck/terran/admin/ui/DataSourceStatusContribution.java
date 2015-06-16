package chuck.terran.admin.ui;

import jo.d2k.data.logic.DataLogic;
import jo.d2k.data.logic.IDataSource;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.ui.ctrl.MenuUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class DataSourceStatusContribution extends WorkbenchWindowControlContribution {
    private CLabel  mLabel;
    
    protected Control createControl( Composite parent ) {
        Composite client = new Composite(parent, SWT.NULL);
        GridLayout gl = new GridLayout(2, false);
        gl.horizontalSpacing = 0;
        gl.marginBottom = 0;
        gl.marginHeight = 0;
        gl.marginLeft = 0;
        gl.marginRight = 0;
        gl.marginTop = 0;
        gl.marginWidth = 0;
        client.setLayout(gl);
        Label separator = new Label(client, SWT.SEPARATOR);
        GridUtils.setLayoutData(separator, "");
        mLabel = new CLabel(client, SWT.LEFT);
        GridUtils.setLayoutData(separator, "size=32x16");

        RuntimeLogic.getInstance().addUIPropertyChangeListener("dataSource", new PropertyChangeInvoker(this, "updateDataSource", client));
        mLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                doSwitchData();
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
        updateDataSource();
        return client;
    }

    private void updateMenu()
    {
        IDataSource currentSource = RuntimeLogic.getInstance().getDataSource();
        Menu menu = mLabel.getMenu();
        while (menu.getItemCount() > 0)
            menu.getItem(0).dispose();
        for (IDataSource ds : DataLogic.getDataSources())
        {
            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText(ds.getName());
            if (ds.isReadOnly())
                item.setImage(ImageUtils.getMappedImage("tb_lock"));
            else
                item.setImage(ImageUtils.getMappedImage("tb_unlock"));
            item.setData("ds", ds);
            item.setEnabled(ds != currentSource);
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    IDataSource src = (IDataSource)e.widget.getData("ds");
                    DataLogic.setDataSource(src);
                }
            });
        }        
        MenuUtils.addCommandCategory(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), menu, "chuck.terran.ds");
        /*
        MenuItem importItem = new MenuItem(menu, SWT.PUSH);
        importItem.setText("Lock...");
        importItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                HandlerLock.doLock();
            }
        });
        MenuItem exportItem = new MenuItem(menu, SWT.PUSH);
        exportItem.setText("Unlock...");
        exportItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                HandlerUnlock.doUnlock();
            }
        });
        MenuItem newItem = new MenuItem(menu, SWT.PUSH);
        newItem.setText("New...");
        newItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                HandlerNewData.doNew();
            }
        });
        */
    }

    private void doSwitchData()
    {
        DlgPickDataSource dlg = new DlgPickDataSource(Display.getDefault().getActiveShell());
        dlg.open();
    }
    
    public void updateDataSource()
    {
        IDataSource currentSource = RuntimeLogic.getInstance().getDataSource();
        mLabel.setText(currentSource.getName());
        if (currentSource.isReadOnly())
            mLabel.setImage(ImageUtils.getMappedImage("tb_lock"));
        else
            mLabel.setImage(ImageUtils.getMappedImage("tb_unlock"));
    }
  }