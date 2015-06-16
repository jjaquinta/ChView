package jo.d2k.admin.rcp.viz.chview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.data.data.SearchParams;
import jo.d2k.data.data.StarBean;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.DoubleUtils;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SearchParamsPanel extends Composite implements PropertyChangeListener
{
    private SearchParams mSearch;

    private SimpleContentProposalProvider mProposals;
    
    private Text    mPattern;
    private Button  mFirstOnly;
    private Text    mCenterX;
    private Text    mCenterY;
    private Text    mCenterZ;
    private Text    mRadius;
    private FilterPanel mFilter;
    
    public SearchParamsPanel(Composite parent, int style)
    {
        super(parent, style);
        setLayout(new GridLayout(6, false));
        
        GridUtils.makeLabel(this, "Name:", "");
        mPattern = GridUtils.makeText(this, "", "5x1 fill=h");
        GridUtils.makeLabel(this, "", "");
        mFirstOnly = GridUtils.makeCheck(this, "First Only", "5x1");
        GridUtils.makeLabel(this, "Centered:", "");
        mCenterX = GridUtils.makeText(this, "", "fill=h");
        GridUtils.makeLabel(this, ",", "");
        mCenterY = GridUtils.makeText(this, "", "fill=h");
        GridUtils.makeLabel(this, ",", "");
        mCenterZ = GridUtils.makeText(this, "", "fill=h");
        GridUtils.makeLabel(this, "Radius:", "");
        mRadius = GridUtils.makeText(this, "", "fill=h");
        mFilter = new FilterPanel(this, SWT.BORDER);
        GridUtils.setLayoutData(mFilter, "6x1 fill=hv");
        
        mProposals = new SimpleContentProposalProvider(new String[0]);
        KeyStroke ks = null;
        try
        {
            ks = KeyStroke.getInstance("Ctrl+Space");
        }
        catch (ParseException e)
        {   // should never happen
            e.printStackTrace();
        }
        ContentProposalAdapter adapter = new ContentProposalAdapter(mPattern, new TextContentAdapter(),
                mProposals,ks,null);
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        if (mSearch != null)
            updateContentProposal();
    }
    
    private void updateContentProposal()
    {
        if (mProposals == null)
            return;
        List<String> proposals = new ArrayList<>();
        if (mSearch.getCache() != null)
            for (StarBean star : mSearch.getCache())
                proposals.add(ChViewRenderLogic.getStarName(star));
        mProposals.setProposals(proposals.toArray(new String[0]));
        //DebugUtils.trace("Setting proposals: "+proposals);
    }
    
    public SearchParams getSearch()
    {
        mSearch.setPattern(mPattern.getText());
        mSearch.setFindFirst(mFirstOnly.getSelection());
        mSearch.getCenter().x = DoubleUtils.parseDouble(mCenterX.getText());
        mSearch.getCenter().y = DoubleUtils.parseDouble(mCenterY.getText());
        mSearch.getCenter().z = DoubleUtils.parseDouble(mCenterZ.getText());
        mSearch.setSearchRadius(DoubleUtils.parseDouble(mRadius.getText()));
        mSearch.getFilter().set(mFilter.getFilter());
        return mSearch;
    }

    public void setSearch(SearchParams params)
    {
        if (mSearch != null)
            mSearch.removePropertyChangeListener(this);
        mSearch = params;
        mPattern.setText(mSearch.getPattern());
        mFirstOnly.setSelection(mSearch.isFindFirst());
        mCenterX.setText(String.valueOf(mSearch.getCenter().x));
        mCenterY.setText(String.valueOf(mSearch.getCenter().y));
        mCenterZ.setText(String.valueOf(mSearch.getCenter().z));
        mRadius.setText(String.valueOf(mSearch.getSearchRadius()));
        mFilter.setFilter(mSearch.getFilter());
        updateContentProposal();
        mSearch.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent ev)
    {
        if ("cache".equals(ev.getPropertyName()))
            updateContentProposal();
    }
}
