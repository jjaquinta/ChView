package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.admin.rcp.sys.ui.schema.StarSchemaUIController;
import jo.d2k.data.data.StarFilter;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class FilterPanel extends Composite
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
    
    public FilterPanel(Composite parent, int style)
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
        mFilter.setSpectraO(mSpectraO.getSelection());
        mFilter.setSpectraB(mSpectraB.getSelection());
        mFilter.setSpectraA(mSpectraA.getSelection());
        mFilter.setSpectraF(mSpectraF.getSelection());
        mFilter.setSpectraG(mSpectraG.getSelection());
        mFilter.setSpectraK(mSpectraK.getSelection());
        mFilter.setSpectraM(mSpectraM.getSelection());
        mFilter.setSpectraL(mSpectraL.getSelection());
        mFilter.setSpectraT(mSpectraT.getSelection());
        mFilter.setSpectraY(mSpectraY.getSelection());
        if (mGenerated.getSelectionIndex() == 0)
            mFilter.setGenerated(null);        
        else if (mGenerated.getSelectionIndex() == 1)
            mFilter.setGenerated(true);
        else
            mFilter.setGenerated(false);
        mSchemaController.getMetadata(mFilter.getExtraFields());
        return mFilter;
    }

    public void setFilter(StarFilter filter)
    {
        mFilter = filter;
        mSpectraO.setSelection(mFilter.isSpectraO());
        mSpectraB.setSelection(mFilter.isSpectraB());
        mSpectraA.setSelection(mFilter.isSpectraA());
        mSpectraF.setSelection(mFilter.isSpectraF());
        mSpectraG.setSelection(mFilter.isSpectraG());
        mSpectraK.setSelection(mFilter.isSpectraK());
        mSpectraM.setSelection(mFilter.isSpectraM());
        mSpectraL.setSelection(mFilter.isSpectraL());
        mSpectraT.setSelection(mFilter.isSpectraT());
        mSpectraY.setSelection(mFilter.isSpectraY());
        if (mFilter.getGenerated() == null)
            mGenerated.select(0);
        else if (mFilter.getGenerated())
            mGenerated.select(1);
        else
            mGenerated.select(2);
        mSchemaController.setMetadata(mFilter.getExtraFields());
    }
}
