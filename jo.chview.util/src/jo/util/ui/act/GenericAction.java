/*
 * Created on Sep 29, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.act;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class GenericAction extends Action
{
    public static Shell getShell()
    {
        return Display.getCurrent().getActiveShell();
    }
    
    public static Shell getShell(Shell s)
    {
        if (s == null)
            return getShell();
        else
            return s;
    }
    
    public static boolean openQuestion(String title, String message)
    {
        return openQuestion(null, title, message);
    }
    
    public static boolean openQuestion(Shell shell, String title, String message)
    {
        return MessageDialog.openQuestion(getShell(shell), title, message);
    }
    
    public static void openError(String title, String message, Exception ex)
    {
        openError(null, title, message, ex);
    }
    
    public static void openError(Shell shell, String title, String message, Exception ex)
    {
        Status status = new Status(IStatus.ERROR, "jo.util.ui", 1, message, ex);
        ErrorDialog dlg = new ErrorDialog(getShell(shell), title, message, status, IStatus.ERROR);
        dlg.open();
    }
    
    private static Map<Class<?>,String> mFileNameMRU = new HashMap<Class<?>,String>();
    
    public String getSaveFile(String fileName, String[] filterNames, String[] filterExtensions)
    {
        return getSaveFile(getClass(), fileName, filterNames, filterExtensions);
    }
    
    public static String getSaveFile(Class<?> wrt, String fileName, String[] filterNames, String[] filterExtensions)
    {
        if (fileName == null)
            fileName = (String)mFileNameMRU.get(wrt);
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFileName(fileName);
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);
        fileName = dlg.open();
        if (fileName != null)
            mFileNameMRU.put(wrt, fileName);
        return fileName;
    }
    
    public String getSaveFile(String fileName, String filterName, String filterExtension)
    {
        return getSaveFile(getClass(), fileName, filterName, filterExtension);
    }
    
    public static String getSaveFile(Class<?> wrt, String fileName, String filterName, String filterExtension)
    {
        String[] filterNames = new String[1];
        filterNames[0] = filterName;
        String[] filterExtensions = new String[1];
        filterExtensions[0] = filterExtension;
        return getSaveFile(wrt, fileName, filterNames, filterExtensions);
    }
    
    public String getOpenFile(String fileName, String[] filterNames, String[] filterExtensions)
    {
        return getOpenFile(getClass(), fileName, filterNames, filterExtensions);
    }
    
    public static String getOpenFile(Class<?> wrt, String fileName, String[] filterNames, String[] filterExtensions)
    {
        if (fileName == null)
            fileName = mFileNameMRU.get(wrt);
        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
        dlg.setFileName(fileName);
        dlg.setFilterNames(filterNames);
        dlg.setFilterExtensions(filterExtensions);
        fileName = dlg.open();
        if (fileName != null)
            mFileNameMRU.put(wrt, fileName);
        return fileName;
    }
    
    public String getOpenFile(String fileName, String filterName, String filterExtension)
    {
        return getOpenFile(getClass(), fileName, filterName, filterExtension);
    }
    
    public static String getOpenFile(Class<?> wrt, String fileName, String filterName, String filterExtension)
    {
        String[] filterNames = new String[1];
        filterNames[0] = filterName;
        String[] filterExtensions = new String[1];
        filterExtensions[0] = filterExtension;
        return getOpenFile(wrt, fileName, filterNames, filterExtensions);
    }
    
    public static String getMRUFile(Class<?> wrt)
    {
        return mFileNameMRU.get(wrt);
    }
}
