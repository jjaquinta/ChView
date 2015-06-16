package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgSearch extends GenericDialog
{
    private StarBean                mStar;
    private String                  mPattern;
    
    private SearchPanel     mClient;
    
    public DlgSearch(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Star Search");
        mClient = new SearchPanel(parent, SWT.NULL);
        if (!StringUtils.isTrivial(mPattern))
            mClient.setPattern(mPattern);
        else if (ChViewVisualizationLogic.mPreferences.getFocus() != null)
            mClient.setPattern(ChViewRenderLogic.getStarName(ChViewVisualizationLogic.mPreferences.getFocus()));
        GridUtils.setLayoutData(mClient, "fill=hv");
        
        mClient.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {            
            @Override
            public void selectionChanged(SelectionChangedEvent ev)
            {
                getButton(OK).setEnabled(!ev.getSelection().isEmpty());
            }
        });
        
        mClient.getSearchParams().setPattern(mPattern);
        
        return mClient;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        super.createButtonsForButtonBar(parent);
        getButton(OK).setEnabled(false);
    }
    
    @Override
    protected void okPressed()
    {
        IStructuredSelection sel = (IStructuredSelection)mClient.getSelectionProvider().getSelection();
        if (!sel.isEmpty())
            mStar = (StarBean)sel.getFirstElement();
        super.okPressed();
    }

    public StarBean getStar()
    {
        return mStar;
    }

    public void setStar(StarBean star)
    {
        mStar = star;
    }

    public String getPattern()
    {
        return mPattern;
    }

    public void setPattern(String pattern)
    {
        mPattern = pattern;
    }
}
