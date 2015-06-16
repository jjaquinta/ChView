package jo.d2k.admin.rcp.sys.ui;

import jo.d2k.admin.rcp.viz.chview.SearchPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class SearchView extends ViewPart
{
    public static final String     ID = SearchView.class.getName();

    private SearchPanel     mClient;
    
    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mClient = new SearchPanel(parent, SWT.FULL_SELECTION);

        getViewSite().setSelectionProvider(mClient.getSelectionProvider());
    }

    @Override
    public void setFocus()
    {
        mClient.setFocus();
    }
}
