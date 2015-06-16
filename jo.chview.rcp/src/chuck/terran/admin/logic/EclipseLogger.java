package chuck.terran.admin.logic;

import jo.chview.rcp.Activator;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.LogEngine;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class EclipseLogger implements LogEngine
{
    public static int       mDebugLevel = DebugUtils.INFO;
    static
    {
        String level = System.getProperty("debug.level");
        if (level != null)
            mDebugLevel = Integer.parseInt(level);
    }

    @Override
    public void log(int severity, String msg, Throwable exception)
    {
        if (severity > mDebugLevel)
            return;
        IStatus status = new Status(translateSeverity(severity), Activator.PLUGIN_ID, msg, exception);
        Activator.getDefault().getLog().log(status);       
    }
    
    private int translateSeverity(int joSeverity)
    {
        switch (joSeverity)
        {
            case DebugUtils.CRITICAL:
                return IStatus.ERROR;
            case DebugUtils.ERROR:
                return IStatus.ERROR;
            case DebugUtils.WARN:
                return IStatus.WARNING;
            case DebugUtils.INFO:
                return IStatus.INFO;
            case DebugUtils.TRACE:
                return IStatus.OK;
        }
        return IStatus.OK;
    }
}
