package jo.d2k.admin.rcp.viz.chview;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.StarRouteLogic;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.FormatUtils;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RoutePanel extends Composite
{
    private StarRouteBean           mRoute;
    private List<StarBean>          mLookups;

    private SimpleContentProposalProvider mProposals;
   
    private Text                    mStar1;
    private Button                  mStar1Lookup;
    private Text                    mStar2;
    private Button                  mStar2Lookup;
    private Text                    mDistance;
    private Button                  mType1;
    private Button                  mType2;
    private Button                  mType3;
    private Button                  mType4;
    private Button                  mType5;
    private Button                  mType6;
    private Button                  mType7;
    private Button                  mType8;
    
    public RoutePanel(Composite parent, int style)
    {
        super(parent, style);
        mLookups = new ArrayList<>();
        setLayout(new GridLayout(1, false));
        
        Composite upper = new Composite(this, SWT.NULL);
        GridUtils.setLayoutData(upper, "fill=h");
        upper.setLayout(new GridLayout(3, false));
        GridUtils.makeLabel(upper, "Point 1:", "");
        mStar1 = GridUtils.makeText(upper, "", "fill=h");
        mStar1Lookup = GridUtils.makeButton(upper, "...", "");
        GridUtils.makeLabel(upper, "Point 2:", "");
        mStar2 = GridUtils.makeText(upper, "", "fill=h");
        mStar2Lookup = GridUtils.makeButton(upper, "...", "");
        GridUtils.makeLabel(upper, "Distance:", "");
        mDistance = GridUtils.makeText(upper, SWT.READ_ONLY, "fill=h");
        
        Composite lower = new Composite(this, SWT.NULL);
        GridUtils.setLayoutData(lower, "fill=h");
        lower.setLayout(new GridLayout(4*3, false));
        mType1 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute1Name(), ChViewVisualizationLogic.mPreferences.getRoute1Color());
        mType2 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute2Name(), ChViewVisualizationLogic.mPreferences.getRoute2Color());
        mType3 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute3Name(), ChViewVisualizationLogic.mPreferences.getRoute3Color());
        mType4 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute4Name(), ChViewVisualizationLogic.mPreferences.getRoute4Color());
        mType5 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute5Name(), ChViewVisualizationLogic.mPreferences.getRoute5Color());
        mType6 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute6Name(), ChViewVisualizationLogic.mPreferences.getRoute6Color());
        mType7 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute7Name(), ChViewVisualizationLogic.mPreferences.getRoute7Color());
        mType8 = makeRadio(lower, ChViewVisualizationLogic.mPreferences.getRoute8Name(), ChViewVisualizationLogic.mPreferences.getRoute8Color());
        
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
        ContentProposalAdapter adapter1 = new ContentProposalAdapter(mStar1, new TextContentAdapter(),
                mProposals,ks,null);
        adapter1.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        adapter1.addContentProposalListener(new IContentProposalListener() {
            @Override
            public void proposalAccepted(IContentProposal ev)
            {
                updateDistance();
            }
        });
        ContentProposalAdapter adapter2 = new ContentProposalAdapter(mStar2, new TextContentAdapter(),
                mProposals,ks,null);
        adapter2.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        adapter2.addContentProposalListener(new IContentProposalListener() {
            @Override
            public void proposalAccepted(IContentProposal ev)
            {
                updateDistance();
            }
        });
        List<String> proposals = new ArrayList<>();
        if (ChViewVisualizationLogic.mPreferences.getStars() != null)
            for (StarBean star : ChViewVisualizationLogic.mPreferences.getStars())
            {
                proposals.add(ChViewRenderLogic.getStarName(star));
                mLookups.add(star);
            }
        mProposals.setProposals(proposals.toArray(new String[0]));
    }
    
    private Button makeRadio(Composite parent, String name, Color c)
    {
        Button radio = GridUtils.makeRadio(parent, "", "");
        Label swatch = GridUtils.makeLabel(parent, "", "size=16x16");
        swatch.setBackground(c);
        GridUtils.makeLabel(parent, name, "align=nw fill=h");
        return radio;
    }
    
    private void doLookup(int which)
    {
        DlgSearch dlg = new DlgSearch(getShell());
        if (which == 1)
            dlg.setPattern(mStar1.getText());
        else
            dlg.setPattern(mStar2.getText());
        if (dlg.open() != Dialog.OK)
            return;
        StarBean star = dlg.getStar();
        if (star == null)
            return;
        if (which == 1)
            mStar1.setText(ChViewRenderLogic.getStarName(star));
        else
            mStar2.setText(ChViewRenderLogic.getStarName(star));
        mLookups.add(star);
        updateDistance();
    }
    
    private void updateDistance()
    {
        StarBean star1 = findStar(mStar1.getText());
        if (star1 == null)
        {
            mDistance.setText("");
            return;
        }
        StarBean star2 = findStar(mStar2.getText());
        if (star2 == null)
        {
            mDistance.setText("");
            return;
        }
        mDistance.setText(FormatUtils.formatDouble(StarExtraLogic.distance(star1, star2), 1));
    }

    public StarRouteBean getRoute()
    {
        StarBean star1 = findStar(mStar1.getText());
        if (star1 == null)
            return null;
        mRoute.setStar1(star1.getOID());
        mRoute.setStar1Ref(star1);
        mRoute.setStar1Quad(star1.getQuadrant());
        StarBean star2 = findStar(mStar2.getText());
        if (star2 == null)
            return null;
        mRoute.setStar2(star2.getOID());
        mRoute.setStar2Ref(star2);
        mRoute.setStar2Quad(star2.getQuadrant());
        if (mType1.getSelection())
            mRoute.setType(0);
        else if (mType2.getSelection())
            mRoute.setType(1);
        else if (mType3.getSelection())
            mRoute.setType(2);
        else if (mType4.getSelection())
            mRoute.setType(3);
        else if (mType5.getSelection())
            mRoute.setType(4);
        else if (mType6.getSelection())
            mRoute.setType(5);
        else if (mType7.getSelection())
            mRoute.setType(6);
        else if (mType8.getSelection())
            mRoute.setType(7);
        return mRoute;
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

    public void setRoute(StarRouteBean route)
    {
        mRoute = route;
        StarRouteLogic.getReferences(mRoute);
        if (mRoute.getStar1Ref() != null)
            mStar1.setText(ChViewRenderLogic.getStarName(mRoute.getStar1Ref()));
        if (mRoute.getStar2Ref() != null)
            mStar2.setText(ChViewRenderLogic.getStarName(mRoute.getStar2Ref()));
        switch (mRoute.getType())
        {
            case 0: mType1.setSelection(true); break;
            case 1: mType2.setSelection(true); break;
            case 2: mType3.setSelection(true); break;
            case 3: mType4.setSelection(true); break;
            case 4: mType5.setSelection(true); break;
            case 5: mType6.setSelection(true); break;
            case 6: mType7.setSelection(true); break;
            case 7: mType8.setSelection(true); break;
        }
        updateDistance();
    }
}
