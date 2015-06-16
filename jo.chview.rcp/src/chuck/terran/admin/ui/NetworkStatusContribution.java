package chuck.terran.admin.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.d2k.data.logic.RuntimeLogic;
import jo.util.logic.ThreadLogic;
import jo.util.ui.utils.GridUtils;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class NetworkStatusContribution extends WorkbenchWindowControlContribution {
    private CLabel  mLabel;
    private Thread  mWatcher = null;
    private int     mIconTick = 1;
    
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
        GridUtils.setLayoutData(separator, "size=16x16");

        fgWatching();
        
        RuntimeLogic.getInstance().addPropertyChangeListener("busyCount", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if ((RuntimeLogic.getInstance().getBusyCount() > 0) && (mWatcher == null))
                    startWatcher();
            }
        });
        return client;
    }
    
    private void startWatcher()
    {
        mWatcher = new Thread("BusyWatcher") { public void run() { bgWatching(); } };
        mWatcher.start();
    }
    
    private void bgWatching()
    {
        mIconTick = 1;
        for (;;)
        {
            if (mLabel.isDisposed())
                break;
            ThreadLogic.runMethodOnUIThread(this, "fgWatching");
            if (++mIconTick > 10)
                mIconTick = 1;
            if (RuntimeLogic.getInstance().getBusyCount() <= 0)
                break;
            try
            {
                Thread.sleep(250);
            }
            catch (InterruptedException e)
            {
            }
        }
        mWatcher = null;
    }
    
    public void fgWatching()
    {
        if (mLabel.isDisposed())
            return;
        int count = RuntimeLogic.getInstance().getBusyCount();
        if (count <= 0)
        {
            mLabel.setImage(ImageUtils.getMappedImage("app_busy0"));
            //mLabel.setText("");
        }
        else
        {
            mLabel.setImage(ImageUtils.getMappedImage("app_busy"+mIconTick));
            //mLabel.setText("busy");
        }
    }
  }