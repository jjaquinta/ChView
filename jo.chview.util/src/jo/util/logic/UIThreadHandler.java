package jo.util.logic;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class UIThreadHandler implements IUIThreadHandler
{
    public boolean isOnUIThread()
    {
        return isOnUIThread(Thread.currentThread().getStackTrace());
    }

    private boolean isOnUIThread(StackTraceElement[] stack)
    {
        for (StackTraceElement ste : stack)
        {
            String className = ste.getClassName();            
            if (className.contains("org.eclipse.ui.internal.Workbench"))
                return true;
        }
        return false;
    }
    
    @Override
    public void runOnUIThread(Runnable r)
    {
        String name = "uiThread"+System.currentTimeMillis();
        if (r instanceof Thread)
            name = ((Thread)r).getName();
        runOnUIThread(r, name, null);
    }

    @Override
    public void runOnUIThread(Runnable r, String name, Object wrt)
    {
        UIThread t = new UIThread(r, name, (Control)wrt);
        t.invoke();
    }

    class UIThread extends Thread
    {
        private Runnable    mRunnable;
        private Control     mCtrl;
        
        public UIThread(Runnable r, String name, Control wtr)
        {
            super((name != null) ? name : "ThreadLogic::runOnUIThread");
            mCtrl = wtr;
            mRunnable = r;
        }
        
        public void run()
        {
            if ((mCtrl != null) && mCtrl.isDisposed())
                return; // abort, control not there anymore
            mRunnable.run();
        }
        
        public void invoke()
        {
            Display d;
            if (mCtrl != null)
                d = mCtrl.getDisplay();
            else
                d = Display.getDefault();
            d.asyncExec(this);
        }
    }
}
