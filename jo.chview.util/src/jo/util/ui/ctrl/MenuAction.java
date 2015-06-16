package jo.util.ui.ctrl;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;

public class MenuAction implements IAction
{
    private MenuItem    mMenu;
    
    public MenuAction(MenuItem menu)
    {
        mMenu = menu;
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener)
    {
    }

    public int getAccelerator()
    {
        return mMenu.getAccelerator();
    }

    public String getActionDefinitionId()
    {
        return String.valueOf(mMenu.hashCode());
    }

    public String getDescription()
    {
        return "";
    }

    public ImageDescriptor getDisabledImageDescriptor()
    {
        return null;
    }

    public HelpListener getHelpListener()
    {
        return null;
    }

    public ImageDescriptor getHoverImageDescriptor()
    {
        return null;
    }

    public String getId()
    {
        return String.valueOf(mMenu.hashCode());
    }

    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    public IMenuCreator getMenuCreator()
    {
        return null;
    }

    public int getStyle()
    {
        return mMenu.getStyle();
    }

    public String getText()
    {
        return mMenu.getText();
    }

    public String getToolTipText()
    {
        return null;
    }

    public boolean isChecked()
    {
        return false;
    }

    public boolean isEnabled()
    {
        return mMenu.isEnabled();
    }

    public boolean isHandled()
    {
        return false;
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener)
    {
    }

    public void run()
    {
    }

    public void runWithEvent(Event event)
    {
    }

    public void setAccelerator(int keycode)
    {
        mMenu.setAccelerator(keycode);
    }

    public void setActionDefinitionId(String id)
    {
    }

    public void setChecked(boolean checked)
    {
    }

    public void setDescription(String text)
    {
    }

    public void setDisabledImageDescriptor(ImageDescriptor newImage)
    {
    }

    public void setEnabled(boolean enabled)
    {
        mMenu.setEnabled(enabled);
    }

    public void setHelpListener(HelpListener listener)
    {
    }

    public void setHoverImageDescriptor(ImageDescriptor newImage)
    {
    }

    public void setId(String id)
    {
    }

    public void setImageDescriptor(ImageDescriptor newImage)
    {
        mMenu.setImage(newImage.createImage());
    }

    public void setMenuCreator(IMenuCreator creator)
    {
    }

    public void setText(String text)
    {
        mMenu.setText(text);
    }

    public void setToolTipText(String text)
    {
    }
}
