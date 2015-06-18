package jo.d2k.admin.rcp.viz.chview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.admin.rcp.sys.ui.schema.StarSchemaUIController;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.logic.StarColumnLogic;
import jo.d2k.data.logic.schema.TextSchemaComparator;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class SimpleFilterPanel extends Composite
{
    private StarFilter  mFilter;
    
    private Button mSpectraO;
    private Button mSpectraB;
    private Button mSpectraA;
    private Button mSpectraF;
    private Button mSpectraG;
    private Button mSpectraK;
    private Button mSpectraM;
    private Button mSpectraL;
    private Button mSpectraT;
    private Button mSpectraY;
    private Combo  mGenerated;
    private StarSchemaUIController  mSchemaController;
    
    public SimpleFilterPanel(Composite parent, int style)
    {
        super(parent, style);
        setLayout(new GridLayout(7, false));
        
        mSpectraO = GridUtils.makeCheck(this, "O Spectrum", "2x1");
        mSpectraB = GridUtils.makeCheck(this, "B Spectrum", "2x1");
        mSpectraA = GridUtils.makeCheck(this, "A Spectrum", "3x1");
        mSpectraF = GridUtils.makeCheck(this, "F Spectrum", "2x1");
        mSpectraG = GridUtils.makeCheck(this, "G Spectrum", "2x1");
        mSpectraK = GridUtils.makeCheck(this, "K Spectrum", "3x1");
        mSpectraM = GridUtils.makeCheck(this, "M Spectrum", "2x1");
        mSpectraL = GridUtils.makeCheck(this, "L Spectrum", "2x1");
        mSpectraT = GridUtils.makeCheck(this, "T Spectrum", "3x1");
        mSpectraY = GridUtils.makeCheck(this, "Y Spectrum", "2x1");
        GridUtils.makeLabel(this, "", "5x1");
        GridUtils.makeLabel(this, "Generated:", "");
        mGenerated = GridUtils.makeCombo(this, new String[] { "Don't Care", "Generated", "Not Generated" }, "6x1");
        mSchemaController = new StarSchemaUIController(this, (style&SWT.READ_ONLY));
    }
    
    public StarFilter getFilter()
    {
        List<FilterConditionBean> ors = mFilter.getConditions();
        ors.clear();
        addSpectraFilter(ors, mSpectraO.getSelection(), "O");
        addSpectraFilter(ors, mSpectraB.getSelection(), "B");
        addSpectraFilter(ors, mSpectraA.getSelection(), "A");
        addSpectraFilter(ors, mSpectraF.getSelection(), "F");
        addSpectraFilter(ors, mSpectraG.getSelection(), "G");
        addSpectraFilter(ors, mSpectraK.getSelection(), "K");
        addSpectraFilter(ors, mSpectraM.getSelection(), "M");
        addSpectraFilter(ors, mSpectraL.getSelection(), "L");
        addSpectraFilter(ors, mSpectraT.getSelection(), "T");
        addSpectraFilter(ors, mSpectraY.getSelection(), "Y");
        if (mGenerated.getSelectionIndex() > 0)
        {
            FilterConditionBean fc = new FilterConditionBean();
            fc.setID("generated");
            fc.setOption(TextSchemaComparator.EQUALS);
            fc.setArgument(String.valueOf((mGenerated.getSelectionIndex() == 1)));
            ors.add(fc);
        }
        Map<String,String> mdSettings = new HashMap<String, String>();
        mSchemaController.getMetadata(mdSettings);
        for (String extraID : mdSettings.keySet())
        {
            StarColumn col = StarColumnLogic.getColumn(extraID);
            FilterConditionBean fc = new FilterConditionBean();
            fc.setID(extraID);
            fc.setOption(col.getComparator().getDefaultOption());
            fc.setArgument(mdSettings.get(extraID));
            ors.add(fc);
        }
        mFilter.setAnd(false);
        return mFilter;
    }

    public void addSpectraFilter(List<FilterConditionBean> ors, boolean selected, String spectra)
    {
        if (selected)
        {
            FilterConditionBean fc = new FilterConditionBean();
            fc.setID("spectra");
            fc.setOption(TextSchemaComparator.CONTAINS);
            fc.setArgument(spectra);
            ors.add(fc);
        }
    }

    public void setFilter(StarFilter filter)
    {
        mFilter = filter;
        mSpectraO.setSelection(false);
        mSpectraB.setSelection(false);
        mSpectraA.setSelection(false);
        mSpectraF.setSelection(false);
        mSpectraG.setSelection(false);
        mSpectraK.setSelection(false);
        mSpectraM.setSelection(false);
        mSpectraL.setSelection(false);
        mSpectraT.setSelection(false);
        mSpectraY.setSelection(false);
        mGenerated.select(0);
        Map<String,String> mdSettings = new HashMap<String, String>();
        if (!mFilter.isAnd())
        {
            for (FilterConditionBean cond : mFilter.getConditions())
            {
                if ("spectra".equals(cond.getID()))
                {
                    if ("O".equals(cond.getArgument()))
                        mSpectraO.setSelection(true);
                    else if ("B".equals(cond.getArgument()))
                        mSpectraB.setSelection(true);
                    else if ("A".equals(cond.getArgument()))
                        mSpectraA.setSelection(true);
                    else if ("F".equals(cond.getArgument()))
                        mSpectraF.setSelection(true);
                    else if ("G".equals(cond.getArgument()))
                        mSpectraG.setSelection(true);
                    else if ("K".equals(cond.getArgument()))
                        mSpectraK.setSelection(true);
                    else if ("M".equals(cond.getArgument()))
                        mSpectraM.setSelection(true);
                    else if ("L".equals(cond.getArgument()))
                        mSpectraL.setSelection(true);
                    else if ("T".equals(cond.getArgument()))
                        mSpectraT.setSelection(true);
                    else if ("Y".equals(cond.getArgument()))
                        mSpectraY.setSelection(true);
                }
                else if ("generated".equals(cond.getID()))
                {
                    if ("true".equals(cond.getArgument()))
                        mGenerated.select(1);
                    else
                        mGenerated.select(2);
                }
                else
                {
                    StarColumn col = StarColumnLogic.getColumn(cond.getID());
                    if ((cond.getOption() == col.getComparator().getDefaultOption()) && (cond.getArgument() instanceof String))
                        mdSettings.put(cond.getID(), (String)cond.getArgument());
                }
            }
        }
        mSchemaController.setMetadata(mdSettings);
    }
}
