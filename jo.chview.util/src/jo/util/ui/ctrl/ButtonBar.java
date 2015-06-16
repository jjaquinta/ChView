package jo.util.ui.ctrl;

import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

public class ButtonBar
{
    private ToolBar             mToolBar;
    private SelectionListener   mListener;
    private Shell               mShell;
    private ToolItem            mLastItem;
    private int                 mMaxWidth;
    
    public ButtonBar(Composite parent, int style, String gridStyle)
    {
        mToolBar = new ToolBar(parent, style);
        if (gridStyle != null)
            GridUtils.setLayoutData(mToolBar, gridStyle);
        if (parent != null)
        {
            mShell = parent.getShell();
            while (parent != null)
            {
                if (parent instanceof SelectionListener)
                {
                    mListener = (SelectionListener)parent;
                    break;
                }
                parent = parent.getParent();
            }
        }
        mMaxWidth = 0;
    }
    
    public Button addButton(Image image)
    {
        return addButton(image, null);
    }
    public Button addButton(String name)
    {
        return addButton(null, name);
    }
    public Button addButton(Image image, String name)
    {
        return addButton(image, name, SWT.PUSH);
    }
    public Button addCheckbox(Image image)
    {
        return addCheckbox(image, null);
    }
    public Button addCheckbox(String name)
    {
        return addCheckbox(null, name);
    }
    public Button addCheckbox(Image image, String name)
    {
        return addButton(image, name, SWT.CHECK);
    }
    public Button addButton(Image image, String name, int style)
    {
        mLastItem = new ToolItem(mToolBar, SWT.SEPARATOR);
        Button btn = new Button(mToolBar, style);
        if (image != null)
            btn.setImage(image);
        if (name != null)
            btn.setText(name);
        if (mListener != null)
            btn.addSelectionListener(mListener);
        btn.pack();
        mMaxWidth = Math.max(mMaxWidth, btn.getSize().x);
        mLastItem.setControl(btn);
        for (int i = 0; i < mToolBar.getItemCount(); i++)
        {
            ToolItem item = mToolBar.getItem(i);
            if (item.getStyle() == SWT.SEPARATOR)
                item.setWidth(mMaxWidth);
        }
        mToolBar.pack();
        return btn;
    }
    
    public ToolItem addDropDown(Image image)
    {
        return addDropDown(image, null);
    }
    public ToolItem addDropDown(String name)
    {
        return addDropDown(null, name);
    }
    public ToolItem addDropDown(Image image, String name)
    {
        mLastItem = new ToolItem(mToolBar, SWT.DROP_DOWN);
        if (image != null)
            mLastItem.setImage(image);
        if (name != null)
            mLastItem.setText(name);
        if (mListener != null)
            mLastItem.addSelectionListener(mListener);
        return mLastItem;
    }
    
    public ToolItem addPush(Image image)
    {
        return addPush(image, null);
    }
    public ToolItem addPush(String name)
    {
        return addPush(null, name);
    }
    public ToolItem addPush(Image image, String name)
    {
        mLastItem = new ToolItem(mToolBar, SWT.PUSH);
        if (image != null)
            mLastItem.setImage(image);
        if (name != null)
            mLastItem.setText(name);
        if (mListener != null)
            mLastItem.addSelectionListener(mListener);
        return mLastItem;
    }
    
    public MenuItem addMenu(String name)
    {
        if (mLastItem == null)
            return null;
        Menu m = (Menu)mLastItem.getData("menu");
        if (m == null)
        {
            m = new Menu(mShell, SWT.POP_UP);
            mLastItem.setData("menu", m);
        }
        MenuItem item = new MenuItem (m, SWT.PUSH);
        item.setText(name);
        if (mListener != null)
            item.addSelectionListener(mListener);
        return item;
    }

    public boolean popup(SelectionEvent e)
    {
        if (e.detail != SWT.ARROW)
            return false;
        Object o = e.getSource();
        if ((o == null) || !(o instanceof ToolItem))
            return false;
        ToolItem item = (ToolItem)e.getSource();
        o = item.getData("menu");
        if ((o == null) || !(o instanceof Menu))
            return false;
        Menu menu = (Menu)o;
        Rectangle rect = item.getBounds();
        Point pt = new Point(rect.x, rect.y + rect.height);
        pt = mToolBar.toDisplay(pt);
        menu.setLocation(pt.x, pt.y);
        menu.setVisible(true);
        return true;
    }

    public SelectionListener getListener()
    {
        return mListener;
    }

    public void setListener(SelectionListener listener)
    {
        mListener = listener;
        if (mListener instanceof Control)
            mShell = ((Control)mListener).getShell();
        else if (mListener instanceof ViewPart)
            mShell = ((ViewPart)mListener).getViewSite().getShell();
    }

    public Shell getShell()
    {
        return mShell;
    }

    public void setShell(Shell shell)
    {
        mShell = shell;
    }

    public ToolBar getControl()
    {
        return mToolBar;
    }
}
