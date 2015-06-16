package jo.d2k.admin.rcp.sys.ui;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.util.ui.act.GenericAction;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgStarEdit extends GenericDialog
{
    private static final int DEL = 0x1001;
    
    private StarBean  mStar;
    
    private ScrolledComposite mScroller;
    private StarPanel mClient;
    
    public DlgStarEdit(Shell parentShell)
    {
        this(parentShell, true);
    }
    
    public DlgStarEdit(Shell parentShell, boolean modal)
    {
        super(parentShell);
        if (!modal)
            setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.RESIZE);
        else
            setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.TITLE | SWT.RESIZE);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Edit Star");
        
        mScroller = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridUtils.setLayoutData(mScroller, "fill=hv");
        mClient = new StarPanel(mScroller, SWT.NULL);
        mScroller.setExpandHorizontal(true);
        mScroller.setExpandVertical(true);
        mScroller.setContent(mClient);
        mClient.setStar(mStar);
        updateScroller();
        return mScroller;
    }

    private void updateScroller()
    {
        mClient.pack();
        mScroller.setMinSize(mClient.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mScroller.layout();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, DEL, "Delete", false);
        super.createButtonsForButtonBar(parent);
    }
    
    @Override
    protected void buttonPressed(int buttonId)
    {
        if (buttonId == DEL)
            delPressed();
        else
            super.buttonPressed(buttonId);
    }
    
    @Override
    protected void okPressed()
    {
        mStar = mClient.getStar();
        super.okPressed();
    }

    private void delPressed()
    {
        if (!GenericAction.openQuestion("Delete Star", "Are you sure you want to delete "+ChViewRenderLogic.getStarName(mStar)+"?"))
            return;
        List<StarBean> stars = new ArrayList<StarBean>();
        stars.add(mStar);
        ChViewVisualizationLogic.deleteStars(stars);
        cancelPressed();
    }
    
    public StarBean getStar()
    {
        return mStar;
    }

    public void setStar(StarBean star)
    {
        mStar = star;
    }
}
