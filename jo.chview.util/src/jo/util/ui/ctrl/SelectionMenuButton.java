package jo.util.ui.ctrl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import jo.chview.util.Activator;
import jo.util.ui.utils.ImageUtils;
import jo.util.utils.ExtensionPointUtils;
import jo.util.utils.IExtensionPointProcessor;
import jo.util.utils.obj.StringUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class SelectionMenuButton extends Composite implements
        ISelectionListener, ISelectionChangedListener, ISelectionProvider, PropertyChangeListener, SelectionListener
{
    private ISelection  mSelection;
    private Object      mSelectedObject;
    private List<ISelectionChangedListener>   mListeners;
    
    private Button      mClick;
    private Image       mImage;
    
    public SelectionMenuButton(Composite parent, int style)
    {
        super(parent, style);
        mListeners = new ArrayList<ISelectionChangedListener>();
        setLayout(new FillLayout());
        mImage = ImageUtils.getMappedImageDescriptor("tape_ds").createImage();
        mClick = new Button(this, SWT.PUSH);
        mClick.setImage(mImage);
        mClick.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e)
            {
                doClick();
            }
        });
    }

    public void dispose()
    {
        mImage.dispose();
        super.dispose();
    }

    private void doClick()
    {
        if (mSelectedObject == null)
            return;
        Point point = getSize();
        point.x /= 2;
        point = toDisplay(point);
        final Menu main = new Menu(this);
        final IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
        ExtensionPointUtils.processExtensions("org.eclipse.ui.popupMenus", new IExtensionPointProcessor(){
            public Object process(IConfigurationElement element)
            {
                addMenu(main, element, activePart);
                return null;
            }
        });
        main.setLocation(point);
        main.setVisible(true);
    }
    
    private void addMenu(Menu main, IConfigurationElement objectContribution, IWorkbenchPart activePart)
    {
        if (!objectContribution.getName().equals("objectContribution"))
            return;
        Object object;
        try
        {
            object = objectContribution.createExecutableExtension("objectClass");
        }
        catch (CoreException e)
        {
            return;
        }
        if (object == null)
            return;
        Class<?> objectClass = object.getClass();
        if (!objectClass.isAssignableFrom(mSelectedObject.getClass()))
            return;
        IConfigurationElement[] children =  objectContribution.getChildren();
        for (int i = 0; i < children.length; i++)
            addAction(main, children[i], activePart);
    }
    
    private void addAction(Menu main, IConfigurationElement action, IWorkbenchPart activePart)
    {
        if (!action.getName().equals("action"))
            return;
        String label = action.getAttribute("label");
        String icon = action.getAttribute("icon");
        if (StringUtils.isTrivial(label) && StringUtils.isTrivial(icon))
            return;
        IObjectActionDelegate actionDelegate;
        try
        {
            actionDelegate = (IObjectActionDelegate)action.createExecutableExtension("class");
        }
        catch (CoreException e)
        {
            return;
        }
        if (actionDelegate == null)
            return;
        MenuItem mi = new MenuItem(main, SWT.PUSH);
        if (label != null)
            mi.setText(label);
        if (icon != null)
        {
            ImageDescriptor id = Activator.imageDescriptorFromPlugin(action.getContributor().getName(), icon);
            if (id != null)
                mi.setImage(id.createImage());
        }
        IAction actionWrapper = new MenuAction(mi);
        mi.setData("actionDelegate", actionDelegate);
        mi.setData("actionWrapper", actionWrapper);
        actionDelegate.setActivePart(actionWrapper, activePart);
        actionDelegate.selectionChanged(actionWrapper, mSelection);
        mi.addSelectionListener(this);
    }
    
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        setSelection(selection);
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
        setSelection(event.getSelection());
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        synchronized (mListeners)
        {
            mListeners.add(listener);
        }
    }

    public ISelection getSelection()
    {
        return mSelection;
    }

    public void removeSelectionChangedListener(
            ISelectionChangedListener listener)
    {
        synchronized (mListeners)
        {
            mListeners.remove(listener);
        }
    }

    public void setSelection(ISelection selection)
    {
        if (mSelection == selection)
            return;
        mSelection = selection;
        if ((mSelection != null) && (mSelection instanceof IStructuredSelection))
            mSelectedObject = ((IStructuredSelection)mSelection).getFirstElement();
        else
            mSelectedObject = null;
        ISelectionChangedListener[] listeners;
        synchronized (mListeners)
        {
            listeners = (ISelectionChangedListener[])mListeners.toArray(new ISelectionChangedListener[0]);
        }
        SelectionChangedEvent event = new SelectionChangedEvent(this, mSelection);
        for (int i = 0; i < listeners.length; i++)
        {
            listeners[i].selectionChanged(event);
        }
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getNewValue() == mSelectedObject)
            return;
        setSelection(new StructuredSelection(evt.getNewValue()));
    }

    public void widgetDefaultSelected(SelectionEvent e)
    {
        widgetSelected(e);
    }

    public void widgetSelected(SelectionEvent e)
    {
        MenuItem mi = (MenuItem)e.getSource();
        IObjectActionDelegate actionDelegate = (IObjectActionDelegate)mi.getData("actionDelegate");
        IAction actionWrapper = (IAction)mi.getData("actionWrapper");
        actionDelegate.run(actionWrapper);
    }

}
