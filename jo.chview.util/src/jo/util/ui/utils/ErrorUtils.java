/*
 * Created on Apr 15, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ErrorUtils
{
    public static void error(String pluginId, int code, String errorMsg, String activity, Throwable t)
    {
        Shell sh = Display.getCurrent().getActiveShell();
        String msg = "Error '"+errorMsg+"' while '"+activity+"'";
        String title = activity+": ERROR";
        Status status = new Status(IStatus.ERROR, pluginId, code, msg, t);
        
        ErrorDialog err = new ErrorDialog(sh, title, msg, status, IStatus.ERROR);
        err.open();
    }
}
