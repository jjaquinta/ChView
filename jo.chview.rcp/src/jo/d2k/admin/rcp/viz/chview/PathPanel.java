package jo.d2k.admin.rcp.viz.chview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.report.RouteFinderLogic;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.FormatUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import chuck.terran.admin.ui.StarsViewer;

public class PathPanel extends Composite
{
    private StarBean                mStar1;
    private StarBean                mStar2;
    private List<StarBean>          mLookups;

    private SimpleContentProposalProvider mProposals;
   
    private Text                    mCtrlStar1;
    private Button                  mStar1Lookup;
    private Text                    mCtrlStar2;
    private Button                  mStar2Lookup;
    private Text                    mLinkDistance;
    private Button                  mCalculate;
    private Button                  mBest;
    private Text                    mStarSeparation;
    private Text                    mPathDistance;
    private Text                    mPathJumps;
    private StarsViewer             mPath;
    
    public PathPanel(Composite parent, int style)
    {
        super(parent, style);
        mLookups = new ArrayList<>();
        setLayout(new GridLayout(3, false));
        GridUtils.makeLabel(this, "Point 1:", "");
        mCtrlStar1 = GridUtils.makeText(this, "", "fill=h");
        mStar1Lookup = GridUtils.makeButton(this, "...", "");
        GridUtils.makeLabel(this, "Point 2:", "");
        mCtrlStar2 = GridUtils.makeText(this, "", "fill=h");
        mStar2Lookup = GridUtils.makeButton(this, "...", "");
        GridUtils.makeLabel(this, "Gap:", "");
        mLinkDistance = GridUtils.makeText(this, FormatUtils.formatDouble(ChViewVisualizationLogic.mPreferences.getLinkDist4(), 2), "fill=h");
        mBest = GridUtils.makeButton(this, "Min", "");
        mBest.setToolTipText("Find minimum distance joining stars");
        GridUtils.makeLabel(this, "", "");
        mCalculate = GridUtils.makeButton(this, "Calculate", "");
        GridUtils.makeLabel(this, "", "");
        
        Group pathGroup = new Group(this, SWT.NULL);
        pathGroup.setText("Path");
        GridUtils.setLayoutData(pathGroup, "3x1 fill=hv");
        pathGroup.setLayout(new GridLayout(3, false));
        GridUtils.makeLabel(pathGroup, "Separation:", "");
        mStarSeparation = GridUtils.makeText(pathGroup, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(pathGroup, "Traverse:", "");
        mPathDistance = GridUtils.makeText(pathGroup, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(pathGroup, "Steps:", "");
        mPathJumps = GridUtils.makeText(pathGroup, SWT.READ_ONLY, "2x1 fill=h");
        mPath = new StarsViewer(pathGroup, SWT.FULL_SELECTION|SWT.MULTI);
        GridUtils.setLayoutData(mPath.getControl(), "3x1 fill=hv");
        
        mStar1Lookup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doLookup(1);
            }
        });
        mStar2Lookup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doLookup(2);
            }
        });
        
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
        ContentProposalAdapter adapter1 = new ContentProposalAdapter(mCtrlStar1, new TextContentAdapter(),
                mProposals,ks,null);
        adapter1.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        adapter1.addContentProposalListener(new IContentProposalListener() {
            @Override
            public void proposalAccepted(IContentProposal ev)
            {
                updateDistance();
            }
        });
        ContentProposalAdapter adapter2 = new ContentProposalAdapter(mCtrlStar2, new TextContentAdapter(),
                mProposals,ks,null);
        adapter2.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        adapter2.addContentProposalListener(new IContentProposalListener() {
            @Override
            public void proposalAccepted(IContentProposal ev)
            {
                updateDistance();
            }
        });
        updateProposals();
        mCtrlStar1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                updateDistance();
            }
        });
        mCtrlStar2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                updateDistance();
            }
        });
        mCalculate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doCalc();
            }
        });
        mBest.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doBest();
            }
        });
    }

    private void doCalc()
    {
        if ((mStar1 == null) || (mStar2 == null))
        {
            mPathDistance.setText("Select two stars first");
            mPathJumps.setText("");
            mPath.setInput(new ArrayList<StarBean>());
            return;
        }
        List<StarBean> path = RouteFinderLogic.computeRoute(mStar1, mStar2, DoubleUtils.parseDouble(mLinkDistance.getText()),
                ChViewVisualizationLogic.mPreferences.getHidden(), ChViewVisualizationLogic.mPreferences.getFilter(),
                ChViewVisualizationLogic.mPreferences);
        if (path.size() == 0)
        {
            mPathDistance.setText("No path between those two stars");
            mPathJumps.setText("");
            mPath.setInput(new ArrayList<StarBean>());
            return;
        }
        double dist = 0;
        for (int i = 0; i < path.size() - 1; i++)
            dist += StarExtraLogic.distance(path.get(i), path.get(i+1));
        mPathDistance.setText(FormatUtils.formatDouble(dist, 1)+" ly");
        mPathJumps.setText(String.valueOf(path.size()));
        mPath.setInput(path);        
    }

    private void doBest()
    {
        if ((mStar1 == null) || (mStar2 == null))
        {
            mPathDistance.setText("Select two stars first");
            mPathJumps.setText("");
            mPath.setInput(new ArrayList<StarBean>());
            return;
        }
        double highDist = StarExtraLogic.distance(mStar1, mStar2);
        double lowDist = 0;
        for (int i = 0; i < 10; i++)
        {
            double testDist = (highDist + lowDist)/2;
            List<StarBean> path = RouteFinderLogic.computeRoute(mStar1, mStar2, testDist,
                    ChViewVisualizationLogic.mPreferences.getHidden(), ChViewVisualizationLogic.mPreferences.getFilter(),
                    ChViewVisualizationLogic.mPreferences);
            if (path.size() == 0)
                lowDist = testDist;
            else
                highDist = testDist;
        }
        mLinkDistance.setText(String.valueOf(highDist));
        doCalc();
    }
    
    private void doLookup(int which)
    {
        DlgSearch dlg = new DlgSearch(getShell());
        String pattern = null;
        if (which == 1)
            pattern = mCtrlStar1.getText();
        else
            pattern = mCtrlStar2.getText();
        if (StringUtils.isTrivial(pattern))
            if (ChViewVisualizationLogic.mPreferences.getFocus() != null)
                pattern = ChViewRenderLogic.getStarName(ChViewVisualizationLogic.mPreferences.getFocus());
        dlg.setPattern(pattern);
        if (dlg.open() != Dialog.OK)
            return;
        StarBean star = dlg.getStar();
        if (star == null)
            return;
        if (which == 1)
            mCtrlStar1.setText(ChViewRenderLogic.getStarName(star));
        else
            mCtrlStar2.setText(ChViewRenderLogic.getStarName(star));
        mLookups.add(star);
        updateDistance();
    }
    
    public void updateDistance()
    {
        mStar1 = findStar(mCtrlStar1.getText());
        if (mStar1 == null)
        {
            mPath.setInput(new ArrayList<StarBean>());
            mStarSeparation.setText("");
            return;
        }
        mStar2 = findStar(mCtrlStar2.getText());
        if (mStar2 == null)
        {
            mPath.setInput(new ArrayList<StarBean>());
            mStarSeparation.setText("");
            return;
        }
        mStarSeparation.setText(FormatUtils.formatDouble(StarExtraLogic.distance(mStar1, mStar2), 1)+" ly");
    }

    public void updateProposals()
    {
        List<String> proposals = new ArrayList<>();
        if (ChViewVisualizationLogic.mPreferences.getFilteredStars() != null)
            for (StarBean star : ChViewVisualizationLogic.mPreferences.getFilteredStars())
            {
                if (star.getParent() != 0)
                    continue;
                proposals.add(ChViewRenderLogic.getStarName(star));
                mLookups.add(star);
            }
        String[] names = proposals.toArray(new String[0]);
        Arrays.sort(names);
        mProposals.setProposals(names);
    }

    private StarBean findStar(String name)
    {
        for (StarBean star : mLookups)
        {
            if (star.getName().equalsIgnoreCase(name))
                return star;
            if (ChViewRenderLogic.getStarName(star).equalsIgnoreCase(name))
                return star;            
        }
        return null;
    }

    public StarBean getStar1()
    {
        mStar1 = findStar(mCtrlStar1.getText());
        return mStar1;
    }

    public void setStar1(StarBean star1)
    {
        mStar1 = star1;
        if (mStar1 != null)
            mCtrlStar1.setText(ChViewRenderLogic.getStarName(mStar1));
        else
            mCtrlStar1.setText("");
    }

    public StarBean getStar2()
    {
        mStar2 = findStar(mCtrlStar2.getText());
        return mStar2;
    }

    public void setStar2(StarBean star2)
    {
        mStar2 = star2;
        if (mStar2 != null)
            mCtrlStar2.setText(ChViewRenderLogic.getStarName(mStar2));
        else
            mCtrlStar2.setText("");
    }
    
    public List<StarBean> getStarPath()
    {
        return mPath.getInput();
    }
    
    public void setStarPath(List<StarBean> path)
    {
        mPath.setInput(path);
    }
}
