package jo.d2k.admin.rcp.sys.ui;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.logic.ThreadLogic;
import jo.util.ui.utils.ClipboardLogic;
import jo.util.ui.utils.ImageUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class InfoView extends ViewPart
{
    public static final String     ID = InfoView.class.getName();

    private Object      mSelected;
    private boolean     mPinned;
    private Action      mActionCopy;
    private Action      mActionEdit;
    private Action      mActionDel;
    private Action      mActionPin;
    
    private Composite   mPanel;
    private Composite   mBlank;
    private StarPanel         mStarPanel;
    private StarSystemPanel   mStarsPanel;
    private SunBodyPanel         mSunPanel;
    private SolidBodyPanel         mBodyPanel;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mPanel = new Composite(parent, SWT.NULL);
        mPanel.setLayout(new StackLayout());
        mBlank = new Composite(mPanel, SWT.NULL);
        mStarPanel = new StarPanel(mPanel, SWT.READ_ONLY);
        mStarsPanel = new StarSystemPanel(mPanel, SWT.READ_ONLY);
        mSunPanel = new SunBodyPanel(mPanel, SWT.NULL);
        mBodyPanel = new SolidBodyPanel(mPanel, SWT.NULL);
        addActions();
        mPinned = !StringUtils.isTrivial(getViewSite().getSecondaryId());
        if (!mPinned)
        {
            getViewSite().getPage().addSelectionListener(new ISelectionListener() {            
                @Override
                public void selectionChanged(IWorkbenchPart part, final ISelection sel)
                {
                    if (mPanel.isDisposed())
                    {
                        getSite().getPage().removeSelectionListener(this);
                        return;
                    }
                    ThreadLogic.runOnUIThread(new Thread() { public void run() {
                        if (sel.isEmpty())
                            setSelected(null);
                        else
                            setSelected(((IStructuredSelection)sel).getFirstElement());
                    }});
                }
            });
            RuntimeLogic.getInstance().addUIPropertyChangeListener("dataSource", new PropertyChangeInvoker(this, "updateEnablement", mPanel));
        }
        else
        {
            RuntimeLogic.getInstance().addUIPropertyChangeListener("dataSource", new PropertyChangeInvoker(this, "hideView", mPanel));
            // TODO: when data changes, if this has been deleted, close window
            Thread t = new Thread("autoClose") { public void run() {
                try { Thread.sleep(250); } catch (InterruptedException e) { }
                if (mSelected == null)
                    ThreadLogic.runOnUIThread(new Runnable() { public void run() { hideView(); } });
                } };
            t.start();
        }
    }
    
    public void hideView()
    {
        getViewSite().getPage().hideView(InfoView.this);
    }
    
    private void addActions()
    {
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager dropDownMenu = actionBars.getMenuManager();
        IToolBarManager toolBar = actionBars.getToolBarManager();
        mActionCopy = new Action("Copy", ImageUtils.getMappedImageDescriptor("tb_copy"))
        {
            public void run() { doCopy(); };
        };
        mActionCopy.setEnabled(false);
        dropDownMenu.add(mActionCopy);
        toolBar.add(mActionCopy);
        mActionEdit = new Action("Edit", ImageUtils.getMappedImageDescriptor("tb_edit"))
        {
            public void run() { doEdit(); };
        };
        mActionEdit.setEnabled(false);
        dropDownMenu.add(mActionEdit);
        toolBar.add(mActionEdit);
        mActionDel = new Action("Del", ImageUtils.getMappedImageDescriptor("tb_del"))
        {
            public void run() { doDel(); };
        };
        mActionDel.setEnabled(false);
        dropDownMenu.add(mActionDel);
        toolBar.add(mActionDel);
        mActionPin = new Action("Pin", ImageUtils.getMappedImageDescriptor("tb_pin"))
        {
            public void run() { doPin(); };
        };
        mActionPin.setEnabled(false);
        dropDownMenu.add(mActionPin);
        toolBar.add(mActionPin);
    }
    
    private void doCopy()
    {
        Control topControl = ((StackLayout)mPanel.getLayout()).topControl;
        if (!(topControl instanceof IToText))
            return;
        String txt = ((IToText)(topControl)).toText();
        String html = ((IToText)(topControl)).toHTML();
        if (StringUtils.isTrivial(txt))
            return;
        ClipboardLogic.setAsTextHTML(txt, html);
    }
    
    private void doEdit()
    {
        Control topControl = ((StackLayout)mPanel.getLayout()).topControl;
        if (!(topControl instanceof IToText))
            return;
        ((IToText)(topControl)).doEdit();
    }
    
    private void doDel()
    {
        Control topControl = ((StackLayout)mPanel.getLayout()).topControl;
        if (!(topControl instanceof IToText))
            return;
        ((IToText)(topControl)).doDel();
    }
    
    private void doPin()
    {
        try
        {
            InfoView newView = (InfoView)getViewSite().getPage().showView(ID, "id"+System.currentTimeMillis(), IWorkbenchPage.VIEW_ACTIVATE);
            newView.setSelected(mSelected);
        }
        catch (PartInitException e)
        {
            DebugUtils.error("Cannot pin info view!", e);
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        mStarPanel.setFocus();
    }

    public Object getSelected()
    {
        return mSelected;
    }

    public void setSelected(Object selected)
    {
        if (mPanel.isDisposed())
            return;
        mSelected = selected;
        if (mSelected == null)
            ((StackLayout)mPanel.getLayout()).topControl = mBlank;
        else if (mSelected instanceof StarBean)
        {
            StarBean primary = (StarBean)mSelected;
            if (primary.getChildren().size() > 1)
            {
                ((StackLayout)mPanel.getLayout()).topControl = mStarsPanel;
                mStarsPanel.setStars(primary.getAllChildren());                
            }
            else
            {
                ((StackLayout)mPanel.getLayout()).topControl = mStarPanel;
                mStarPanel.setStar(primary);
            }
            if (mPinned)
                setPartName(ChViewRenderLogic.getStarName(primary));
        }
        else if (mSelected instanceof SunBean)
        {
            ((StackLayout)mPanel.getLayout()).topControl = mSunPanel;
            mSunPanel.setBody((BodyBean)mSelected);
            if (mPinned)
                setPartName(((BodyBean)mSelected).getName());
        }
        else if (mSelected instanceof SolidBodyBean)
        {
            ((StackLayout)mPanel.getLayout()).topControl = mBodyPanel;
            mBodyPanel.setBody((BodyBean)mSelected);
            if (mPinned)
                setPartName(((BodyBean)mSelected).getName());
        }
        mPanel.layout();
        updateEnablement();
    }

    public void updateEnablement()
    {
        boolean editable = ((StackLayout)mPanel.getLayout()).topControl instanceof IToText;
        mActionCopy.setEnabled(editable);
        boolean readWrite = !RuntimeLogic.getInstance().getDataSource().isReadOnly();
        mActionEdit.setEnabled(editable && readWrite);
        mActionDel.setEnabled(editable && readWrite);
        mActionPin.setEnabled(!mPinned && (mSelected != null));
    }
}