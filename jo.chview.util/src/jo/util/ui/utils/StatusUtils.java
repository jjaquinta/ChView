package jo.util.ui.utils;

import jo.util.logic.ThreadLogic;

import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class StatusUtils
{
    public static void setMessage(final String msg)
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
            return;
        ThreadLogic.runOnUIThread(new Thread() { public void run() { StatusUtils.doSetMessage(msg); }});
    }
    
    public static void doSetMessage(String msg)
    {
        IViewSite vsite = getViewSite();
        if (vsite != null)
            vsite.getActionBars().getStatusLineManager().setMessage(msg);        
    }

    public static void setErrorMessage(final String msg)
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
            return;
        ThreadLogic.runOnUIThread(new Thread() { public void run() { doSetErrorMessage(msg); } });
    }
    
    private static void doSetErrorMessage(String msg)
    {
        IViewSite vsite = getViewSite();
        if (vsite != null)
            vsite.getActionBars().getStatusLineManager().setErrorMessage(msg);        
    }

    private static IViewSite getViewSite()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
            return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null)
            return null;
        IWorkbenchPart part = page.getActivePart();
        if (part == null)
            return null;
        IWorkbenchSite site = part.getSite();
        if (site == null)
            return null;
        if (!(site instanceof IViewSite))
            return null;
        IViewSite vsite = (IViewSite)site;;
        return vsite;
    }
}
