/*
 * Created on May 10, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.dlg;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GenericDialog extends Dialog
{
    private boolean     mAccepted;
    private boolean     mAcceptedSet = false;
    IDialogSettings     mSettings;


    public static Shell getActiveShell()
    {
        return Display.getCurrent().getActiveShell();
    }
    
    /**
     * @param parentShell
     */
    public GenericDialog(Shell parentShell)
    {
        super(parentShell);
    }
    
    /**
     * @param parentShell
     */
    public GenericDialog()
    {
        super(getActiveShell());
    }
    
    protected Point getInitialSize()
    {
        return new Point(640, 480);
    }
    protected void okPressed()
    {
        if (!mAcceptedSet)
            mAccepted = true;
        super.okPressed();
    }
    protected void cancelPressed()
    {
        if (!mAcceptedSet)
            mAccepted = false;
        super.cancelPressed();
    }

    /**
     * @return Returns the accepted.
     */
    public boolean isAccepted()
    {
        return mAccepted;
    }

    /**
     * @param accepted The accepted to set.
     */
    public void setAccepted(boolean accepted)
    {
        mAccepted = accepted;
        mAcceptedSet = true;
    }

    public IDialogSettings getSettings()
    {
        return mSettings;
    }

    public void setSettings(IDialogSettings settings)
    {
        mSettings = settings;
    }
    
    public String getSetting(String key, String def)
    {
        try
        {
            return mSettings.get(key);
        }
        catch (Exception e)
        {
            return def;
        }
    }
    
    public int getIntSetting(String key, int def)
    {
        try
        {
            return mSettings.getInt(key);
        }
        catch (Exception e)
        {
            return def;
        }
    }
    
    public void putSetting(String key, String val)
    {
        mSettings.put(key, val);
    }
    
    public void putSetting(String key, int val)
    {
        mSettings.put(key, val);
    }
}
