package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.data.data.SearchParams;
import jo.d2k.data.data.StarBean;
import jo.util.logic.ThreadLogic;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import chuck.terran.admin.ui.StarsViewer;

public class DlgSearchOutput extends GenericDialog
{
    private SearchParams    mSearch;
    private StarBean        mSelected;
    
    private SearchParamsPanel mParams;
    private Label       mStatus;
    private StarsViewer mResults;

    private int mLastTick = 0;
    private int mLastResults = 0;
    
    public DlgSearchOutput(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Star Search Results");
        Composite client = new Composite(parent, SWT.NULL);
        GridUtils.setLayoutData(client, "fill=hv");
        client.setLayout(new GridLayout(1, false));
        mParams = new SearchParamsPanel(client, SWT.NULL);
        GridUtils.setLayoutData(mParams, "fill=hv");
        mStatus = GridUtils.makeLabel(client, "Searching...", "fill=h");
        mResults = new StarsViewer(client, SWT.FULL_SELECTION);
        mResults.setInput(mSearch.getResults());
        GridUtils.setLayoutData(mResults.getControl(), "fill=hv");
        mParams.setSearch(mSearch);
        mParams.setEnabled(false);
        
        Thread t = new Thread("Search watcher") { public void run() { watchBackground(); } };
        t.start();
        
        return mParams;
    }
    
    private void watchBackground()
    {
        for (;;)
        {
            if (mParams.isDisposed())
                break;
            ThreadLogic.runMethodOnUIThread(this,  "watchForeground");
            if (mSearch.isDone())
                break;
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
        } 
    }

    public void watchForeground()
    {
        if (mParams.isDisposed())
            return;
        if (mLastResults != mSearch.getResults().size())
        {
            mLastResults = mSearch.getResults().size();
            mResults.refresh();
        }
        if (++mLastTick > 10)
            mLastTick = 0;
        if (mSearch.isDone())
            mStatus.setText("Done searching, "+mSearch.getResults().size()+" found");
        else
        {
            String status = "Searching";
            for (int i = 0; i <= mLastTick; i++)
                status += ".";
            mStatus.setText(status);
        }
    }
    
    @Override
    protected void okPressed()
    {
        mSearch.setCancel(true);
        mSelected = (StarBean)mResults.getSelectedItem();
        super.okPressed();
    }
    
    @Override
    protected void cancelPressed()
    {
        mSearch.setCancel(true);
        mSelected = null;
        super.cancelPressed();
    }

    public SearchParams getSearch()
    {
        return mSearch;
    }

    public void setSearch(SearchParams search)
    {
        mSearch = search;
    }

    public StarBean getSelected()
    {
        return mSelected;
    }

    public void setSelected(StarBean selected)
    {
        mSelected = selected;
    }
}
