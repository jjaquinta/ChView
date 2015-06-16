package jo.util.ui.dlg;

import jo.util.logic.ThreadLogic;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.LogEngine;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DebugWindow extends Window implements LogEngine
{
    private String  mData;
    private Text    mText;

    public DebugWindow(Shell shell)
    {
        super(shell);
        mData = "";
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent)
    {
        DebugUtils.mLoggers.add(this);
        mText = new Text(parent, SWT.READ_ONLY|SWT.H_SCROLL|SWT.V_SCROLL);
        GridUtils.setLayoutData(mText, "fill=hv");
        getShell().setSize(320, 200);
        getShell().addDisposeListener(new DisposeListener() {            
            @Override
            public void widgetDisposed(DisposeEvent ev)
            {
                DebugUtils.mLoggers.remove(DebugWindow.this);
            }
        });
        return mText;
    }

    public void log(int severity, String msg, Throwable exception)
    {
        mData += msg+"\r\n";
        if (exception != null)
            mData += exception.toString()+"\r\n";
        ThreadLogic.runMethodOnUIThread(this, "updateText");
    }
    
    public void updateText()
    {
        mText.setText(mData);
    }
}
