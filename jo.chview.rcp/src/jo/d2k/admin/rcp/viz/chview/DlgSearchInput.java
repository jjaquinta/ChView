package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.data.data.SearchParams;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgSearchInput extends GenericDialog
{
    private SearchParams    mSearch;
    
    private SearchParamsPanel mClient;
    
    public DlgSearchInput(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Star Search");
        mClient = new SearchParamsPanel(parent, SWT.NULL);
        GridUtils.setLayoutData(mClient, "fill=hv");
        mClient.setSearch(mSearch);
        return mClient;
    }
    
    @Override
    protected void okPressed()
    {
        mSearch = mClient.getSearch();
        super.okPressed();
    }

    public SearchParams getSearch()
    {
        return mSearch;
    }

    public void setSearch(SearchParams search)
    {
        mSearch = search;
    }
}
